package l.f.mappool.dto;

public class BasePageReqListDto {
    protected final static int SIZE=20;
    protected final static int NO=1;



    protected int pageNum=NO;
    protected int pageSize = SIZE;

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getStartIndex() {
        return (getPageNum() - 1) * this.getPageSize();
    }

    public int getEndIndex() {
        return getPageNum() * this.getPageSize();
    }


}
