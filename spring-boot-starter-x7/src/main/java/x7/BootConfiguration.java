package x7;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import x7.config.ConfigProperties;
import x7.core.bean.SpringHelper;
import x7.repository.RepositoryProperties;
import x7.repository.RepositoryProperties_W;

@Configuration
@EnableConfigurationProperties({ConfigProperties.class,RepositoryProperties.class,RepositoryProperties_W.class})
public class BootConfiguration {

	@Autowired
	private ConfigProperties configProperies;
	@Autowired
	private RepositoryProperties repositoryProperties;
	@Autowired
	private RepositoryProperties_W repositoryProperties_W;

	@Bean
	SpringHelper getHelper(){
		return new SpringHelper();
	}

	@Bean
    ConfigStarter x7ConfigStarter (){
		
        return  new ConfigStarter(configProperies.isCentralized(),configProperies.getSpace(), configProperies.getLocalAddress(), configProperies.getRemoteAddress());
    }

	@Bean
	RepositoryStarter x7RepsositoryStarter(){

		return new RepositoryStarter(repositoryProperties.getIsRemote());
	}
	
}
