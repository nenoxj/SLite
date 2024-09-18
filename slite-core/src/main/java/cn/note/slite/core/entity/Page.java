package cn.note.slite.core.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * 分页
 */
@Getter
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -5395997221963176643L;
    private List<T> list;                // list result of this page
    private int currentPage;                // page number
    private int pageSize;                // result amount of this page
    private long totalPage;                // total page
    private long totalCount;                // total row

    public Page(List<T> list, long totalCount, int currentPage, int pageSize) {
        this.list = list;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        long totalPage = totalCount / pageSize;
        if (totalCount % pageSize != 0) {
            totalPage++;
        }
        this.totalPage = totalPage;
    }

}

