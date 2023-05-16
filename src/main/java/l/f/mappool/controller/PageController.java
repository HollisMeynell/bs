package l.f.mappool.controller;

import l.f.mappool.config.interceptor.Open;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Open
public class PageController {
    @GetMapping({"/home","/home/**", "/dev/**", "/", })
    public String forward() {
        return "/index.html";
    }
}
