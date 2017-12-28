package x7.repository;

import java.util.List;
import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.bean.IQuantity;
import x7.core.web.Pagination;

public interface X7Repository<T> {

	void set(byte[] key, byte[] value);

	byte[] get(byte[] key);

	void set(String key, String value, int seconds);

	void set(String key, String value);

	String get(String key);

	long createId(Object obj);

	/**
	 * 以持久化后的数量为准<br>
	 * 适合高速更新数量的需求<br>
	 * 执行结束后，需要更新相关的对象到DB, 可以加入队列，合并数量，调用异步更新接口，此 API未实现复杂逻辑<br>
	 * 
	 * @param obj
	 * @param reduced
	 * @return currentQuantity
	 */
	int reduce(IQuantity obj, int reduced);

	boolean createBatch(List<T> objList);

	long create(T obj);

	/**
	 * 直接更新，不需要查出对象再更新<BR>
	 * 对于可能重置为0的数字，或Boolean类型，不能使用JAVA基本类型
	 * 
	 * @param obj
	 */
	boolean refresh(T obj);

	/**
	 * 带条件更新(默认需要ID, 不需要增加id)<br>
	 * 不支持无ID更新<br>
	 * 
	 * @param obj
	 * @param conditionMap
	 * @return true | false
	 */
	boolean refresh(T obj, Map<String, Object> conditionMap);

	void refreshAsync(T obj);

	/**
	 * 删除数据<br>
	 * 为了删除索引，必须先从数据库里查出对象，再删<br>
	 * 如果有辅助索引类(IIndexTyped.java), 每次仅仅支持删除条记录，自动删除索引<br>
	 * 
	 * @param obj
	 */
	void remove(T obj);

	/**
	 * 
	 * 根据主键查一条记录，(findByPK)
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
	 * 手动拼接SQL查询 分页
	 * 
	 * @param criteria
	 * @param pagination
	 * 
	 */
	Pagination<Map<String, Object>> list(Criteria.Fetch criteria, Pagination<Map<String, Object>> pagination);


	/**
	 * 获取�?大ID
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
	 * 配合refreshTime使用，后台按更新时间查询列表之前调用<br>
	 * 
	 * @param clz
	 */
	void refreshCache();

	Object getSum(T conditionObj, String sumProperty);

	Object getSum(T conditionObj, String sumProperty, Criteria criteria);

	Object getCount(String sumProperty, Criteria criteria);

	List<T> in(List<? extends Object> inList);

	List<T> in(String inProperty, List<? extends Object> inList);

	/**
	 * 手动拼接SQL查询 分页
	 * 
	 * @param criteria
	 * @param pagination
	 * 
	 */
	Pagination<T> list(Criteria criteria, Pagination<T> pagination);

}