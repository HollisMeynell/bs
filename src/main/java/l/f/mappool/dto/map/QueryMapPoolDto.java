package l.f.mappool.dto.map;

import l.f.mappool.dto.BasePageReqListDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

@Data
@EqualsAndHashCode(callSuper=false)
public class QueryMapPoolDto extends BasePageReqListDto {
    @Nullable
    String poolName;
    @Nullable
    Integer poolId;
    boolean queryOther = false;
}
