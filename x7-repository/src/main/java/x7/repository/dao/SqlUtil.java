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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import x7.core.bean.Parsed;
import x7.core.repository.X;
import x7.core.util.BeanUtil;



public class SqlUtil {
	
	protected static void adpterSqlKey(PreparedStatement pstmt, String keyOne, String keyTwo, Object obj, int i) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		/*
		 * 处理KEY
		 */
		Method method = null;
		try {
			method = obj.getClass().getDeclaredMethod(BeanUtil.getGetter(keyOne));
		} catch (NoSuchMethodException e) {
			method = obj.getClass().getSuperclass()
					.getDeclaredMethod(BeanUtil.getGetter(keyOne));
		}
		Object value = method.invoke(obj);
		pstmt.setObject(i++, value);

	}
	
	protected static void adpterSqlKey(PreparedStatement pstmt, Field keyOneF, Field keyTwoF, Object obj, int i) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		/*
		 * 处理KEY
		 */
		Object value = keyOneF.get(obj);
		pstmt.setObject(i++, value);


	}

	
	/**
	 * 拼接SQL
	 * @param sql
	 * @param obj
	 */
	protected static String concat(Parsed parsed, String sql, Map<String,Object> queryMap){

		for (String key : queryMap.keySet()){

			String mapper = parsed.getMapper(key);
			sql = sql.concat(" AND " + mapper + " = ?");

		}
		
		System.out.println(sql);
		
		return sql;
	}
	
	/**
	 * 拼接SQL
	 * @param sql
	 * @param obj
	 */
	protected static String concatRefresh(StringBuilder sb, Parsed parsed,Map<String,Object> queryMap){

		sb.append(" SET ");
		int size = queryMap.size();
		int i = 0;
		for (String key : queryMap.keySet()){

			String mapper = parsed.getMapper(key);
			sb.append(mapper);
			sb.append(" = ?");
			if (i < size - 1){
				sb.append(",");
			}
			
			i++;
		}
		
		sb.append(" WHERE ");
		String keyOne = parsed.getKey(X.KEY_ONE);
		String mapper = parsed.getMapper(keyOne);
		sb.append(mapper).append(" = ?");
		
		return sb.toString();
	}
	
	/**
	 * 拼接SQL
	 * @param sql
	 * @param obj
	 */
	protected static String concatRefresh(StringBuilder sb, Parsed parsed,Map<String,Object> queryMap, Map<String,Object> conditionMap){

		sb.append(" SET ");
		int size = queryMap.size();
		int i = 0;
		for (String key : queryMap.keySet()){

			String mapper = parsed.getMapper(key);
			sb.append(mapper);
			sb.append(" = ?");
			if (i < size - 1){
				sb.append(",");
			}
			
			i++;
		}
		
		sb.append(" WHERE ");
		String keyOne = parsed.getKey(X.KEY_ONE);
		String mapper = parsed.getMapper(keyOne);
		sb.append(mapper).append(" = ?");
		
		
		if (conditionMap != null) {
			for (String key : conditionMap.keySet()) {
				mapper = parsed.getMapper(key);
				sb.append(" AND ").append(mapper).append(" = ?");
			}
		}
		
		return sb.toString();
	}
	
	protected static void adpterRefreshCondition(PreparedStatement pstmt, Field keyOneF, Field keyTwoF, Object obj, int i, Map<String,Object> conditionMap) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		/*
		 * 处理KEY
		 */
		Object value = keyOneF.get(obj);
		pstmt.setObject(i++, value);

		if (keyTwoF != null ){
			value = keyTwoF.get(obj);
			pstmt.setObject(i++, value);
		}
		
		if (conditionMap != null) {
			for (Object v : conditionMap.values()) {
				pstmt.setObject(i++, v);
			}
		}
	}
	
	protected static Object filter(Object value) {
		
		if (value instanceof String){
			String str = (String)value;
			value = str.replace("<", "&lt").replace(">", "&gt");
		}
		
		return value;
	}
	
}
