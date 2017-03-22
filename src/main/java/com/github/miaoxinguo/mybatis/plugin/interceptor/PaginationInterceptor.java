package com.github.miaoxinguo.mybatis.plugin.interceptor;

import com.github.miaoxinguo.mybatis.plugin.PageableQo;
import com.github.miaoxinguo.mybatis.plugin.SqlUtils;
import com.github.miaoxinguo.mybatis.plugin.TotalCountHolder;
import com.github.miaoxinguo.mybatis.plugin.dialect.Dialect;
import com.github.miaoxinguo.mybatis.plugin.dialect.MySqlDialect;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
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

        ParameterHandler parameterHandler = handler.getParameterHandler();

        // 判断是分页sql, 当前实现只允许一个参数即PageableQo或其子类, 如需多个参数,这里要修改判断
        Object parameterObject = parameterHandler.getParameterObject();
        String sql = handler.getBoundSql().getSql();
        if (!this.isPagedSql(sql, parameterObject)) {
            return invocation.proceed();
        }

        // 获取count的sql, 查询 count
        String countSql = dialect.getCountSql(sql);
        Connection connection = (Connection) metaObject.getValue("executor.delegate.transaction.connection");

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
            logger.error("查询总记录数异常", e);
        } finally {
            // conn 由spring 事务管理, preparedStatement 和 resultSet 自己关闭
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
        }

        // 结果数为0， 直接返回
        if (count == 0) {
            return invocation.proceed();
        }
        TotalCountHolder.setTotalCount(count);

        // 组装分页 sql
        PageableQo qo = (PageableQo) parameterObject;
        String pagedSql = dialect.getPagedSql(sql, qo.getOffset(), qo.getLimit());

        // delegate 是定义在 RoutingStatementHandler 中的属性，实际的对象是真正执行方法的 StatementHandler
        metaObject.setValue("delegate.boundSql.sql", pagedSql);
        metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        return invocation.proceed();
    }

    /**
     * 根据参数和返回类型判断是否是分页sql.
     * <p>
     * 分页方法的参数必须是 PageableQo 的子类, 查询sql不能包含select count;
     */
    private boolean isPagedSql(String originalSql, Object parameterObject) {
        String sql = SqlUtils.getLineSql(originalSql).toLowerCase();
        if (sql.contains("select count")) {
            logger.debug("original sql contain 'select count', didn't build page sql");
            return false;
        }
        if (!(parameterObject instanceof PageableQo)) {
            logger.debug("查询参数对象不是 Pageable 或其子类, 不进行分页处理");
            return false;
        }
        return true;
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
