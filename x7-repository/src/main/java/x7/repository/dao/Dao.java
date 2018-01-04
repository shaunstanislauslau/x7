package x7.repository.dao;

import java.util.List;
import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.web.Pagination;


/**
 * 
 * @author Sim
 *
 */
public interface Dao {

	long create(Object obj);

	boolean createBatch(List<Object> objList);

	boolean refresh(Object obj);
	
	boolean refresh(Object obj, Map<String,Object> conditionMap);

	boolean remove(Object obj);
	
	/**
	 * 适合单主键
	 * @param clz
	 * @param idOne
	 * @return
	 */
	<T> T get(Class<T> clz, long idOne);

	
	<T> List<T> list(Object conditionObj);
	
	<T> Pagination<T> list(Criteria criteria, Pagination<T> pagination);
	
	List<Map<String,Object>>  list(Class clz, String sql,
			List<Object> conditionList);

	<T> List<T> list(Class<T> clz);
	
	<T> T getOne(T conditionObj, String orderBy, String sc);

	<T> long getMaxId(Class<T> clz);
	
	long getMaxId(Object obj);
	
	long getCount(Object obj);
	
	@Deprecated
	boolean execute(Object obj, String sql);
	
	Object getSum(Object conditionObj, String sumProperty);
	
	Object getSum(String sumProperty, Criteria criteria);

	Object getCount(String countProperty, Criteria criteria);
	
	<T> List<T> in(Class<T> clz, List<? extends Object> inList);
	
	<T> List<T> in(Class<T> clz, String inProperty, List<? extends Object> inList);
	
	Pagination<Map<String,Object>> list(Criteria.Fetch criterionJoinable, Pagination<Map<String,Object>> pagination);
	
	List<Map<String,Object>> list(Criteria.Fetch criteriaJoinable);
}