package org.sun.flumeinaction.jdbcsink;

import com.google.common.base.Preconditions;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyVetoException;

/**
 * Created on 2018/2/3.
 */
public class JdbcSink extends AbstractSink implements Configurable {

    private static String hostname;
    private static String port;
    private static String database;
    private static String table;
    private static String user;
    private static String password;
    private static int batchSize;

    private ComboPooledDataSource dataSource;

    private static final Logger LOG = LoggerFactory.getLogger(JdbcSink.class);
    private static final String JDBC_DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final int DS_IDLE_Time = 60;
    private static final int DS_TEST_PERIOD = 60;
    private static final int DS_RETRY_COUNT = 20;

    @Override
    public void configure(Context context) {
        hostname = context.getString("hostname");
        Preconditions.checkNotNull(hostname, "hostname should be set");
        port = context.getString("port");
        Preconditions.checkNotNull(port, "port should be set");
        database = context.getString("database");
        Preconditions.checkNotNull(database, "database should be set");
        table = context.getString("table");
        Preconditions.checkNotNull(table, "table should be set");
        user = context.getString("user");
        Preconditions.checkNotNull(user, "user should be set");
        password = context.getString("password");
        Preconditions.checkNotNull(password, "password should be set");
        batchSize = context.getInteger("batchSize");
        Preconditions.checkArgument(batchSize > 0, "batchSize should bigger than 0");
    }


    @Override
    public void start() {
        init();
        super.start();
    }

    private void init() {
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;
        try {
            dataSource = createMysqlDataSource(url, user, password, JDBC_DRIVER_CLASS);
            LOG.debug("Create mysql datasource");
        } catch (PropertyVetoException e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    @Override
    public void stop() {
        preStop();
        super.stop();
    }

    private void preStop() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Override
    public Status process() throws EventDeliveryException {
        return null;
    }


    private ComboPooledDataSource createMysqlDataSource(String jdbcUrl, String user, String password,
                                                        String driverClass) throws PropertyVetoException {
        LOG.info("Create Data Source:" + jdbcUrl);
        ComboPooledDataSource gpDatasource = new ComboPooledDataSource();
        gpDatasource.setDriverClass(driverClass);
        gpDatasource.setJdbcUrl(jdbcUrl);
        gpDatasource.setUser(user);
        gpDatasource.setPassword(password);
        gpDatasource.setMinPoolSize(1);
        gpDatasource.setMaxPoolSize(1);
        gpDatasource.setInitialPoolSize(1);
        gpDatasource.setMaxIdleTime(DS_IDLE_Time);
        gpDatasource.setAcquireIncrement(1);
        gpDatasource.setMaxStatements(0);
        gpDatasource.setIdleConnectionTestPeriod(DS_TEST_PERIOD);
        gpDatasource.setAcquireRetryAttempts(DS_RETRY_COUNT);
        gpDatasource.setBreakAfterAcquireFailure(false);
        gpDatasource.setTestConnectionOnCheckout(true);
        return gpDatasource;
    }

    private void commit() {

    }

}
