package idea_service;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import oracle.jdbc.pool.OracleDataSource;

@SpringBootApplication
public class App {

    public static void main(final String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    DataSource dataSource() throws SQLException {
        final OracleDataSource dataSource = new OracleDataSource();
        dataSource.setServerName("localhost");
        dataSource.setUser("SYSTEM");
        dataSource.setPassword("Go0b3r23Drowsap321");
        dataSource.setDatabaseName("orcl");
        dataSource.setPortNumber(1521);
        dataSource.setDriverType("thin");
        return dataSource;
    }
}
