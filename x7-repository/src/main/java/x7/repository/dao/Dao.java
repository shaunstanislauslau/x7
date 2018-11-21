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
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.ReduceCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Direction;
import x7.core.web.Pagination;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author Sim
 *
 */
public interface Dao {

	long create(Object obj);

	boolean createBatch(List<Object> objList);

	boolean refresh(Object obj);

	boolean remove(Object obj);

	<T> boolean refreshByCondition(RefreshCondition<T> conditon);

	
	/**
	 * 适合单主键
	 * @param clz
	 * @param idOne
	 * @return
	 */
	<T> T get(Class<T> clz, long idOne);

	
	<T> List<T> list(Object conditionObj);
	
	List<Map<String,Object>>  list(Class clz, String sql,
			List<Object> conditionList);

	<T> List<T> list(Class<T> clz);
	
	<T> T getOne(T conditionObj, String orderBy, Direction sc);
	
	<T> List<T> in(InCondition inCondition);
	
	Pagination<Map<String, Object>> find(Criteria.ResultMapped resultMapped);
	
	List<Map<String,Object>> list(Criteria.ResultMapped resultMapped);
	
	<T> Pagination<T> find(Criteria criteria);

	<T> Object reduce(ReduceCondition<T> reduceCondition);

	@Deprecated
	boolean execute(Object obj, String sql);
}