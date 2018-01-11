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


import java.util.List;

import x7.core.config.Configs;
import x7.repository.BaseRepository.HealthChecker;
import x7.repository.dao.DaoInitializer;
import x7.repository.dao.DaoWrapper;
import x7.repository.dao.ShardingDaoImpl;
import x7.repository.pool.DataSourceFactory;
import x7.repository.pool.DataSourcePool;
import x7.repository.redis.CacheResolver;
import x7.repository.redis.JedisConnector_Persistence;


public class RepositoryBooter {

	
	private final static String MONGO_DB = "mongodb";
	
	private final static String TiDB = "tidb";
	
	private final static String LEVEL_DB = "leveldb";

	private static RepositoryBooter instance = null;
	
	
	
	public static void boot() {
		if (instance == null) {
			instance = new RepositoryBooter();
			init();
		}
	}
	
	public static void boot(String dataSourceType) {
		if (instance == null) {
			instance = new RepositoryBooter();
			init(dataSourceType);
			HealthChecker.onStarted();
		}
	}
	
	public static void generateId(){
		List<IdGenerator> idGeneratorList = Repositories.getInstance().list(IdGenerator.class);
		for (IdGenerator generator : idGeneratorList) {
			String name = generator.getClzName();
			long maxId = generator.getMaxId();
			
			String idInRedis = JedisConnector_Persistence.getInstance().hget(BaseRepository.ID_MAP_KEY, name);
			
			System.out.println(name + ",test, idInDB = " + maxId + ", idInRedis = " + idInRedis);
			

			if (idInRedis == null){
				JedisConnector_Persistence.getInstance().hset(BaseRepository.ID_MAP_KEY, name, String.valueOf(maxId));
			}else if (idInRedis != null && maxId > Long.valueOf(idInRedis)){
				JedisConnector_Persistence.getInstance().hset(BaseRepository.ID_MAP_KEY, name, String.valueOf(maxId));
			}
			
			System.out.println(name + ",final, idInRedis = " + JedisConnector_Persistence.getInstance().hget(BaseRepository.ID_MAP_KEY, name));

		
		}
	}
	
	private static void init(){
		init(null);
	}
	
	private static void init(String dataSourceType){
		
		String repository = Configs.getString(ConfigKey.REPOSITORY);
		repository = repository.toLowerCase();
		
		switch (repository){
		
		default:
			DataSourcePool pool = DataSourceFactory.get(dataSourceType);
			DaoInitializer.init(pool.get(), pool.getR());
			DaoInitializer.init(pool.getDsMapW(), pool.getDsMapR());
			Repositories.getInstance().setSyncDao(DaoWrapper.getInstance());
			Repositories.getInstance().setShardingDao(ShardingDaoImpl.getInstance());
			break;
		
		}

			
		
		if (Configs.isTrue(ConfigKey.IS_CACHEABLE)){
			Repositories.getInstance().setCacheResolver(CacheResolver.getInstance());
		}
	}
	
}
