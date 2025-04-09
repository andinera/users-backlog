package users_backlog;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@SpringBootApplication
public class App extends SpringBootServletInitializer {

    public static void main(final String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }

    @Bean
    DataSource dataSource() throws SQLException {
        // The configuration object specifies behaviors for the connection pool.
        HikariConfig config = new HikariConfig();

        // Configure which instance and what database user to connect with.
        config.setJdbcUrl("jdbc:mysql://35.232.87.30/users-backlog");
        config.setUsername("root");
        config.setPassword("MyE8wEakfNoFtlky");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // For Java users, the Cloud SQL JDBC Socket Factory can provide authenticated connections.
        // See https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory for details.
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", "users-backlog:us-central1:users-backlog");

        // ... Specify additional connection properties here.
        // ...

        // Initialize the connection pool using the configuration object.
        DataSource pool = new HikariDataSource(config);
        return pool;
    }
}
