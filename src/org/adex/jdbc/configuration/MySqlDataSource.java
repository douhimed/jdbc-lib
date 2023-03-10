package org.adex.jdbc.configuration;

public class MySqlDataSource extends DataSource {

    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_BRIDGE = "jdbc:mysql:";

    private static final String URL_TEMPLATE = "%s//%s:%s/%s";

    public MySqlDataSource(AbstractBuilder builder) {
        super(builder);
    }

    @Override
    String getDriver() {
        return MYSQL_DRIVER;
    }

    @Override
    String getUrlTemplate() {
        return URL_TEMPLATE;
    }

    @Override
    String getBridge() {
        return MYSQL_BRIDGE;
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
