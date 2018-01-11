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

public interface ConfigKey{
	String IS_DEVELOPING = "x7.developing";
	String IS_CACHEABLE = "x7.cache.serving";
	String IS_ID_GENERATOR = "x7.db.id.generator";
	String REPOSITORY = "x7.repository.db";
	String SHARDING_MODE = "x7.db.sharding.mode";
	String SHARDING_POLICY = "x7.db.sharding.policy";
	String DB_NAMING_SPEC = "x7.db.naming.spec";
	String DB_NAMING_PREFIX = "x7.db.naming.prefix";
	

//	x7.repository.dataSourceType
//	x7.db.sharding.num
	
//	x7.db.sharding.policy
//	x7.db.url
//	x7.db.address.w
//	x7.db.address.r
//	x7.db.name
//	x7.db.username
//	x7.db.password
//	x7.db.max
//	x7.db.read
}
