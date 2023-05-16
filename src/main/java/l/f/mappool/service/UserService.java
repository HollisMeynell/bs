package l.f.mappool.service;

import jakarta.annotation.Resource;
import l.f.mappool.entity.OsuUser;
import l.f.mappool.entity.User;
import l.f.mappool.repository.OsuUserRepository;
import l.f.mappool.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Resource
    OsuGetService osuGetService;
    @Resource
    OsuUserRepository osuUserRepository;
    @Resource
    UserRepository userRepository;

    public User doLogin(String code, String addr) {
        var user = osuGetService.getToken(code);
        user = osuGetService.getMeInfo(user);
        osuUserRepository.saveAndFlush(user);
        var uToken = new User();
        uToken.setCode(UUID.randomUUID().toString());
        uToken.setAddr(addr);
        uToken.setOsuId(user.getOsuId());
        userRepository.save(uToken);
        return uToken;
    }

    public OsuUser getOsuUser(long id) {
        var uOpt = osuUserRepository.findById(id);
        if (uOpt.isEmpty()) throw new RuntimeException("用户检索失败");
        return uOpt.get();
    }

    public List<User> getAllLoginUser(long osuId) {
        return userRepository.findByOsuId(osuId);
    }

    public boolean loginCheck(User user) {
        return userRepository.countByOsuIdAndCode(user.getOsuId(), user.getCode()) > 0;
    }
}
