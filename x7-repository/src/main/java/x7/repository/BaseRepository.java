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

import org.apache.log4j.Logger;
import x7.core.async.CasualWorker;
import x7.core.async.IAsyncTask;
import x7.core.bean.*;
import x7.core.repository.X;
import x7.core.util.BeanUtilX;
import x7.core.util.StringUtil;
import x7.core.web.Direction;
import x7.core.web.Pagination;
import x7.repository.exception.PersistenceException;
import x7.repository.mapper.Mapper;
import x7.repository.mapper.MapperFactory;
import x7.repository.redis.JedisConnector_Persistence;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 
 * Biz Repository extends BaseRepository
 * 
 * @author Sim
 *
 * @param <T>
 */
public abstract class BaseRepository<T> implements X7Repository<T> {

	private final static Logger logger = Logger.getLogger(BaseRepository.class);

	public final static String ID_MAP_KEY = "ID_MAP_KEY";

	public Map<String, String> map = new HashMap<String, String>();

	private Class<T> clz;

	protected Class<T> getClz() {
		return clz;
	}

	public BaseRepository() {
		parse();
	}

	private void parse() {

		Type genType = getClass().getGenericSuperclass();

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		this.clz = (Class) params[0];

		logger.info("BaseRepository<T>, T: " + this.clz.getName());
		HealthChecker.repositoryList.add(this);
	}

	protected Object find(String methodName, Object... s) {

		boolean isOne = methodName.startsWith("get");

		String sql = map.get(methodName);
		if (StringUtil.isNullOrEmpty(sql)) {


			Parsed parsed = Parser.get(this.clz);

			String[] arr = methodName.split("By");

			String conditionSql = arr[1];

			String defaultKeyword = "eq";
			String[] keywordArr = {"And", "Or", "NotIn", "In", "Like", "IsNotNull", "IsNull", "Ne", "Eq", "Gte", "Gt", "Lte", "Lt"};
			String space = " ";

			for (String keyword : keywordArr) {
				String target = space + keyword + space;
				conditionSql = conditionSql.replace(keyword, target);
			}

		}









		List<Object> conditionList = Arrays.asList(s);
		List<T> list = (List<T>) ManuRepository.list(clz, sql, conditionList);

		if (isOne) {
			if (list.isEmpty())
				return null;
			return list.get(0);
		}

		return list;
	}

	@Override
	public void set(byte[] key, byte[] value) {
		JedisConnector_Persistence.getInstance().set(key, value);
	}

	@Override
	public byte[] get(byte[] key) {
		return JedisConnector_Persistence.getInstance().get(key);
	}

	@Override
	public void set(String key, String value, int seconds) {
		JedisConnector_Persistence.getInstance().set(key, value, seconds);
	}

	@Override
	public void set(String key, String value) {
		JedisConnector_Persistence.getInstance().set(key, value);
	}

	@Override
	public String get(String key) {
		return JedisConnector_Persistence.getInstance().get(key);
	}

	@Override
	public long createId() {

		final String name = clz.getName();
		final long id = JedisConnector_Persistence.getInstance().hincrBy(ID_MAP_KEY, name, 1);

		if (id == 0) {
			throw new PersistenceException("UNEXPECTED EXCEPTION WHILE CREATING ID");
		}

		CasualWorker.accept(new IAsyncTask() {

			@Override
			public void execute() throws Exception {
				IdGenerator generator = new IdGenerator();
				generator.setClzName(name);
				generator.setMaxId(id);
				StringBuilder sb = new StringBuilder();
				sb.append("update idGenerator set maxId = ").append(id).append(" where clzName = '").append(name)
						.append("' and ").append(id).append(" > maxId ;");//sss

				try {
					Parsed parsed = Parser.get(IdGenerator.class);
					String sql = sb.toString();
					ManuRepository.execute(generator, sql);
				}catch(Exception e){
					e.printStackTrace();
				}

			}

		});

		return id;
	}

	@Override
	public int reduce(IQuantity obj, int reduced) {
		if (reduced < 0) {
			throw new RuntimeException("reduced quantity must > 0");
		}

		String mapKey = obj.getClass().getName();

		int quantity = (int) JedisConnector_Persistence.getInstance().hincrBy(mapKey, obj.getKey(), -reduced);

		obj.setQuantity(quantity);

		return quantity;
	}

	@Override
	public boolean createBatch(List<T> objList) {
		return SqlRepository.getInstance().createBatch(objList);
	}

	@Override
	public long create(T obj) {
		/*
		 * FIXME
		 */
		System.out.println("BaesRepository.create: " + obj);

		long id = SqlRepository.getInstance().create(obj);

		return id;

	}

	@Override
	public boolean refresh(T obj) {
		return SqlRepository.getInstance().refresh(obj);
	}

	@Override
	public boolean refresh(T obj, CriteriaCondition condition) {
		return SqlRepository.getInstance().refresh(obj, condition);
	}

	@Override
	public void remove(T obj) {
		SqlRepository.getInstance().remove(obj);
	}

	@Override
	public T get(long idOne) {

		return SqlRepository.getInstance().get(clz, idOne);
	}

	@Override
	public List<T> list() {

		return SqlRepository.getInstance().list(clz);
	}

	@Override
	public List<T> list(T conditionObj) {

		if (conditionObj instanceof Criteria.ResultMapped) {
			throw new RuntimeException(
					"Exception supported, no pagination not to invoke SqlRepository.getInstance().list(criteriaJoinalbe);");
		}

		return SqlRepository.getInstance().list(conditionObj);
	}

	@Override
	public Pagination<Map<String, Object>> find(Criteria.ResultMapped criteria) {

		return SqlRepository.getInstance().find(criteria);
	}

	@Override
	public T getOne(T conditionObj, String orderBy, Direction sc) {

		return SqlRepository.getInstance().getOne(conditionObj, orderBy, sc);
	}

	@Override
	public T getOne(T conditionObj) {

		T t = SqlRepository.getInstance().getOne(conditionObj);
		return t;
	}

	@Override
	public void refreshCache() {
		SqlRepository.getInstance().refreshCache(clz);
	}

	@Override
	public Object reduce(Criteria.ReduceType type,String reduceProperty, Criteria criteria) {
		return SqlRepository.getInstance().reduce(type, reduceProperty, criteria);
	}


	@Override
	public List<T> in(String inProperty, List<? extends Object> inList) {
		if (inList.isEmpty())
			return new ArrayList<T>();

		return SqlRepository.getInstance().in(clz, inProperty, inList);
	}

	@Override
	public Pagination<T> find(Criteria criteria) {

		return SqlRepository.getInstance().find(criteria);
	}

	public static class HealthChecker {

		private static List<BaseRepository> repositoryList = new ArrayList<BaseRepository>();

		protected static void onStarted() {

			Parsed parsed = Parser.get(IdGenerator.class);

			String sql = "CREATE TABLE IF NOT EXISTS `idGenerator` ( "
			+ "`clzName` varchar(120) NOT NULL, "
					+ "`maxId` bigint(13) DEFAULT NULL, "
			+ "PRIMARY KEY (`clzName`) "
					+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ";
			
			ManuRepository.execute(IdGenerator.class, sql);

			System.out.println("-------------------------------------------------");

			boolean flag = false;

			for (BaseRepository repository : repositoryList) {

				try {
					Class clz = repository.getClz();
					String createSql = MapperFactory.tryToCreate(clz);
					String test = MapperFactory.getSql(clz, Mapper.CREATE);
					if (StringUtil.isNullOrEmpty(test)) {
						System.out.println("FAILED TO START X7-REPOSITORY, check Bean: " + clz);
						System.exit(1);
					}

					if (DbType.value.equals(DbType.MYSQL)) {
						System.out.println("________ table check: " + clz.getName());
						System.out.println("________ SQL   check: " + createSql);
						SqlRepository.getInstance().execute(clz.newInstance(), createSql);
					}

					Parsed clzParsed = Parser.get(clz);
					Field f = clzParsed.getKeyField(X.KEY_ONE);
					if (f.getType() == String.class)
						continue;
					final String name = clz.getName();
					IdGenerator generator = new IdGenerator();
					generator.setClzName(name);
					List<IdGenerator> list = SqlRepository.getInstance().list(generator);
					if (list.isEmpty()) {
						System.out.println("________ id init: " + generator.getClzName());
						generator.setMaxId(0);
						SqlRepository.getInstance().create(generator);
					}

				} catch (Exception e) {
					flag |= true;
//					e.printStackTrace();
				}
			}

			logger.info("X7 Repository " + (flag?"still ":"")+ "started" + (flag ? " OK, wtih some problem" : ""));

		}
	}

	public enum SimpleX {

		//"And", "Or", "NotIn", "In", "Like", "IsNotNull", "IsNull", "Ne", "Eq", "Gte", "Gt", "Lte", "Lt"
		AND{

			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setConjunction(Conjunction.AND);
				return x;
			}

		},
		OR{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setConjunction(Conjunction.OR);
				return x;
			}
		},
		NOTIN{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.NOT_IN);
				return x;
			}
		},
		IN{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.IN);
				return x;
			}
		},
		LIKE{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.LIKE);
				return x;
			}
		},
		ISNOTNULL{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.IS_NOT_NULL);
				return x;
			}
		},
		ISNULL{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.IS_NULL);
				return x;
			}
		},
		NE{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.NE);
				return x;
			}
		},
		EQ{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.EQ);
				return x;
			}
		},
		GTE{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.GTE);
				return x;
			}
		},
		GT{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.GT);
				return x;
			}
		},
		LTE{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.LTE);
				return x;
			}
		},
		LT{
			public Criteria.X x() {
				Criteria.X x = new Criteria.X();
				x.setPredicate(Predicate.LT);
				return x;
			}
		};

		public abstract Criteria.X x();
	}

}
