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

import x7.core.bean.*;
import x7.core.bean.condition.RefreshCondition;
import x7.core.repository.X;
import x7.core.util.BeanUtil;
import x7.core.util.BeanUtilX;
import x7.core.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class SqlUtil {


	protected static void adpterSqlKey(PreparedStatement pstmt, String keyOne, Object obj, int i)
			throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		/*
		 * 处理KEY
		 */
		Method method = null;
		try {
			method = obj.getClass().getDeclaredMethod(BeanUtil.getGetter(keyOne));
		} catch (NoSuchMethodException e) {
			method = obj.getClass().getSuperclass().getDeclaredMethod(BeanUtil.getGetter(keyOne));
		}
		Object value = method.invoke(obj);
		pstmt.setObject(i++, value);

	}

	protected static void adpterSqlKey(PreparedStatement pstmt, Field keyOneF,  Object obj, int i)
			throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		/*
		 * 处理KEY
		 */
		Object value = keyOneF.get(obj);

		pstmt.setObject(i++, value);

	}

	/**
	 * 拼接SQL
	 *
	 */
	protected static String concat(Parsed parsed, String sql, Map<String, Object> queryMap) {
		
		StringBuilder sb = new StringBuilder();

		boolean flag = (sql.contains(SqlScript.WHERE) || sql.contains(SqlScript.WHERE.toLowerCase()));

		for (String key : queryMap.keySet()) {

			String mapper = parsed.getMapper(key);
			if (flag) {
				sb.append(Conjunction.AND.sql()).append(mapper).append(SqlScript.EQ_PLACE_HOLDER);
			}else{
				sb.append(SqlScript.WHERE).append(mapper).append(SqlScript.EQ_PLACE_HOLDER);
				flag = true;
			}

		}

		sql += sb.toString();

		System.out.println(sql);

		return sql;
	}

	/**
	 * 拼接SQL
	 *
	 */
	protected static String concatRefresh(StringBuilder sb, Parsed parsed, Map<String, Object> refreshMap) {

		String keyOne = parsed.getKey(X.KEY_ONE);
		refreshMap.remove(keyOne);

		sb.append(SqlScript.SET);
		int size = refreshMap.size();
		int i = 0;
		for (String key : refreshMap.keySet()) {

			String mapper = parsed.getMapper(key);
			sb.append(mapper);
			sb.append(SqlScript.EQ_PLACE_HOLDER);
			if (i < size - 1) {
				sb.append(SqlScript.COMMA);
			}
			i++;
		}


		sb.append(SqlScript.WHERE);
		String mapper = parsed.getMapper(keyOne);
		sb.append(mapper).append(SqlScript.EQ_PLACE_HOLDER);

		return sb.toString();
	}

	/**
	 * 拼接SQL
	 *
	 */
	protected static String concatRefresh(StringBuilder sb, Parsed parsed, Map<String, Object> refreshMap,
										  RefreshCondition refreshCondition) {

		String keyOne = parsed.getKey(X.KEY_ONE);
		Object keyOneValue = refreshMap.get(keyOne);

		refreshMap.remove(keyOne);

		if(Objects.nonNull(keyOneValue)){
			String valueStr = keyOneValue.toString();
			if(StringUtil.isNullOrEmpty(valueStr)){
				keyOneValue = null;
			}else{
				try{
					long id = Long.valueOf(valueStr);
					if (id == 0){
						keyOneValue = null;
					}
				}catch (Exception e){

				}
			}

		}

		sb.append(SqlScript.SET);

		List<Criteria.X> refreshList = refreshCondition.getRefreshList();

		int size = refreshList.size();
		int i = 0;
		for (Criteria.X x : refreshList){
			if (x.getPredicate() == Predicate.X){

				Object value = x.getValue();

				String str = value.toString();

				if (str.contains(","))
					throw new RuntimeException("RefreshCondition.refresh(), para can not contains(,)");

				String target = BeanUtilX.normalizeSql(str);

				target = BeanUtilX.mapper(target,parsed);

				sb.append(target);

				if (i < size + refreshMap.size() - 1) {
					sb.append(SqlScript.COMMA);
				}
			}else{
				refreshMap.put(x.getKey(),x.getValue());
			}

			i++;
		}

		size = refreshMap.size();
		i = 0;
		for (String key : refreshMap.keySet()) {

			String mapper = parsed.getMapper(key);
			sb.append(mapper);
			sb.append(SqlScript.EQ_PLACE_HOLDER);
			if (i < size - 1) {
				sb.append(SqlScript.COMMA);
			}

			i++;
		}


		CriteriaCondition condition = refreshCondition.getCondition();

		List<Criteria.X> xList = condition.getListX();

		boolean flag = true;
		for (Criteria.X x : xList){
			if (x.getKey().equals(keyOne)){//allready
				flag = false;
				break;
			}
		}

		if (flag && Objects.nonNull(keyOneValue)){
			Criteria.X x = new Criteria.X();
			x.setConjunction(Conjunction.AND);
			x.setPredicate(Predicate.EQ);
			x.setKey(keyOne);
			x.setValue(keyOneValue);
			xList.add(0, x);
		}

		String conditionSql = CriteriaBuilder.parseCondition(condition);

		conditionSql = BeanUtilX.mapper(conditionSql,parsed);

		sb.append(conditionSql);

		return sb.toString();
	}

	protected static void adpterRefreshCondition(PreparedStatement pstmt, Field keyOneF, Object obj,
			int i, CriteriaCondition condition) throws SQLException, NoSuchMethodException, SecurityException,
					IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		if (Objects.nonNull(condition)) {
			System.out.println("_______________concatRefresh: condition.getValueList: " + condition.getValueList());
			for (Object v : condition.getValueList()) {
				if(Objects.nonNull(v) && v.getClass().isEnum()){
					pstmt.setObject(i++, v.toString());
				}else {
					pstmt.setObject(i++, v);
				}
			}
		}
	}

	protected static Object filter(Object value) {

		if (value instanceof String) {
			String str = (String) value;
			value = str.replace("<", "&lt").replace(">", "&gt");
		}

		return value;
	}


}
