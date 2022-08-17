package dbdump.common.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private final DbPropBundle prop = new DbPropBundle();

    /**
     * MEMO: DataSourceをDI対象から除去することでSpringBoot標準テーブルの作成を回避.<br/>
     * 独自のデータソースを作成する
     * @throws SQLException 
     */
    public DataSource driverManagerDataSource(){
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName(prop.getDriverClassName());
        driverManagerDataSource.setUrl(prop.getUrl());
        driverManagerDataSource.setUsername(prop.getUsername());
        driverManagerDataSource.setPassword(prop.getPassword());
        // jdbcTemplateではデフォルトでコネクションをクローズしているので、poolしても意味がない
//        final Properties connectionProperties = new Properties();
//        try {
//			driverManagerDataSource.getConnection().setAutoCommit(false);
//		} catch (SQLException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
//        connectionProperties.setProperty("initialSize", "1");
//        connectionProperties.setProperty("maxActive", "1");
//        connectionProperties.setProperty("minIdle", "1");
//        driverManagerDataSource.setConnectionProperties(connectionProperties);
        return driverManagerDataSource;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSourceTransactionManager().getDataSource());
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSourceTransactionManager().getDataSource());
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager() {
        return new DataSourceTransactionManager(driverManagerDataSource());
    }
}
