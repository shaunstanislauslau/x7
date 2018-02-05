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

import java.util.List;
import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.web.Direction;
import x7.core.web.Pagination;


public class DaoWrapper implements Dao{

	private static DaoWrapper instance;

	public static DaoWrapper getInstance() {
		if (instance == null) {
			instance = new DaoWrapper();
		}
		return instance;
	}

	private DaoWrapper() {

	}

	private Dao dao;
	protected void setDao(Dao dao){
		this.dao = dao;
	}

	@Override
	public long create(Object obj) {
		return this.dao.create(obj);
	}


	@Override
	public boolean remove(Object obj) {
		return this.dao.remove(obj);
	}

	@Override
	public List<Map<String,Object>>  list(Class clz, String sql, List<Object> conditionList) {
		return this.dao.list(clz, sql, conditionList);
	}

	@Override
	public <T> List<T> list(Class<T> clz) {
		return this.dao.list(clz);
	}

	@Override
	public <T> List<T> list(Object conditionObj) {

		return this.dao.list(conditionObj);
	}

	@Override
	public <T> Pagination<T> find(Criteria criteria) {

		return this.dao.find(criteria);
	}

	@Override
	public <T> T getOne(T obj, String orderBy, Direction sc) {
		
		return this.dao.getOne(obj, orderBy, sc);
	}

	@Override
	public long getMaxId(Object obj) {

		return this.dao.getMaxId(obj);
	}

	@Override
	public boolean refresh(Object obj) {
		
		return this.dao.refresh(obj);
		
	}

	@Override
	public <T> T get(Class<T> clz, long idOne) {

		return this.dao.get(clz, idOne);
	}

	@Deprecated
	@Override
	public boolean execute(Object obj, String sql) {
		
		return this.dao.execute(obj, sql);
	}

	
	@Override
	public long getCount(Object obj) {
		
		return this.dao.getCount(obj);
	}

	@Override
	public Object getSum(String sumProperty, Criteria criteria) {
		
		return this.dao.getSum(sumProperty, criteria);
	}

	@Override
	public boolean refresh(Object obj, Map<String, Object> conditionMap) {

		return this.dao.refresh(obj, conditionMap);
	}


	@Override
	public <T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList) {
		return this.dao.in(clz, inProperty, inList);
	}

	@Override
	public Pagination<Map<String, Object>> find(Criteria.Fetch fetch) {
		return this.dao.find(fetch);
	}

	@Override
	public List<Map<String, Object>> list(Criteria.Fetch criteriaFetch) {
		
		return this.dao.list(criteriaFetch);
	}

	@Override
	public boolean createBatch(List<Object> obj) {
		return this.dao.createBatch(obj);
	}
	
	
}
