package com.github.miaoxinguo.mybatis.plugin.qo;

import com.github.miaoxinguo.mybatis.plugin.PageableQo;

/**
 * User 查询对象
 */
public class UserQo extends PageableQo {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
