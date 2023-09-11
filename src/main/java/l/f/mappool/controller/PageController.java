package l.f.mappool.controller;

import l.f.mappool.config.interceptor.Open;
import org.springframework.stereotype.Controller;

@Open
@Controller
@SuppressWarnings("all")
public class PageController {
    /**
     * 前端静态资源映射
     * 已弃用,到时候使用nginx处理前后端分离
     * @return 跳转目的
     */
//    @GetMapping(value = {"/{x:^(?!index\\.html$|assets$|ws$).*}", "/{x:^(?!index\\.html$|assets$).*}/**", "/", })
    public String forward(String x) {
        return "/index.html";
    }
}
