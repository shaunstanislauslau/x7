package x7;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import x7.config.ConfigProperties;
import x7.core.bean.SpringHelper;
import x7.core.config.Configs;
import x7.repository.RepositoryProperties;
import x7.repository.pool.HikariPoolUtil;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties({
        ConfigProperties.class,
        RepositoryProperties.class,
        DataSourceProperties.class,
        DataSourceProperties_R.class})
public class BootConfiguration {

    @Autowired
    private ConfigProperties configProperies;
    @Autowired
    private RepositoryProperties repositoryProperties;
    @Autowired
    private DataSourceProperties dataSourceProperties;
    @Autowired
    private DataSourceProperties_R dataSourceProperties_r;

    @Bean
    @Order(1)
    public SpringHelper getHelper() {
        return new SpringHelper();
    }


    private ConfigStarter x7ConfigStarter() {
        ConfigStarter configStarter = new ConfigStarter(configProperies.isCentralized(), configProperies.getSpace(), configProperies.getLocalAddress(), configProperies.getRemoteAddress());
        RepositoryStarter.isLocal(repositoryProperties.getIsRemote());

        return configStarter;
    }


    @Bean(name = "dataSource")
    @Qualifier("dataSource")
    @Primary
    @Order(2)
    public DataSource getDataSource() {

        x7ConfigStarter();

        DataSource dataSource = (DataSource)SpringHelper.getObject("dataSource");
        if (Objects.nonNull(dataSource))
            return dataSource;

        if (repositoryProperties.getIsRemote())
            return null;

        DataSource writeDataSource = getWriteDataSource();
        DataSource readDataSource = getReadDataSource();

        startX7Repsository(writeDataSource, readDataSource);

        return writeDataSource;
    }


    public DataSource getWriteDataSource(){

        Object key = Configs.get("x7.db");
        if (Objects.nonNull(key)) {
            DataSource ds = HikariPoolUtil.create(true);
            System.out.println("_________Writeable DataSource Created By X7 Config: " + ds);
            if (Objects.nonNull(ds))
                return ds;
        }


        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dataSourceProperties.getUrl());
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());

        System.out.println("_________springBootConfig: " + dataSourceProperties.getUrl());
        System.out.println("_________springBootConfig: " + dataSourceProperties.getUsername());
        System.out.println("_________springBootConfig: " + dataSourceProperties.getPassword());
        System.out.println("_________springBootConfig: " + dataSourceProperties.getDriverClassName());


        System.out.println("_________Writeable DataSource Created By SpringBoot Config: " + dataSource);

        return dataSource;
    }


    public HikariDataSource getReadDataSource() {

        System.out.println("\n_________Readable DataSource Creating....");

        HikariDataSource ds = null;

        Object key = Configs.get("x7.db");
        if (Objects.nonNull(key)) {
            ds = HikariPoolUtil.create(false);
        }

        if (Objects.nonNull(ds)) {
            System.out.println("_________Readable DataSource Created By X7 Config: " + ds);
            return ds;
        }

        if (Objects.isNull(dataSourceProperties_r.getUrl())) {
            System.out.println("_________Readable DataSource Config Key: x7(x7.db.address.r) or springBoot(spring.datasource.read.url)");
            System.out.println("_________Readable DataSource Config Value: null");
            System.out.println("_________Readable DataSource Ignored\n");

            return null;
        }

        String driverClassName = dataSourceProperties.determineDriverClassName();
        String username = dataSourceProperties.getUsername();
        String password = dataSourceProperties.getPassword();

        if (Objects.nonNull(dataSourceProperties_r.getDriverClassName())) {
            driverClassName = dataSourceProperties_r.getDriverClassName();
        }

        if (Objects.nonNull(dataSourceProperties_r.getUsername())) {
            username = dataSourceProperties_r.getUsername();
        }

        if (Objects.nonNull(dataSourceProperties_r.getPassword())) {
            password = dataSourceProperties_r.getPassword();
        }

        ds = new HikariDataSource();

        HikariDataSource dsR = (HikariDataSource) ds;
        dsR.setJdbcUrl(dataSourceProperties_r.getUrl());
        dsR.setUsername(username);
        dsR.setPassword(password);
        dsR.setDriverClassName(driverClassName);


        System.out.println("_________Readable DataSource Created By SpringBoot Config: " + ds);
        return ds;
    }


    public void startX7Repsository(DataSource dsW, DataSource dsR) {

        System.out.println("_________X7 Repsository Starter....");

        if (Objects.isNull(dsW))
            throw new RuntimeException("Writeable DataSource Got NULL");

        new RepositoryStarter(dsW, dsR, dataSourceProperties.getDriverClassName());

    }


}
