package x7;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import x7.config.ConfigProperties;
import x7.core.bean.SpringHelper;
import x7.core.config.Configs;
import x7.repository.RepositoryProperties;
import x7.repository.pool.DataSourceFactory;
import x7.repository.pool.DataSourcePool;

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
	public SpringHelper getHelper(){
		return new SpringHelper();
	}


    private void x7ConfigStarter (){
		new ConfigStarter(configProperies.isCentralized(),configProperies.getSpace(), configProperies.getLocalAddress(), configProperies.getRemoteAddress());
    }


	@Bean(name = "x7DataSourceWrite")
	@Qualifier("x7DataSourceWrite")
	@ConfigurationProperties(prefix="spring.datasource")
	@Primary
	@Order(2)
	public DataSource getDataSource(){

		x7ConfigStarter ();

		if (repositoryProperties.getIsRemote())
			return null;

		String dataSourceType = null;
		try{
			dataSourceType = Configs.getString("x7.repository.dataSourceType");
		}catch (Exception e){

		}

		Object key = Configs.get("x7.db");
		if (Objects.nonNull(key)){
			DataSourcePool pool = DataSourceFactory.get(dataSourceType);
			return pool.get();
		}

		DataSource ds =  DataSourceBuilder.create().build();
		if (ds instanceof HikariDataSource) {
			HikariDataSource dataSource = (HikariDataSource)ds;
			dataSource.setJdbcUrl(dataSourceProperties.getUrl());
			dataSource.setUsername(dataSourceProperties.getUsername());
			dataSource.setPassword(dataSourceProperties.getPassword());
			dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
		}
		return ds;
	}

	@Bean(name = "x7DataSourceRead")
	@Qualifier("x7DataSourceRead")
	@Order(3)
	public DataSource getReadDataSource(){

		if (repositoryProperties.getIsRemote())
			return null;

		String dataSourceType = null;
		try{
			dataSourceType = Configs.getString("x7.repository.dataSourceType");
		}catch (Exception e){

		}


		System.out.println("\n_________Readable DataSource Config Key: x7(x7.db.address.r) or springBoot(spring.datasource.read.url)");

		DataSource ds = null;

		Object key = Configs.get("x7.db");
		if (Objects.nonNull(key)){
			DataSourcePool pool = DataSourceFactory.get(dataSourceType);
			ds =  pool.getR();
		}

		if (Objects.nonNull(ds))
			return ds;

		if (Objects.isNull(dataSourceProperties_r.getUrl())) {
			System.out.println("_________Readable DataSource Config Value: null");
			System.out.println("_________Readable DataSource Ignored\n");
			return null;
		}

		String driverClassName = dataSourceProperties.determineDriverClassName();
		String username = dataSourceProperties.getUsername();
		String password = dataSourceProperties.getPassword();

		if (Objects.nonNull(dataSourceProperties_r.getDriverClassName())){
			driverClassName = dataSourceProperties_r.getDriverClassName();
		}

		if (Objects.nonNull(dataSourceProperties_r.getUsername())){
			username = dataSourceProperties_r.getUsername();
		}

		if (Objects.nonNull(dataSourceProperties_r.getPassword())){
			password = dataSourceProperties_r.getPassword();
		}

		ds =  DataSourceBuilder.create().build();
		if (ds instanceof HikariDataSource) {
			HikariDataSource dataSource = (HikariDataSource)ds;
			dataSource.setJdbcUrl(dataSourceProperties_r.getUrl());
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			dataSource.setDriverClassName(driverClassName);
		}
		return ds;
	}

	@Bean
	@Order(4)
	public RepositoryStarter x7RepsositoryStarter(){

		DataSource dsW = (DataSource)SpringHelper.getObject("x7DataSourceWrite");
		DataSource dsR = (DataSource)SpringHelper.getObject("x7DataSourceRead");

		return new RepositoryStarter(repositoryProperties.getIsRemote(),dsW,dsR,dataSourceProperties.getDriverClassName());
	}


}
