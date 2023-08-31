package l.f.mappool.dto.map;

import l.f.mappool.dto.validator.AddUser;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PoolUserDto {
    @NotNull(message = "图池不能为空", groups = {AddUser.class})
    Integer poolId;
    @NotNull(message = "用户不能为空", groups = {AddUser.class})
    Long userId;
}
