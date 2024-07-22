package l.f.mappool.util;

import l.f.mappool.entity.osu.BeatMap;
import l.f.mappool.enums.OsuMod;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class DataUtil {
    private static int AR2MS(float ar){
        if (0 < ar - 5){
            if (ar > 11) return 300;
            return  1200 - (int) (150 * (ar - 5));
        } else {
            return  1800 - (int) (120 * ar);
        }
    }

    private static float MS2AR(int ms){
        if (0 < 1200 - ms){
            if (ms < 300) return 11;
            return  5 + (1200 - ms) / 150f;
        } else {
            if (ms >= 2400) return -5;
            return  (1800 - ms) / 120f;
        }
    }

    @SuppressWarnings("lossy-conversions")
    public static float AR(float ar, int mod){
        int ms;
//      1800  -  1200  -  450  -  300
        if (OsuMod.hasHr(mod)){
            ar *= 1.4f;
        } else if (OsuMod.hasEz(mod)) {
            ar /= 2;
        }
        ar = Math.min(10f, ar);
        ms = AR2MS(ar);
        if (OsuMod.hasDt(mod)){
            ms /= (3d/2);
        } else if (OsuMod.hasHt(mod)) {
            ms /= (3d/4);
        }
        ar = MS2AR(ms);
        ar = Math.min(11f, ar);
        return roundTwoDecimals(ar);
    }

    private static float OD2MS(float od){
        return 80 - (6 * od);
    }

    private static float MS2OD(float ms){
        return (80 - ms) / 6f;
    }

    @SuppressWarnings("lossy-conversions")
    public static float OD(float od, int mod){
        float ms;
        if (OsuMod.hasHr(mod)){
            od *= 1.4f;
        } else if (OsuMod.hasEz(mod)) {
            od /= 2f;
        }
        od = Math.min(10f, od);
        ms = OD2MS(od);
        if (OsuMod.hasDt(mod)){
            ms /= (3d/2);
        } else if (OsuMod.hasHt(mod)) {
            ms /= (3d/4);
        }
        return roundTwoDecimals(MS2OD(ms));
    }


    public static float CS(float cs, int mod){
        if (OsuMod.hasHr(mod)){
            cs *= 1.3f;
        } else if (OsuMod.hasEz(mod)) {
            cs /= 2f;
        }
        return roundTwoDecimals(cs);
    }
    public static float HP(float hp, int mod){
        if (OsuMod.hasHr(mod)){
            hp *= 1.3f;
        } else if (OsuMod.hasEz(mod)) {
            hp /= 1.3f;
        }
        return roundTwoDecimals(hp);
    }

    public static float BPM(float bpm, int mod){
        if (OsuMod.hasDt(mod)){
            bpm *= 1.5f;
        } else if (OsuMod.hasHt(mod)) {
            bpm *= 0.75f;
        }
        return roundTwoDecimals(bpm);
    }

    public static int Length(float length, int mod){
        if (OsuMod.hasDt(mod)){
            length /= 1.5f;
        } else if (OsuMod.hasHt(mod)) {
            length /= 0.75f;
        }
        return Math.round(length);
    }

    @NotNull
    public static String getFileMd5(Path file) {
        if (!Files.exists(file)) {
            return "";
        }
        try {
            long size = Files.size(file);
            if (size < 512000) {
                var all = Files.readAllBytes(file);
                return DigestUtils.md5DigestAsHex(all);
            }

            try (var input = Files.newInputStream(file)) {
                return DigestUtils.md5DigestAsHex(input);
            }
        } catch (IOException e) {
            return "";
        }
    }

    private static float roundTwoDecimals(float value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }

    public static void applyBeatMapChanges(BeatMap beatMap, int mods) {
        if (Objects.isNull(beatMap)) return;
        if (!OsuMod.hasChangeRating(mods)) {
            return;
        }
        if (Objects.nonNull(beatMap.getBeatMapSet())) {
            var set = beatMap.getBeatMapSet();
            set.setBpm(DataUtil.BPM(set.getBpm(), mods));
        }
        beatMap.setAr(DataUtil.AR(Optional.ofNullable(beatMap.getAr()).orElse(0f), mods));
        beatMap.setCs(DataUtil.CS(Optional.ofNullable(beatMap.getCs()).orElse(0f), mods));
        beatMap.setOd(DataUtil.OD(Optional.ofNullable(beatMap.getOd()).orElse(0f), mods));
        beatMap.setHp(DataUtil.HP(Optional.ofNullable(beatMap.getHp()).orElse(0f), mods));
        beatMap.setTotalLength(DataUtil.Length(beatMap.getTotalLength(), mods));
    }
}
