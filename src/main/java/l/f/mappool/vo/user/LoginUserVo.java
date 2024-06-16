package l.f.mappool.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginUserVo {
    String name;
    String token;
    Long uid;
    boolean admin;
}
