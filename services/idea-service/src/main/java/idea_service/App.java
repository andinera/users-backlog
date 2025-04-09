package idea_service;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// import oracle.jdbc.pool.OracleDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

@SpringBootApplication
public class App {

    public static void main(final String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    DataSource dataSource() throws SQLException {
        final MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("Go0b3r@#Drowsap3@1");
        dataSource.setDatabaseName("iequals");
        return dataSource;
    }
}
