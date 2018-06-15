package x7;

import x7.core.config.Configs;
import x7.repository.RepositoryBooter;

import java.util.Objects;

public class RepositoryStarter {

	public RepositoryStarter(){

	}

	public RepositoryStarter(Boolean isRemote) {

		boolean remote = false;
		if (isRemote == null){
			try {
				remote = Configs.isTrue("x7.repository.local");
			}catch (Exception e){

			}
		}else{
			remote = isRemote;
		}
		System.out.println("_________Will start repository: " + !remote);
		if (!remote) {

			String dataSourceType = null;
			try{
				dataSourceType = Configs.getString("x7.repository.dataSourceType");
			}catch (Exception e){

			}
			Object key = Configs.get("x7.db");
			if (Objects.isNull(key))
				return;
			RepositoryBooter.boot(dataSourceType);
		}

	}
}
