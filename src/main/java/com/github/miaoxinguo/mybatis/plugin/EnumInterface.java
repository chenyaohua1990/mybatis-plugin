package com.github.miaoxinguo.mybatis.plugin;

/**
 * 涉及到 Mybatis 结果集映射的枚举必须实现此接口，目的是：.
 *
 * <p>Mybatis 自带的枚举类型处理器（TypeHandler）有两种，分别存储枚举名称或索引。
 * 但是实际上我们一般是存储自定义的code，使用统一接口只需要实现一个额外的 TypeHandler。</p>
 */
public interface EnumInterface {

    /**
     * 所有枚举必须有code属性，数据中存储的就是这个code值
     */
    int getCode();

}
