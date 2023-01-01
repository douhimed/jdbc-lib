package org.adex.jdbc.configuration;

import org.adex.utils.StringUtils;

public class MySqlDataSource extends DataSource {

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_BRIDGE = "jdbc:mysql:";

    public MySqlDataSource(AbstractBuilder builder) {
        url = String.format("%s//%s:%s/%s",
                MYSQL_BRIDGE,
                StringUtils.isBlank(builder.host) ? DEFAULT_HOST : builder.host,
                StringUtils.isBlank(builder.port) ? DEFAULT_POST : builder.port,
                builder.databaseName);
        userName = StringUtils.isBlank(builder.userName) ? DEFAULT_USERNAME : builder.userName;
        password = StringUtils.isBlank(builder.password) ? StringUtils.EMPTY : builder.password;
    }

    @Override
    String getDriver() {
        return MYSQL_DRIVER;
    }

    public static class Builder extends AbstractBuilder {


        public Builder(String databaseName) {
            super(databaseName);
        }

        @Override
        public DataSource reelBuild() {
            return new MySqlDataSource(this);
        }
    }

}
