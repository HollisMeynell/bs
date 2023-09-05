package l.f.mappool.dto;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class BasePageReqListDto {
    protected final static int SIZE=20;
    protected final static int NO=1;



    protected int pageNum=NO;
    protected int pageSize = SIZE;
    public int getStartIndex() {
        return (getPageNum() - 1) * this.getPageSize();
    }

    public int getEndIndex() {
        return getPageNum() * this.getPageSize();
    }


}
