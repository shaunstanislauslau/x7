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
package x7.repository.dao;

import x7.core.bean.Criteria;
import x7.core.web.Pagination;

import java.util.Map;



/**
 * 
 * Sharding <BR>
 * 
 * 
 * @author Sim
 *
 * @param
 */
public interface ShardingDao {

	long create(Object obj);

	/**
	 * 一般要查出对象再更新
	 * @param obj
	 */
	boolean refresh(Object obj);
	
	boolean refresh(Object obj, Map<String, Object> conditonMap);

	boolean remove(Object obj);

	boolean execute(Object obj, String sql);

	<T> T get(Class<T> clz, long idOne);
	<T> T getOne(T conditionObj);
	<T> long getCount(String property, Criteria criteria);
	
	<T> Pagination<T> find(Criteria criteria);
	
	Pagination<Map<String,Object>> find(Criteria.Fetch fetch);

}