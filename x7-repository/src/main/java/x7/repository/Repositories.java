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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;

import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.CacheException;
import x7.core.repository.ICacheResolver;
import x7.core.repository.Repository;
import x7.core.repository.X;
import x7.core.web.Pagination;
import x7.repository.dao.Dao;
import x7.repository.dao.ShardingDao;
import x7.repository.exception.PersistenceException;
import x7.repository.exception.ShardingException;

/**
 * 
 * @author Sim
 *
 */
public class Repositories implements Repository {

	private final static Logger logger = Logger.getLogger(Repositories.class);
	private static Repositories instance;

	public static Repositories getInstance() {

		if (instance == null) {
			instance = new Repositories();
		}
		return instance;
	}

	private Dao syncDao;

	public void setSyncDao(Dao syncDao) {
		logger.info("---------------init---------------");
		logger.info("---------------" + syncDao);
		logger.info("---------------init---------------");
		this.syncDao = syncDao;
	}

	private ShardingDao shardingDao;

	public void setShardingDao(ShardingDao shardingDao) {
		this.shardingDao = shardingDao;
	}

	private ICacheResolver cacheResolver;

	public void setCacheResolver(ICacheResolver cacheResolver) {
		this.cacheResolver = cacheResolver;
	}

	private String getCacheKey(Object obj, Parsed parsed) {
		try {

			Field field = obj.getClass().getDeclaredField(parsed.getKey(X.KEY_ONE));
			field.setAccessible(true);
			String keyOne = field.get(obj).toString();
			return keyOne;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private <T> void replenishAndRefreshCache(List<String> keyList, List<T> list, Class<T> clz, Parsed parsed) {

		Set<String> keySet = new HashSet<String>();
		for (T t : list) {
			String key = getCacheKey(t, parsed);
			keySet.add(key);
		}

		for (String key : keyList) {
			if (!keySet.contains(key)) {

				T obj = null;

				Field f = parsed.getKeyField(X.KEY_ONE);
				if (f.getType() == String.class) {
					T condition = null;
					try {
						condition = clz.newInstance();
						f.set(condition, key);
					} catch (Exception e) {
						e.printStackTrace();
					}

					List<T> tempList = null;
					if (parsed.isSharding()) {
						throw new ShardingException("Sharding not supported");
					} else {
						tempList = syncDao.list(condition);
					}
					if (!tempList.isEmpty()) {
						obj = tempList.get(0);
					}

				} else {
					long idOne = Long.valueOf(key);
					if (parsed.isSharding()) {
						obj = shardingDao.get(clz, idOne);
					} else {
						obj = syncDao.get(clz, idOne);
					}
				}

				/*
				 * 更新或重置缓存
				 */
				if (obj == null) {
					if (cacheResolver != null && !parsed.isNoCache())
						cacheResolver.markForRefresh(clz);
				} else {
					list.add(obj);
					if (cacheResolver != null && !parsed.isNoCache())
						cacheResolver.set(clz, key, obj);
				}
			}
		}

	}

	private <T> void replenishAndRefreshCache_In_KeyOne(List<? extends Object> inList, List<T> list, Class<T> clz,
			Parsed parsed) {

		try {
			for (T t : list) {
				Field keyOneField = parsed.getKeyField(X.KEY_ONE);
				Object v = keyOneField.get(t);
				if (inList.contains(v)) {// 没从缓存里取到的值
					inList.remove(v);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<T> replenishedList = this.syncDao.in(parsed.getClz(), inList);

		for (T obj : replenishedList) {
			if (obj == null) {
				if (cacheResolver != null && !parsed.isNoCache())
					cacheResolver.markForRefresh(clz);
			} else {
				list.add(obj);
				if (cacheResolver != null && !parsed.isNoCache()) {
					Field keyOneField = parsed.getKeyField(X.KEY_ONE);
					Object keyO = null;
					try {
						keyO = keyOneField.get(obj);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new CacheException("IllegalArgumentException");
					}
					cacheResolver.set(clz, keyO.toString(), obj);
				}
			}
		}

	}

	private <T> List<T> sort(List<String> keyList, List<T> list, Parsed parsed) {
		List<T> sortedList = new ArrayList<T>();
		for (String key : keyList) {
			Iterator<T> ite = list.iterator();
			while (ite.hasNext()) {
				T t = ite.next();
				if (key.equals(getCacheKey(t, parsed))) {
					ite.remove();
					sortedList.add(t);
					break;
				}
			}
		}
		return sortedList;
	}

	@Override
	public long create(Object obj) {
		testAvailable();
		Class clz = obj.getClass();
		Parsed parsed = Parser.get(clz);
		long id = 0;
		if (parsed.isSharding()) {
			id = shardingDao.create(obj);
		} else {
			id = syncDao.create(obj);
		}
		if (cacheResolver != null && !parsed.isNoCache())
			cacheResolver.markForRefresh(clz);
		return id;
	}

	@Override
	public boolean refresh(Object obj) {
		testAvailable();
		boolean flag = false;
		Class clz = obj.getClass();
		Parsed parsed = Parser.get(clz);
		if (parsed.isSharding()) {
			shardingDao.refresh(obj);// FIXME
		} else {
			flag = syncDao.refresh(obj);
		}
		if (flag) {
			String key = getCacheKey(obj, parsed);
			if (cacheResolver != null && !parsed.isNoCache()) {
				if (key != null)
					cacheResolver.remove(clz, key);
				cacheResolver.markForRefresh(clz);
			}
		}
		return flag;
	}

	@Override
	public boolean refresh(Object obj, Map<String, Object> conditionMap) {
		testAvailable();
		boolean flag = false;
		Class clz = obj.getClass();
		Parsed parsed = Parser.get(clz);
		String key = getCacheKey(obj, parsed);
		if (parsed.isSharding()) {
			flag = shardingDao.refresh(obj, conditionMap);
		} else {
			flag = syncDao.refresh(obj, conditionMap);
		}
		if (cacheResolver != null && !parsed.isNoCache()) {
			if (key != null)
				cacheResolver.remove(clz, key);
			cacheResolver.markForRefresh(clz);
		}
		return flag;
	}

	/**
	 * 配合refreshTime使用，后台按更新时间查询列表之前调用
	 * 
	 * @param obj
	 */
	public <T> void refreshCache(Class<T> clz) {
		Parsed parsed = Parser.get(clz);
		if (cacheResolver != null && !parsed.isNoCache()) {
			cacheResolver.markForRefresh(clz);
		}

	}

	@Override
	public boolean remove(Object obj) {
		testAvailable();
		boolean flag = false;
		Class clz = obj.getClass();
		Parsed parsed = Parser.get(clz);
		String key = getCacheKey(obj, parsed);
		if (parsed.isSharding()) {
			shardingDao.remove(obj);// FIXME
		} else {
			flag = syncDao.remove(obj);
		}
		if (cacheResolver != null && !parsed.isNoCache()) {
			if (key != null)
				cacheResolver.remove(clz, key);
			cacheResolver.markForRefresh(clz);
		}
		return flag;
	}

	@Override
	public <T> T get(Class<T> clz, long idOne) {
		testAvailable();
		Parsed parsed = Parser.get(clz);

		if (cacheResolver == null || parsed.isNoCache()) {
			if (parsed.isSharding()) {
				return shardingDao.get(clz, idOne);
			} else {
				return syncDao.get(clz, idOne);
			}
		}

		String key = String.valueOf(idOne);
		T obj = cacheResolver.get(clz, key);

		if (obj == null) {
			if (parsed.isSharding()) {
				obj = shardingDao.get(clz, idOne);
			} else {
				obj = syncDao.get(clz, idOne);
			}
			cacheResolver.set(clz, key, obj);
		}

		return obj;
	}

	@Override
	public <T> List<T> list(Object conditionObj) {
		testAvailable();
		if (conditionObj instanceof CriteriaBuilder || conditionObj instanceof Criteria)
			throw new RuntimeException("Notes: parameter is not Criteria");

		Class clz = conditionObj.getClass();
		Parsed parsed = Parser.get(clz);

		if (cacheResolver == null || parsed.isNoCache()) {
			if (parsed.isSharding()) {
				throw new ShardingException("Sharding not supported");
			} else {
				return syncDao.list(conditionObj);
			}
		}

		List<T> list = null;

		String condition = conditionObj.toString();

		List<String> keyList = cacheResolver.getResultKeyList(clz, condition);

		if (keyList == null || keyList.isEmpty()) {
			if (parsed.isSharding()) {
				throw new ShardingException("Sharding not supported");
			} else {
				list = syncDao.list(conditionObj);
			}

			keyList = new ArrayList<String>();

			for (T t : list) {
				String key = getCacheKey(t, parsed);
				keyList.add(key);
			}

			cacheResolver.setResultKeyList(clz, condition, keyList);

			return list;
		}

		list = cacheResolver.list(clz, keyList);

		if (keyList.size() == list.size())
			return list;

		replenishAndRefreshCache(keyList, list, clz, parsed);

		List<T> sortedList = sort(keyList, list, parsed);

		return sortedList;
	}

	@Override
	public <T> T getOne(T conditionObj) {
		testAvailable();
		Class<T> clz = (Class<T>) conditionObj.getClass();
		Parsed parsed = Parser.get(clz);

		if (cacheResolver == null || parsed.isNoCache()) {
			if (parsed.isSharding()) {
				return shardingDao.getOne(conditionObj);
			} else {
				List<T> list = syncDao.list(conditionObj);
				if (list.isEmpty())
					return null;
				return list.get(0);
			}
		}

		String condition = conditionObj.toString();

		T obj = cacheResolver.get(clz, condition);

		if (obj == null) {
			T t = null;
			if (parsed.isSharding()) {
				t = shardingDao.getOne(conditionObj);
			} else {
				List<T> list = syncDao.list(conditionObj);
				if (list.isEmpty()) {
					t = null;
				} else {
					t = list.get(0);
				}
			}

			cacheResolver.set(clz, condition, obj);

			return t;
		}

		return obj;
	}

	@Override
	public <T> T getOne(T conditionObj, String orderBy, String sc) {
		testAvailable();
		Class<T> clz = (Class<T>) conditionObj.getClass();
		Parsed parsed = Parser.get(clz);

		if (cacheResolver == null || parsed.isNoCache()) {
			if (parsed.isSharding()) {
				throw new ShardingException("Sharding not supported");
			} else {
				return (T) syncDao.getOne(conditionObj, orderBy, sc);
			}
		}

		String condition = conditionObj.toString() + orderBy + sc;

		T obj = cacheResolver.get(clz, condition);

		if (obj == null) {
			T t = null;
			if (parsed.isSharding()) {
				throw new ShardingException("Sharding not supported");
			} else {
				t = syncDao.getOne(conditionObj, orderBy, sc);

			}

			cacheResolver.set(clz, condition, obj);

			return t;
		}

		return obj;
	}

	@Override
	public <T> Pagination<T> find(Criteria criteria) {
		testAvailable();
		Class clz = criteria.getClz();
		Parsed parsed = Parser.get(clz);
		

		if (cacheResolver == null) {
			if (parsed.isSharding()) {
				return shardingDao.find(criteria);
			} else {
				return syncDao.find(criteria);
			}
		}

		List<T> list = null;

		String condition = criteria.toString();

		Pagination<T> p = cacheResolver.getResultKeyListPaginated(clz, condition);// FIXME

		if (p == null) {
			if (parsed.isSharding()) {
				p = shardingDao.find(criteria);
			} else {
				p = syncDao.find(criteria);
			}

			list = p.getList(); // 结果

			List<String> keyList = p.getKeyList();

			for (T t : list) {

				String key = getCacheKey(t, parsed);
				keyList.add(key);
			}

			p.setList(null);

			cacheResolver.setResultKeyListPaginated(clz, condition, p, 10);

			p.setKeyList(null);
			p.setList(list);

			return p;
		}

		List<String> keyList = p.getKeyList();

		if (keyList == null || keyList.isEmpty()) {
			return p;
		}

		list = cacheResolver.list(clz, keyList);

		if (keyList.size() == list.size()) {
			p.setList(list);
			return p;
		}

		replenishAndRefreshCache(keyList, list, clz, parsed);

		List<T> sortedList = sort(keyList, list, parsed);

		p.setList(sortedList);

		return p;
	}

	@Override
	public <T> List<T> list(Class<T> clz) {
		testAvailable();
		Parsed parsed = Parser.get(clz);

		if (cacheResolver == null || parsed.isNoCache()) {
			if (parsed.isSharding()) {
				throw new ShardingException("Sharding not supported: List<T> list(Class<T> clz)");
			} else {
				return syncDao.list(clz);
			}
		}

		List<T> list = null;

		String condition = "loadAll";

		List<String> keyList = cacheResolver.getResultKeyList(clz, condition);

		if (keyList == null || keyList.isEmpty()) {
			if (parsed.isSharding()) {
				throw new ShardingException(
						"Sharding not supported: List<T> list(Class<T> clz, String sql, List<Object> conditionList)");
			} else {
				list = syncDao.list(clz);
			}

			keyList = new ArrayList<String>();

			for (T t : list) {
				String key = getCacheKey(t, parsed);
				keyList.add(key);
			}

			cacheResolver.setResultKeyList(clz, condition, keyList);

			return list;
		}

		list = cacheResolver.list(clz, keyList);// FIXME 可能要先转Object

		if (keyList.size() == list.size())
			return list;

		replenishAndRefreshCache(keyList, list, clz, parsed);

		List<T> sortedList = sort(keyList, list, parsed);

		return sortedList;
	}


	@Override
	public <T> long getMaxId(Class<T> clz) {
		testAvailable();
		Parsed parsed = Parser.get(clz);
		if (parsed.isSharding()) {
			throw new ShardingException("Sharding not supported: getMaxId(Class<T> clz)");
		} else {
			return syncDao.getMaxId(clz);
		}
	}

	@Override
	public long getCount(Object conditonObj) {
		testAvailable();
		Parsed parsed = Parser.get(conditonObj.getClass());
		if (parsed.isSharding()) {
			return shardingDao.getCount(conditonObj);
		} else {
			return syncDao.getCount(conditonObj);
		}
	}

	@Override
	public Object getSum(Object conditionObj, String sumProperty) {
		testAvailable();
		Class clz = conditionObj.getClass();
		Parsed parsed = Parser.get(clz);
		if (parsed.isSharding()) {
			throw new ShardingException("Sharding not supported: getSum(conditionObj, propertyName)");
		} else {
			return syncDao.getSum(conditionObj, sumProperty);
		}
	}

	@Override
	public Object getSum(String sumProperty, Criteria criteria) {
		testAvailable();
		Class clz = criteria.getClz();
		Parsed parsed = Parser.get(clz);
		if (parsed.isSharding()) {
			throw new ShardingException(
					"Sharding not supported: getSum(Object conditionObj, String sumProperty, Criteria criteria)");
		} else {
			return syncDao.getSum(sumProperty, criteria);
		}
	}

	@Override
	public long getMaxId(Object conditionObj) {
		testAvailable();
		Class clz = conditionObj.getClass();
		Parsed parsed = Parser.get(clz);
		if (parsed.isSharding()) {
			throw new ShardingException("Sharding not supported: getMaxId(Class<T> clz)");
		} else {
			return syncDao.getMaxId(conditionObj);
		}
	}


	protected <T> boolean execute(Object obj, String sql) {
		testAvailable();
		boolean b;
		Parsed parsed = Parser.get(obj.getClass());
		if (parsed.isSharding()) {
			b = shardingDao.execute(obj, sql);
		} else {
			b = syncDao.execute(obj, sql);
		}

		if (b) {
			String key = getCacheKey(obj, parsed);
			if (cacheResolver != null && !parsed.isNoCache()) {
				if (key != null) {
					cacheResolver.remove(obj.getClass(), key);
				}
			}
		}

		return b;
	}

	@Override
	public Object getCount(String countProperty, Criteria criteria) {
		testAvailable();
		Class clz = criteria.getClz();
		Parsed parsed = Parser.get(clz);
		if (parsed.isSharding()) {
			throw new ShardingException("Sharding not supported: getCount(String countProperty, Criteria criteria)");
		} else {
			return syncDao.getCount(countProperty, criteria);
		}
	}

	@Override
	public <T> List<T> in(Class<T> clz, List<? extends Object> inList) {
		testAvailable();
		Parsed parsed = Parser.get(clz);

		if (parsed.isSharding()) {
			throw new ShardingException(
					"Sharding not supported now: in(Class<T> clz, String inProperty, List<Object> inList)");
		}

		if (cacheResolver == null || parsed.isNoCache()) {
			return syncDao.in(clz, inList);
		}

		List<String> keyList = new ArrayList<String>();

		for (Object obj : inList) {
			keyList.add(obj.toString());
		}

		List<T> list = this.cacheResolver.list(clz, keyList);

		if (keyList.size() == list.size())
			return list;

		replenishAndRefreshCache_In_KeyOne(inList, list, clz, parsed);// FIXME

		List<T> sortedList = sort(keyList, list, parsed);

		return sortedList;
	}

	@Override
	public <T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList) {
		testAvailable();
		Parsed parsed = Parser.get(clz);
		if (parsed.isSharding()) {
			throw new ShardingException(
					"Sharding not supported now: in(Class<T> clz, String inProperty, List<Object> inList)");
		}

		if (cacheResolver == null || parsed.isNoCache()) {
			return syncDao.in(clz, inProperty, inList);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(inProperty).append(":");
		for (Object obj : inList) {
			sb.append(obj.toString()).append("_");
		}
		String condition = sb.toString();

		List<String> keyList = cacheResolver.getResultKeyList(clz, condition);

		List<T> list = null;

		if (keyList == null || keyList.isEmpty()) {

			list = syncDao.in(clz, inProperty, inList);

			keyList = new ArrayList<String>();

			for (T t : list) {
				String key = getCacheKey(t, parsed);
				keyList.add(key);
			}

			cacheResolver.setResultKeyList(clz, condition, keyList);

			return list;
		}

		list = cacheResolver.list(clz, keyList);// FIXME 可能要先转Object

		if (keyList.size() == list.size())
			return list;

		replenishAndRefreshCache(keyList, list, clz, parsed);

		List<T> sortedList = sort(keyList, list, parsed);

		return sortedList;
	}

	@Override
	public Pagination<Map<String, Object>> find(Criteria.Fetch fetch) {
		testAvailable();
		Class clz = fetch.getClz();
		Parsed parsed = Parser.get(clz);
		
		if (parsed.isSharding()) {
			return shardingDao.find(fetch);
		} else {
			return syncDao.find(fetch);
		}
	}

	@Override
	public List<Map<String, Object>> list(Criteria.Fetch fetch) {
		testAvailable();
		Class clz = fetch.getClz();
		Parsed parsed = Parser.get(clz);

		if (parsed.isSharding()) {
			throw new ShardingException(
					"Sharding not supported: List<Map<String, Object>> list(Criteria.Fetch fetch)");
		} else {
			return syncDao.list(fetch);
		}
	}

	@Override
	public boolean createBatch(List<? extends Object> objList) {
		testAvailable();
		Class clz = objList.get(0).getClass();
		Parsed parsed = Parser.get(clz);
		List<Object> list = new ArrayList<Object>();
		list.addAll(objList);
		if (cacheResolver != null && !parsed.isNoCache())
			cacheResolver.markForRefresh(clz);
		return this.syncDao.createBatch(list);
	}

	protected List<Map<String, Object>> list(Class clz, String sql, List<Object> conditionList) {
		
		Parsed parsed = Parser.get(clz);

		if (cacheResolver == null || parsed.isNoCache()) {
			if (parsed.isSharding()) {
				throw new ShardingException(
						"Sharding not supported: List<Map<String,Object>>  list(Class<T> clz, String sql, List<Object> conditionList)");
			} else {
				return syncDao.list(clz, sql, conditionList);
			}
		}

		String condition = sql + conditionList.toString();

		List<Map<String, Object>> mapList = cacheResolver.getMapList(clz, condition);

		if (mapList == null) {
			mapList = syncDao.list(clz, sql, conditionList);

			if (mapList != null) {
				cacheResolver.setMapList(clz, condition, mapList);
			}
		}

		return mapList;

	}
	
	private void testAvailable(){
		if (Objects.isNull(this.syncDao))
			throw new PersistenceException("X7-Repository does not started");
	}

}
