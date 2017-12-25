package x7.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import x7.core.async.CasualWorker;
import x7.core.async.IAsyncTask;
import x7.core.bean.Criteria;
import x7.core.bean.IQuantity;
import x7.core.util.StringUtil;
import x7.core.web.Pagination;
import x7.repository.exception.PersistenceException;
import x7.repository.mapper.Mapper;
import x7.repository.mapper.MapperFactory;
import x7.repository.redis.JedisConnector_Persistence;

/**
 * 
 * 其他模块的Repository建议继承此类
 *
 */
public abstract class BaseRepository<T> implements X7Repository<T> {

	public final static String ID_MAP_KEY = "ID_MAP_KEY";

	public Map<String, String> map = new HashMap<String, String>();

	private Class<T> clz;
	
	protected Class<T> getClz(){
		return clz;
	}

	public BaseRepository() {
		parse();
	}
	
	private void parse(){
		
		Type genType = getClass().getGenericSuperclass();

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		
		this.clz = (Class)params[0];
		
		System.out.println("______BaseRepository, " + this.clz.getName());
		HealthChecker.repositoryList.add(this);
	}

	protected Object preMapping(String methodName, Object... s) {

		boolean isOne = methodName.startsWith("get");

		String sql = map.get(methodName);
		if (StringUtil.isNullOrEmpty(sql)) {

			methodName = methodName.replace("list", "").replace("get", "").replace("find", "").replace("By", " where ");
			methodName = methodName.replace("And", " = ? and ").replace("Or", " = ? or ");

			methodName = methodName.toLowerCase();

			StringBuilder sb = new StringBuilder();
			sb.append("select * from ").append(methodName).append(" = ?");

			sql = sb.toString();

			map.put(methodName, sql);

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


	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#set(byte[], byte[])
	 */
	@Override
	public void set(byte[] key, byte[] value) {
		JedisConnector_Persistence.getInstance().set(key, value);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#get(byte[])
	 */
	@Override
	public byte[] get(byte[] key) {
		return JedisConnector_Persistence.getInstance().get(key);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#set(java.lang.String, java.lang.String, int)
	 */
	@Override
	public void set(String key, String value, int seconds) {
		JedisConnector_Persistence.getInstance().set(key, value, seconds);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#set(java.lang.String, java.lang.String)
	 */
	@Override
	public void set(String key, String value) {
		JedisConnector_Persistence.getInstance().set(key, value);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#get(java.lang.String)
	 */
	@Override
	public String get(String key) {
		return JedisConnector_Persistence.getInstance().get(key);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#createId(java.lang.Object)
	 */
	@Override
	public long createId(Object obj) {
		
		final String name = obj.getClass().getName();
		final long id = JedisConnector_Persistence.getInstance().hincrBy(ID_MAP_KEY, name, 1);

		if (id == 0) {
			throw new PersistenceException("UNEXPECTED EXCEPTION WHILE CREATING ID");
		}

		CasualWorker.accept(new IAsyncTask(){

			@Override
			public void execute() throws Exception {
				IdGenerator generator = new IdGenerator();
				generator.setClzName(name);
				List<IdGenerator> list = Repositories.getInstance().list(generator);
				if (list.isEmpty()){
					generator.setMaxId(id);
					Repositories.getInstance().create(generator);
				}else{
					generator.setMaxId(id);
					Repositories.getInstance().refresh(generator);
				}
			}
			
		});

		return id;
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#reduce(x7.core.bean.IQuantity, int)
	 */
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
	
	
	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#createBatch(java.util.List)
	 */
	@Override
	public boolean createBatch(List<T> objList){
		return Repositories.getInstance().createBatch(objList);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#create(T)
	 */
	@Override
	public long create(T obj) {
		/*
		 * FIXME 日志
		 */
		System.out.println("BaesRepository.create: " + obj);

		long id = Repositories.getInstance().create(obj);

		return id;

	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#refresh(T)
	 */
	@Override
	public boolean refresh(T obj) {
		return Repositories.getInstance().refresh(obj);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#refresh(T, java.util.Map)
	 */
	@Override
	public boolean refresh(T obj, Map<String, Object> conditionMap) {
		return Repositories.getInstance().refresh(obj, conditionMap);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#refreshAsync(T)
	 */
	@Override
	public void refreshAsync(T obj) {
		Repositories.getInstance().refreshAsync(obj);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#remove(T)
	 */
	@Override
	public void remove(T obj) {
		Repositories.getInstance().remove(obj);
	}


	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#get(long)
	 */
	@Override
	public T get(long idOne) {
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().get(clz, idOne);

	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#get(long, long)
	 */
	@Override
	public T get(long idOne, long idTwo) {
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().get(clz, idOne, idTwo);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#list()
	 */
	@Override
	public List<T> list() {

		return Repositories.getInstance().list(clz);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#list(T)
	 */
	@Override
	public List<T> list(T conditionObj) {

		if (conditionObj instanceof Criteria.Fetch) {
			throw new RuntimeException(
					"Exception supported, no pagination not to invoke Repositories.getInstance().list(criteriaJoinalbe);");
		}
		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(conditionObj);
	}


	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#list(x7.core.bean.Criteria.Fetch, x7.core.web.Pagination)
	 */
	@Override
	public Pagination<Map<String, Object>> list(Criteria.Fetch criteria, Pagination<Map<String, Object>> pagination) {

		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(criteria, pagination);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getMaxId(long)
	 */
	@Override
	public long getMaxId(long idOne) {

		return Repositories.getInstance().getMaxId(clz, idOne);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getMaxId()
	 */
	@Override
	public long getMaxId() {
		return Repositories.getInstance().getMaxId(clz);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getMaxId(T)
	 */
	@Override
	public long getMaxId(T conditionObj) {
		return Repositories.getInstance().getMaxId(conditionObj);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getCount(long)
	 */
	@Override
	public long getCount(long idOne) {

		return Repositories.getInstance().getCount(clz, idOne);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getCount(T)
	 */
	@Override
	public long getCount(T conditonObj) {
		return Repositories.getInstance().getCount(conditonObj);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getOne(T, java.lang.String, java.lang.String)
	 */
	@Override
	public T getOne(T conditionObj, String orderBy, String sc) {

		return Repositories.getInstance().getOne(conditionObj, orderBy, sc);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getOne(T)
	 */
	@Override
	public T getOne(T conditionObj) {

		T t = Repositories.getInstance().getOne(conditionObj);
		return t;
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#refreshTime(T)
	 */
	@Override
	public void refreshTime(T obj) {
		Repositories.getInstance().refreshTime(obj);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#refreshCache()
	 */
	@Override
	public void refreshCache() {
		Repositories.getInstance().refreshCache(clz);
	}


	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getSum(T, java.lang.String)
	 */
	@Override
	public Object getSum(T conditionObj, String sumProperty) {
		return Repositories.getInstance().getSum(conditionObj, sumProperty);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getSum(T, java.lang.String, x7.core.bean.Criteria)
	 */
	@Override
	public Object getSum(T conditionObj, String sumProperty, Criteria criteria) {
		return Repositories.getInstance().getSum(sumProperty, criteria);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#getCount(java.lang.String, x7.core.bean.Criteria)
	 */
	@Override
	public Object getCount(String sumProperty, Criteria criteria) {
		return Repositories.getInstance().getCount(sumProperty, criteria);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#in(java.util.List)
	 */
	@Override
	public List<T> in(List<? extends Object> inList) {
		if (inList.isEmpty())
			return new ArrayList<T>();
		Set<Object> set = new HashSet<Object>();
		for (Object obj : inList){
			set.add(obj);
		}
		
		List<Object> list = new ArrayList<Object>();
		for (Object obj : set){
			list.add(obj);
		}
		
		return Repositories.getInstance().in(clz, list);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#in(java.lang.String, java.util.List)
	 */
	@Override
	public List<T> in(String inProperty, List<? extends Object> inList) {
		if (inList.isEmpty())
			return new ArrayList<T>();
		
		Set<Object> set = new HashSet<Object>();
		for (Object obj : inList){
			set.add(obj);
		}
		
		List<Object> list = new ArrayList<Object>();
		for (Object obj : set){
			list.add(obj);
		}
		
		return Repositories.getInstance().in(clz, inProperty, list);
	}

	/* (non-Javadoc)
	 * @see x7.repository.X7RepositoryX#list(x7.core.bean.Criteria, x7.core.web.Pagination)
	 */
	@Override
	public Pagination<T> list(Criteria criteria, Pagination<T> pagination) {

		/*
		 * FIXME 日志
		 */
		return Repositories.getInstance().list(criteria, pagination);
	}

	
	public static class  HealthChecker {
		
		
		private static List<BaseRepository> repositoryList = new ArrayList<BaseRepository>();
		
		protected static void onStarted (){
			
			for (BaseRepository repository : repositoryList) {

				try{
					Class clz = repository.getClz();
					String sql = MapperFactory.tryToCreate(clz);
					String test = MapperFactory.getSql(clz, Mapper.CREATE);
					if (StringUtil.isNullOrEmpty(test)){
						System.out.println("FAILED TO START X7-REPOSITORY, check Bean: " + clz);
						System.exit(1);
					}
					
					Repositories.getInstance().execute(clz.newInstance(), sql);
					
				}catch (Exception e) {
					
				}
			}
		}
	}
}
