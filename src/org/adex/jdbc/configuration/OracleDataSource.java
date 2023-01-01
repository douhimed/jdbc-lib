package org.adex.jdbc.configuration;

public class OracleDataSource extends DataSource {

    private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String ORACLE_BRIDGE = "jdbc:oracle:thin:";

    private static final String URL_TEMPLATE = "%s@%s:%s:%s";

    public OracleDataSource(AbstractBuilder builder) {
        super(builder);
    }

    @Override
    String getDriver() {
        return ORACLE_DRIVER;
    }

    @Override
    String getUrlTemplate() {
        return URL_TEMPLATE;
    }

    @Override
    String getBridge() {
        return ORACLE_BRIDGE;
    }

    public static class Builder extends AbstractBuilder {

        public Builder(String databaseName) {
            super(databaseName);
        }

        @Override
        public DataSource reelBuild() {
            return new OracleDataSource(this);
        }
    }
}
