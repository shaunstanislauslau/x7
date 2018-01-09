package x7.repository;

import java.util.List;
import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.bean.IQuantity;
import x7.core.web.Pagination;

/**
 * 
 * @author Sim
 *
 * @param <T>
 */
public interface X7Repository<T> {

	void set(byte[] key, byte[] value);

	byte[] get(byte[] key);

	void set(String key, String value, int seconds);

	void set(String key, String value);

	String get(String key);

	long createId(Object obj);

	/**
	 * @param obj
	 * @param reduced
	 * @return currentQuantity
	 */
	int reduce(IQuantity obj, int reduced);

	boolean createBatch(List<T> objList);

	long create(T obj);

	/**
	 * @param obj
	 */
	boolean refresh(T obj);

	/**
	 * @param obj
	 * @param conditionMap
	 * @return true | false
	 */
	boolean refresh(T obj, Map<String, Object> conditionMap);

	void refreshAsync(T obj);

	/**
	 * @param obj
	 */
	void remove(T obj);

	/**
	 *  
	 * @param clz
	 * @param idOne
	 * 
	 */
	T get(long idOne);


	/**
	 * LOAD
	 * 
	 * @param clz
	 * @return
	 */
	List<T> list();

	/**
	 * 根据对象查询
	 * 
	 * @param conditionObj
	 * 
	 */
	List<T> list(T conditionObj);

	/**
	 * Standard query pageable API, FETCH supported
	 * 
	 * @param criteria
	 * @param pagination
	 * 
	 */
	Pagination<Map<String, Object>> list(Criteria.Fetch criteria, Pagination<Map<String, Object>> pagination);


	/**
	 * 
	 * @param clz
	 * 
	 */
	long getMaxId();

	long getMaxId(T conditionObj);

	long getCount(T conditonObj);

	T getOne(T conditionObj, String orderBy, String sc);

	T getOne(T conditionObj);


	/**
	 * 
	 * 
	 * @param clz
	 */
	void refreshCache();

	Object getSum(T conditionObj, String sumProperty);

	Object getSum(T conditionObj, String sumProperty, Criteria criteria);

	/**
	 * 
	 * @param countProperty | null
	 * @param criteria
	 * @return
	 */
	Object getCount(String countProperty, Criteria criteria);

	List<T> in(List<? extends Object> inList);

	List<T> in(String inProperty, List<? extends Object> inList);

	/**
	 * Standard query pageable API
	 * 
	 * @param criteria
	 * @param pagination
	 * 
	 */
	Pagination<T> list(Criteria criteria, Pagination<T> pagination);

}