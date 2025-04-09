package users_backlog;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mysql.cj.jdbc.MysqlDataSource;
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
        
        String environment = System.getenv("environment");
        if (environment != null && environment.equals("development")) {
            final MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser("root");
            dataSource.setPassword("Go0b3r@#Drowsap3@1");
            dataSource.setUrl("jdbc:mysql://localhost:3306/users-backlog?allowMultiQueries=true");
            return dataSource;
        } else {
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

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client() {
        RestHighLevelClient client;
        String environment = System.getenv("environment");
        if (environment != null && environment.equals("development")) {
            client = new RestHighLevelClient(
                RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
                )
                .setHttpClientConfigCallback(new HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder;
                    }
                })
            );
        } else {
            client = new RestHighLevelClient(
                RestClient.builder(
                    new HttpHost("0ad0a1a4418d4beca0ff5bf26ee9bf83.us-central1.gcp.cloud.es.io", 9243, "https")
                )
                .setHttpClientConfigCallback(new HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "UomEBszg80ddqaA2E3sCUWQa"));
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                })
            );
        }
        return client;
    }
}
