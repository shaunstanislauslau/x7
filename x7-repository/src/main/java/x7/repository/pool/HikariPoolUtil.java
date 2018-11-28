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
package x7.repository.pool;

import com.zaxxer.hikari.HikariDataSource;
import x7.core.config.Configs;
import x7.core.util.StringUtil;
import x7.repository.exception.PersistenceException;
import x7.repository.sharding.ShardingPolicy;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
x7.db.sharding.database=0,1 <br>
x7.db.driver=com.mysql.jdbc.Driver<br>
x7.db.url=jdbc:mysql://${address}/${name}?characterEncoding=utf8<br>
x7.db.name=dyt<br>
x7.db.user=root<br>
x7.db.password=dyt123456x!<br>
x7.db.max=200<br>
x7.db.min=40<br>
x7.db.address.w=127.0.0.1:4408,127.0.0.1:4409<br>
x7.db.read=0<br>
x7.db.address.r=127.0.0.1:3306<br>
 * @author Sim
 *
 */
public class HikariPoolUtil {


	public static  HikariDataSource create(boolean isWriteable) {

		HikariDataSource dataSource = new HikariDataSource();


		String address = Configs.getString("x7.db.address.r");
		if (isWriteable){
			address = Configs.getString("x7.db.address.w");
		}
		int num = Configs.getIntValue("x7.db.read");
		if (StringUtil.isNullOrEmpty(address))
			return null;
		if (!isWriteable && num < 1)
			return null;

		String url = Configs.getString("x7.db.url");
		url = url.replace("${address}", address)
				.replace("${name}", Configs.getString("x7.db.name"));

		System.out.println("\n_________x7.db.url: " + url);
		dataSource.setReadOnly(!isWriteable);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(Configs.getString("x7.db.username"));
		dataSource.setPassword(Configs.getString("x7.db.password"));
		dataSource.setConnectionTimeout(300000);
		dataSource.setIdleTimeout(600000);
		dataSource.setMaxLifetime(1800000);
		dataSource.setDriverClassName(Configs.getString("x7.db.driver"));
		dataSource.setMaximumPoolSize(Configs.getIntValue("x7.db.max"));

		return dataSource;
	}

}
