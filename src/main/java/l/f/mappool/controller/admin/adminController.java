package l.f.mappool.controller.admin;

import l.f.mappool.config.interceptor.Open;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@ResponseBody
@SuppressWarnings("unused")
@RequestMapping(value = "/api/admin", produces = "application/json;charset=UTF-8")
@Open(admin = true)
public class adminController {
    @GetMapping()
    String name() {
        return "test ok";
    }
}
