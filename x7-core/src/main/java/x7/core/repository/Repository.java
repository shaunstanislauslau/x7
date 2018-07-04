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
package x7.core.repository;

import x7.core.bean.Criteria;
import x7.core.web.Direction;
import x7.core.web.Pagination;

import java.util.List;
import java.util.Map;


/**
 * 
 * X7 Persistence Interface<br>
 * @author Sim
 *
 */
public interface Repository {
	
	/**
	 * 更新缓存
	 * @param clz
	 */
	<T> void refreshCache(Class<T> clz);

	/**
	 * 创建
	 * @param obj
	 * @return
	 */
	long create(Object obj);

	/**
	 * 更新, 支持局部更新
	 * @param obj
	 */
	boolean refresh(Object obj);
	
	/**
	 * 带条件支持局部更新
	 * @param obj
	 * @param conditionMap
	 * @return
	 */
	boolean refresh(Object obj, Map<String, Object> conditionMap);

	/**
	 * 删除
	 * @param obj
	 */
	boolean remove(Object obj);

	
	/**
	 * 根据主键查出单条
	 * @param clz
	 * @param idOne
	 * @return
	 */
	<T> T get(Class<T> clz, long idOne);


	/**
	 * 根据对象内容查询<br>
	 * 
	 * @param conditionObj
	 * 
	 */
	<T> List<T> list(Object conditionObj);
	
	/**
	 * 
	 * @param conditionObj
	 * @return
	 */
	<T> T getOne(T conditionObj);
	/**
	 * 根据对象查一条记录
	 * @param conditionObj
	 * @param orderBy
	 * @param sc "DESC", "ASC"
	 * @return
	 */
	<T> T getOne(T conditionObj, String orderBy, Direction sc);

	/**
	 * 根据对象内容查询<br>
	 *
	 *            可以拼接的条件
	 * 
	 */
	<T> Pagination<T> find(Criteria criteria);

	/**
	 * loadAll
	 * @param clz
	 * 
	 */
	<T> List<T> list(Class<T> clz);
	
	/**
	 * 支持单一的指定property的in查询, 包括主键
	 * @param clz
	 * @param inProperty
	 * @param inList
	 */
	<T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList);
	
	/**
	 * 最大值
	 * 
	 */
	long getMax(String property, Criteria criteria);
	
	/**
	 * 查总条数
	 * 
	 */
	long getCount(String property, Criteria criteria);
	
	
	/**
	 * 条件查询累计
	 * @param property
	 * @param criteria
	 * 
	 */
	Object getSum(String property, Criteria criteria);

	
	/**
	 * 连表查询，标准化拼接
	 * 尽量避免在互联网业务系统中使用<br>
	 * 不支持缓存<br>
	 * @param fetch
	 * 
	 */
	Pagination<Map<String,Object>> find(Criteria.Fetch fetch);
	
	/**
	 * 
	 * 不要通过WEB传来的参数调用此接口, 因为没有分页限制
	 * @param fetch
	 * 
	 */
	List<Map<String,Object>> list(Criteria.Fetch fetch);

	boolean createBatch(List<? extends Object> objList);
}