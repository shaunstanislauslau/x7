package x7.repository.dao;

import java.util.Map;

import x7.core.bean.Criteria;
import x7.core.web.Pagination;



/**
 * 
 * Sharding <BR>
 * 
 * 
 * @author Sim
 *
 * @param <T>
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
	<T> long getCount(Object obj);
	
	<T> Pagination<T> list(Criteria criteria, Pagination<T> pagination);
	
	Pagination<Map<String,Object>> list(Criteria.Fetch criterionJoinable, Pagination<Map<String,Object>> pagination);

}