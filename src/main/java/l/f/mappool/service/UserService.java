package l.f.mappool.service;

import jakarta.annotation.Resource;
import l.f.mappool.entity.osu.OsuUser;
import l.f.mappool.entity.User;
import l.f.mappool.repository.osu.OsuUserRepository;
import l.f.mappool.repository.UserRepository;
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

    public OsuUser doLogin(String code) {
        var user = osuApiService.getToken(code);
        user = osuApiService.getMeInfo(user);
        osuUserRepository.saveAndFlush(user);
        return user;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public OsuUser getOsuUser(long id) {
        var uOpt = osuUserRepository.findById(id);
        if (uOpt.isEmpty()) throw new RuntimeException("用户检索失败");
        return uOpt.get();
    }

    @SuppressWarnings("unused")
    public List<User> getAllLoginUser(long osuId) {
        return userRepository.findByOsuId(osuId);
    }

    public boolean loginCheck(User user) {
        return userRepository.countByOsuIdAndCode(user.getOsuId(), user.getCode()) > 0;
    }
}
