package x7;

import x7.core.config.Configs;
import x7.repository.RepositoryBooter;

import javax.sql.DataSource;

public class RepositoryStarter {

	public RepositoryStarter(){

	}

	public RepositoryStarter(DataSource ds_W, DataSource ds_R,String driverClassName) {



		RepositoryBooter.onDriver(driverClassName);
		RepositoryBooter.boot(ds_W,ds_R);



	}

	public static boolean isLocal(Boolean isRemote){
		System.out.println("\n_________Will start repository: " + !isRemote + (isRemote?"\n":""));
		return !isRemote;

	}
}
