package com.github.miaoxinguo.mybatis.plugin.interceptor;

import com.github.miaoxinguo.mybatis.plugin.Page;
import com.github.miaoxinguo.mybatis.plugin.dialect.Dialect;
import com.github.miaoxinguo.mybatis.plugin.dialect.MySqlDialect;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 分页拦截器.
 *
 * <p>拦截 StatementHandler 的 prepare 方法， 组装 sql; 同时构造查询总数的sql</>
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PaginationInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(PaginationInterceptor.class);

    private Dialect dialect;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget(); // 默认是 RoutingStatementHandler

        // MetaObject 是 Mybatis提供的一个的工具类，通过它包装一个对象后可以获取或设置该对象的原本不可访问的属性（比如那些私有属性）
        MetaObject metaObject = MetaObject.forObject(handler,
                new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());

        // 组装分页 sql
        Object parameterObject = handler.getParameterHandler().getParameterObject();
        // TODO 判断是分页sql
        if (!this.isPagedSql(parameterObject)) {
            return invocation.proceed();
        }

        BoundSql boundSql = handler.getBoundSql();
        String sql = boundSql.getSql();

        // 获取count的sql, 查询 count
        Connection connection = (Connection) metaObject.getValue("executor.delegate.transaction.connection");
        ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("parameterHandler");

        String countSql = dialect.getCountSql(boundSql.getSql());

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = 0;
        try {
            preparedStatement = connection.prepareStatement(countSql);
            parameterHandler.setParameters(preparedStatement);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            count = (int) JdbcUtils.getResultSetValue(resultSet, 1, int.class);
        } catch (SQLException e) {
            logger.error("关闭资源异常", e);
        } finally {
            // TODO conn 、preparedStatement 是否需要关闭，对mybatis框架是否有影响
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
        }

        // 结果数为0， 直接返回
        if (count == 0) {
            return invocation.proceed();
        }

        // TODO 多个参数
        Page page = (Page) handler.getParameterHandler().getParameterObject();
        String pagedSql = dialect.getPagedSql(sql, page.getOffset(), page.getLimit());
        if ("".equals(pagedSql)) {
            return invocation.proceed();
        }

        // delegate 是定义在 RoutingStatementHandler 中的属性，实际的对象是真正执行方法的 StatementHandler
        metaObject.setValue("delegate.boundSql.sql", pagedSql);
        metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        return invocation.proceed();
    }


    /**
     * 根据参数和返回类型判断是否是分页sql.
     * <p>
     * 分页方法的参数必须是 Page 的子类, 返回类型必须是 Page;
     * 如果返回值是基本类型 或 Integer, 该方法应该是查询数量的方法，而不是分页方法
     */
    private boolean isPagedSql(Object parameterObject) {
        return parameterObject instanceof Page;
    }

    /**
     * 代理哪些类
     * <p>
     * 这个方法可以替代 Mybatis 的插件拦截方式，即实现一个全局拦截器拦截所有方法，
     * 然后在 plugin 方法里做判断， 对 target 为 StatementHandler 类型的参数执行 Plugin.warp
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler || target instanceof ResultSetHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    /**
     * 从 mybatis.conf 中取 <plugin> 标签下配置的属性
     */
    @Override
    public void setProperties(Properties properties) {
        logger.info("mybatis pagination plugin's properties: {}", properties);

        Dialect.Type dialectType = Dialect.Type.valueOf(properties.getProperty("dialect").toUpperCase());
        switch (dialectType) {
            case MYSQL:
                dialect = new MySqlDialect();
                break;
            default:
                throw new InvalidParameterException("'dialect' property is invalid.");
        }
    }
}
