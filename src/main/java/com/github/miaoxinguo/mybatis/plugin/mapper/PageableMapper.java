package com.github.miaoxinguo.mybatis.plugin.mapper;

import com.github.miaoxinguo.mybatis.plugin.PagingResult;
import com.github.miaoxinguo.mybatis.plugin.qo.PageableQo;

import java.io.Serializable;

/**
 * 分页查询接口
 */
public interface PageableMapper<T, PK extends Serializable, Q extends PageableQo> extends GenericMapper<T, PK> {

    /**
     * 根据 QueryObject 分页查询结果
     */
    PagingResult selectByPagableQo(Q qo);

}
