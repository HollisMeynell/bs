package l.f.mappool.controller;

import l.f.mappool.config.interceptor.Open;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Open
public class PageController {
    /**
     * 前端静态资源映射
     * 已弃用,到时候使用nginx处理前后端分离
     * @return 跳转目的
     */
    @GetMapping({"/home","/home/**", "/dev/**", "/", })
    public String forward() {
        return "/index.html";
    }
}
