package dbdump.common.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class DbPropBundle {

    private String url;
    private String username;
    private String password;
    private String driverClassName;

    DbPropBundle() {
        ResourceBundle rb;
        try {
            File dir = Paths.get(System.getProperty("dbdump.dbpropdir")).toFile();
            URLClassLoader urlLoader = new URLClassLoader(new URL[]{dir.toURI().toURL()});
            rb = ResourceBundle.getBundle("application", Locale.getDefault(), urlLoader);
        } catch (MissingResourceException | MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        this.url = rb.getString("spring.datasource.url");
        this.username = rb.getString("spring.datasource.username");
        this.password = rb.getString("spring.datasource.password");
        this.driverClassName = rb.getString("spring.datasource.driver-class-name");
    }
}
