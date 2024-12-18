package l.f.mappool.controller;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.entity.osu.OsuUserOptional;
import l.f.mappool.entity.osu.BeatMap;
import l.f.mappool.enums.OsuMod;
import l.f.mappool.enums.OsuMode;
import l.f.mappool.exception.ModsCatchException;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.service.DownloadOsuFileService;
import l.f.mappool.service.OsuApiService;
import l.f.mappool.service.OsuFileService;
import l.f.mappool.util.DataUtil;
import l.f.mappool.vo.yasunaori.YasunaoriBeatmapInfoVo;
import l.f.mappool.vo.yasunaori.YasunaoriUserInfoVo;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import rosu.Rosu;
import rosu.parameter.JniScore;

import java.io.IOException;

@Open
@Controller
@ResponseBody
@AllArgsConstructor
@RequestMapping("/api/yasunaori")
@CrossOrigin("https://a.yasunaori.be/")
public class YasunaoriApi {
    public static final String OSU_AVATAR_PREFIX = "https://a.ppy.sh/";
    public static String AVATAR_URL_PREFIX;
    public static String BACKGROUND_URL_PREFIX;

    @Nonnull
    private final OsuApiService          osuApiService;
    private final OsuFileService         osuFileService;
    private final DownloadOsuFileService downloadOsuFileService;

    @PostConstruct
    void init () {
        BACKGROUND_URL_PREFIX = BeatmapSelectionProperties.URL + "/api/file/map/bg/";
        AVATAR_URL_PREFIX = BeatmapSelectionProperties.URL + "/api/yasunaori/avatar/";
    }

    public static String getAvatarUrl(long uid) {
        return AVATAR_URL_PREFIX + Long.toHexString(uid);
    }

    private static String getOsuAvatarUrl(String key) {
        return OSU_AVATAR_PREFIX + Long.parseLong(key, 16);
    }

    @GetMapping("user")
    YasunaoriUserInfoVo getUser(
            @RequestParam(value = "uid", required = false)Long uid,
            @RequestParam(value = "name", required = false)String name,
            @RequestParam(value = "mode", required = false)String mode
            ) {
        var modeObj = OsuMode.getMode(mode);
        if (uid == null && name == null) {
            return new YasunaoriUserInfoVo("uid 与 name 不能同时为空.");
        }
        OsuUserOptional user;
        try {
            if (uid == null) {
                user = osuApiService.getUserInfo(name, modeObj);
            } else {
                user = osuApiService.getUserInfo(uid, modeObj);
            }
        } catch (WebClientResponseException.NotFound notFound) {
            return new YasunaoriUserInfoVo("用户不存在, 请检查输入.");
        }
        return new YasunaoriUserInfoVo(user);
    }

    @GetMapping("beatmap/{bid}")
    YasunaoriBeatmapInfoVo getBeatmap(
            @PathVariable("bid") long bid,
            @RequestParam(value = "mods", required = false) String mods,
            @RequestParam(value = "mode", required = false) String mode
    ) {
        int modsValue;
        OsuMode osuMode = OsuMode.getMode(mode);
        try {
            modsValue = Integer.parseInt(mods);
        } catch (NumberFormatException e) {
            try {
                modsValue = OsuMod.getModsValue(mods);
            } catch (ModsCatchException ex) {
                return new YasunaoriBeatmapInfoVo(ex.getMessage());
            }
        }
        BeatMap map;
        try {
            map = osuApiService.getMapInfo(bid);
        } catch (WebClientResponseException.NotFound notFound) {
            return new YasunaoriBeatmapInfoVo("谱面不存在, 请检查输入.");
        }
        if (OsuMod.hasChangeRating(modsValue)) {
            byte[] fileData;
            try {
                fileData = osuFileService.getOsuFile(map.getId(), DownloadOsuFileService.Type.FILE);
            } catch (IOException e) {
                return new YasunaoriBeatmapInfoVo("计算出错, 请稍后尝试或者联系管理员.");
            }
            var score =  new JniScore();
            if (osuMode != OsuMode.DEFAULT) score.setMode(osuMode.toMode());
            score.setMods(modsValue);
            var result = Rosu.calculate(fileData, score);
            map.setDifficulty((float) result.getStar());
            DataUtil.applyBeatMapChanges(map, modsValue);
        }
        return new YasunaoriBeatmapInfoVo(map);
    }

    @GetMapping("avatar/{file}")
    ResponseEntity<byte[]> avatar(@PathVariable("file") String key) {
        var data = downloadOsuFileService.downloadAvatar(getOsuAvatarUrl(key));
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(data);
    }
}
