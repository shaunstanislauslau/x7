package x7;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//@Configuration
public class DataSourceConfig {

    @Bean(name = "readDataSource")
    @Qualifier("readDataSource")
    @ConfigurationProperties(prefix="spring.datasource.read")
    public DataSource getReadDataSource(){
        DataSource ds_R =  DataSourceBuilder.create().build();
        System.out.println("________ds_R: " + ds_R);
        return ds_R;
    }

}