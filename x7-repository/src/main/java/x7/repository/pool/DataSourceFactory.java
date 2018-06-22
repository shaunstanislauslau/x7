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

import org.springframework.stereotype.Component;
import x7.core.util.StringUtil;
import x7.repository.exception.PersistenceException;

@Component
public class DataSourceFactory {
	
	public static DataSourcePool get(String dataSourceType) {
		if (StringUtil.isNullOrEmpty(dataSourceType))
			return HikariPool.getInstance();
		
		if (dataSourceType.equals("hikari"))
			return HikariPool.getInstance();
		
		throw new PersistenceException("DataSource type: " + dataSourceType + ", is comming soon....");
	}
}
