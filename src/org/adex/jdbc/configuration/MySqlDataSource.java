package org.adex.jdbc.configuration;

import org.adex.utils.StringUtils;

public class MySqlDataSource extends DataSource {

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_BRIDGE = "jdbc:mysql:";

    public MySqlDataSource(Builder builder) {
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

    public static class Builder {

        private final String databaseName;
        private String host;
        private String port;
        private String userName;
        private String password;

        public Builder(String databaseName) {
            this.databaseName = databaseName;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = String.valueOf(port);
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public DataSource build() {
            return new MySqlDataSource(this);
        }
    }

}
