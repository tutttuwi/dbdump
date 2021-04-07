package dbdump.common.config;

import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

/**
 * DataSource管理用バンドルクラス. <br/>
 * SpringBatch標準の管理テーブルの使用を避けたので本クラスでコネクション管理
 *
 * @author Tomo
 *
 */
@Configuration
@Getter
public class DbPropBundle {

    private String url;
    private String username;
    private String password;
    private String driverClassName;

    DbPropBundle() {
        ResourceBundle rb = ResourceBundle.getBundle("application", Locale.getDefault());
        this.url = rb.getString("spring.datasource.url");
        this.username = rb.getString("spring.datasource.username");
        this.password = rb.getString("spring.datasource.password");
        this.driverClassName = rb.getString("spring.datasource.driver-class-name");
    }
}
