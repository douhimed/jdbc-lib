package org.adex.jdbc.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

public abstract class DataSource {

    protected static final String DEFAULT_HOST = "localhost";
    protected static final String DEFAULT_POST = "3306";
    protected static final String DEFAULT_USERNAME = "root";

    private static Connection connection;

    protected String userName;

    protected String password;

    protected String url;

    abstract String getDriver();

    public Connection getConnection() {
        if (Objects.isNull(connection)) {
            synchronized (this) {
                if (Objects.isNull(connection)) {
                    try {
                        Class.forName(getDriver());
                        connection = DriverManager.getConnection(url, userName, password);
                    } catch (Exception e) {
                        System.out.println("Went bad in connection : " + e.getMessage());
                    }
                }
            }
        }
        return connection;
    }

    public abstract static class AbstractBuilder {

        protected final String databaseName;
        protected String host;
        protected String port;
        protected String userName;
        protected String password;

        public AbstractBuilder(String databaseName) {
            this.databaseName = databaseName;
        }

        public AbstractBuilder host(String host) {
            this.host = host;
            return this;
        }

        public AbstractBuilder port(int port) {
            this.port = String.valueOf(port);
            return this;
        }

        public AbstractBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public AbstractBuilder password(String password) {
            this.password = password;
            return this;
        }

        public DataSource build() {
            return this.reelBuild();
        }

        protected abstract DataSource reelBuild();
    }

}
