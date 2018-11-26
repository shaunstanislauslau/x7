package x7;

import x7.core.config.Configs;
import x7.repository.RepositoryBooter;

import javax.sql.DataSource;

public class RepositoryStarter {

	public RepositoryStarter(){

	}

	public RepositoryStarter(Boolean isRemote,DataSource ds_W, DataSource ds_R,String driverClassName) {

		boolean remote = false;
		if (isRemote == null){
			try {
				remote = Configs.isTrue("x7.repository.local");
			}catch (Exception e){

			}
		}else{
			remote = isRemote;
		}
		System.out.println("_________Will start repository: " + !remote + "\n");
		if (!remote) {

			RepositoryBooter.onDriver(driverClassName);

			RepositoryBooter.boot(ds_W,ds_R);

		}

	}
}
