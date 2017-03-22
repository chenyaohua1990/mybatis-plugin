package com.github.miaoxinguo.mybatis.plugin.mapper;

import com.github.miaoxinguo.mybatis.plugin.PageableQo;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询接口
 */
public interface PageableMapper<T, PK extends Serializable> extends BaseMapper<T, PK> {

    /**
     * 根据 QueryObject 分页查询结果
     */
    List<T> selectByPageableQo(PageableQo qo);

}