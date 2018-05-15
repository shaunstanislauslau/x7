/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package x7.repository;


import x7.core.config.Configs;
import x7.repository.dao.AsyncDaoImpl;
import x7.repository.dao.DaoInitializer;
import x7.repository.pool.HikariPool;

/**
 * 
 * for Game develop
 * @author Sim
 *
 */
public class AsyncRepositoryBooter {
	
	private final static String MYSQL = "mysql";
	
	private final static String MongoDb = "mongodb";
	
	private final static String HBASE = "hbase";

	private static AsyncRepositoryBooter instance = null;
	
	
	public static void boot() {
		if (instance == null) {
			instance = new AsyncRepositoryBooter();
			init();
		}
	}
	
	private static void init(){
		
		String driver = Configs.getString("x7.db.driver");
		driver = driver.toLowerCase();

		if (driver.contains(DbType.MYSQL)){
			DbType.value = DbType.MYSQL;
		}
		
		switch (DbType.value){
		
		case MYSQL:
			HikariPool pool = HikariPool.getInstance();
			DaoInitializer.init(pool.get(), pool.getR());
			AsyncDaoImpl.getInstance().setDataSource(pool.get());
			break;
		
		}
		
	}
}
