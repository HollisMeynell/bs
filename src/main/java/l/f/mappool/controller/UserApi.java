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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/user")
public class UserApi {
    @Resource
    UserService userService;

    @Open
    @GetMapping("login")
    Object login(@Nullable @RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        User u = userService.doLogin(code, request.getHeader("User-Agent"));
        String token = JwtUtil.createToken(u);
        response.addCookie(new Cookie("TOKEN", token));
        return new DataVo().setData(token).setMessage("登陆成功");
    }

}
