package l.f.mappool.dto.map;

import l.f.mappool.dto.BasePageReqListDto;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class QueryMapPoolDto extends BasePageReqListDto {
    @Nullable
    String poolName;
    @Nullable
    Integer poolId;
    boolean queryOther = false;
}
