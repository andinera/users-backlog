package users_backlog;

import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mysql.cj.jdbc.MysqlDataSource;
// import com.zaxxer.hikari.HikariConfig;
// import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
public class App extends SpringBootServletInitializer {
// public class App {

    private static final Logger log = Logger.getLogger(App.class.getName());

    public static void main(final String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://usersbacklog.com", "http://localhost:4200");
            }
        };
    }

    @Bean
    DataSource dataSource() throws SQLException {

        log.info("######################");
        log.info(System.getenv("LOCAL"));
        log.info(System.getenv("local"));
        log.info(System.getProperty("LOCAL"));
        log.info(System.getProperty("local"));
        
        final MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("Go0b3r@#Drowsap3@1");
        dataSource.setDatabaseName("iequals");
        return dataSource;


        // The configuration object specifies behaviors for the connection pool.
        // HikariConfig config = new HikariConfig();

        // Configure which instance and what database user to connect with.
        // config.setJdbcUrl("jdbc:mysql://35.232.87.30/users-backlog");
        // config.setUsername("root");
        // config.setPassword("MyE8wEakfNoFtlky");
        // config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
        // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for details.
        // config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        // config.addDataSourceProperty("cloudSqlInstance", "users-backlog:us-central1:users-backlog");

        // ... Specify additional connection properties here.
        // ...

        // Initialize the connection pool using the configuration object.
        // DataSource pool = new HikariDataSource(config);
        // return pool;
    }
}
