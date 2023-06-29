package l.f.mappool.controller;

import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import l.f.mappool.config.interceptor.Open;
import l.f.mappool.entity.User;
import l.f.mappool.service.UserService;
import l.f.mappool.util.JwtUtil;
import l.f.mappool.vo.DataVo;
import l.f.mappool.vo.user.LoginUserVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Controller
@CrossOrigin
@ResponseBody
@RequestMapping("/api/user")
public class UserApi {
    @Resource
    UserService userService;

    @Open
    @GetMapping("login")
    DataVo<LoginUserVo> login(@NotNull @RequestParam("code") String code, HttpServletRequest request) {

        var user = userService.doLogin(code);
        long uid = user.getOsuId();
        var uToken = new User();
        uToken.setCode(UUID.randomUUID().toString());
        uToken.setOsuId(uid);
        uToken.setAddr(request.getHeader("User-Agent"));
        String token = JwtUtil.createToken(uToken);
        userService.saveUser(uToken);

        return new DataVo<LoginUserVo>()
                .setData(new LoginUserVo()
                        .setName(user.getName())
                        .setUid(uid)
                        .setToken(token))
                .setMessage("登陆成功");
    }

}
