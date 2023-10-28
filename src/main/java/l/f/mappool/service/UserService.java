package l.f.mappool.service;

import jakarta.annotation.Resource;
import l.f.mappool.entity.LoginUser;
import l.f.mappool.entity.osu.OsuUser;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.UserRepository;
import l.f.mappool.repository.osu.OsuUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Resource
    OsuApiService osuApiService;
    @Resource
    OsuUserRepository osuUserRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    BeatmapSelectionProperties properties;

    public OsuUser doLogin(String code) {
        var user = osuApiService.getToken(code);
        user = osuApiService.getMeInfo(user);
        osuUserRepository.saveAndFlush(user);
        return user;
    }

    public void saveUser(LoginUser loginUser) {
        userRepository.save(loginUser);
    }

    public OsuUser getOsuUser(long id) {
        var uOpt = osuUserRepository.findById(id);
        if (uOpt.isEmpty()) throw new RuntimeException("用户检索失败");
        return uOpt.get();
    }

    @SuppressWarnings("unused")
    public List<LoginUser> getAllLoginUser(long osuId) {
        return userRepository.findByOsuId(osuId);
    }

    public boolean loginCheck(LoginUser loginUser) {
        return userRepository.countByOsuIdAndCode(loginUser.getOsuId(), loginUser.getCode()) > 0;
    }
}
