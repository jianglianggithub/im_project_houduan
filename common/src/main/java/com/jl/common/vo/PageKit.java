package com.jl.common.vo;

 import lombok.Data;

/**
 * 分页参数对象
 *
 * @author JinLongXu
 */
@Data
public class PageKit {

    /**
     * 页数
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer limit = 1000;

    public Integer getPage() {
        return page < 1 ? 1 : page;
    }

    public Integer getLimit() {
        return limit;
    }
}
