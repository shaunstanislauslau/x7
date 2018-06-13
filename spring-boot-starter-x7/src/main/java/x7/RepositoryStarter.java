package x7;

import x7.core.config.Configs;
import x7.repository.RepositoryBooter;

public class RepositoryStarter {

	public RepositoryStarter(){

	}

	public RepositoryStarter(Boolean isRemote) {

		boolean remote = false;
		if (isRemote == null){
			remote = Configs.isTrue("x7.repository.local");
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
			RepositoryBooter.boot(dataSourceType);
		}

	}
}
