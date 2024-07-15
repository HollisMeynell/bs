package l.f.mappool.service;

import l.f.mappool.entity.LoginUser;
import l.f.mappool.entity.osu.OsuOauthUser;
import l.f.mappool.properties.BeatmapSelectionProperties;
import l.f.mappool.repository.UserRepository;
import l.f.mappool.repository.osu.OsuUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    OsuApiService osuApiService;

    OsuUserRepository osuUserRepository;

    UserRepository userRepository;

    BeatmapSelectionProperties properties;

    public OsuOauthUser doLogin(String code) {
        var user = osuApiService.getToken(code);
        var userInfo = osuApiService.getMeInfo(user);
        user.setName(userInfo.getUserName());
        user.setOsuId(userInfo.getId());
        osuUserRepository.saveAndFlush(user);
        return user;
    }

    public void saveUser(LoginUser loginUser) {
        userRepository.save(loginUser);
    }

    public OsuOauthUser getOsuUser(long id) {
        var uOpt = osuUserRepository.findById(id);
        if (uOpt.isEmpty()) throw new RuntimeException("用户检索失败");
        return uOpt.get();
    }

    @SuppressWarnings("unused")
    public List<LoginUser> getAllLoginUser(long osuId) {
        return userRepository.findByOsuId(osuId);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean loginCheck(LoginUser loginUser) {
        return userRepository.countByOsuIdAndCode(loginUser.getOsuId(), loginUser.getCode()) > 0;
    }
}
