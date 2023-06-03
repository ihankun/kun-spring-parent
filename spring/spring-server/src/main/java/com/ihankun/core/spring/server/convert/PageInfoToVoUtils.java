package com.ihankun.core.spring.server.convert;

import com.github.pagehelper.PageInfo;
import com.ihankun.core.base.api.BasePageVO;

public class PageInfoToVoUtils {
    /**
     * 转换
     *
     * @param pageInfo
     * @param <T>
     * @return
     */
    public static <T> BasePageVO<T> convert(PageInfo<T> pageInfo) {

        BasePageVO vo = new BasePageVO<>();

        vo.setTotal(pageInfo.getTotal());
        vo.setPageSize(pageInfo.getPageSize());
        vo.setPageNum(pageInfo.getPageNum());
        vo.setList(pageInfo.getList());

        return vo;
    }
}
