package ibf2022.tfipminiproject.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class MySqlConfig {

    @Value("${MYSQL_HOST}")
    private String host;

    @Value("${MYSQL_PORT}")
    private String port;

    @Value("${MYSQL_USER}")
    private String username;

    @Value("${MYSQL_PASSWORD}")
    private String password;

    @Value("${MYSQL_DATABASE}")
    private String database;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
