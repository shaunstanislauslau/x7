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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import x7.core.bean.BeanElement;
import x7.core.util.JsonX;
import x7.core.util.StringUtil;
import x7.repository.exception.SqlTypeException;

public class ResultSetUtil {

	public static <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles) {
		Object value = null;
		try {
			value = null;
			for (BeanElement ele : eles) {
				tempEle = ele;
				Method method = ele.setMethod;
				String mapper = ele.getMapper();

				if ( ele.clz == Boolean.class && DbType.ORACLE.equals(DbType.value)) {
					int ib = rs.getInt(mapper);
					value = ib;
					method.invoke(obj, ib == 1 ? true : false);
				}else if (ele.clz.isEnum()) {
					value = rs.getObject(mapper);
					if (Objects.isNull(value))
						continue;
					String str = value.toString();

					Method m = ele.clz.getDeclaredMethod("valueOf", String.class);
					Object e = m.invoke(null, str);
					value = e;
					method.invoke(obj, e);

				} else if (ele.isJson) {
					String str = rs.getString(mapper);
					if (StringUtil.isNullOrEmpty(str))
						continue;
					value = str;
					if (ele.clz == Map.class) {
						method.invoke(obj, JsonX.toMap(str));
					} else if (ele.clz == List.class) {
						method.invoke(obj, JsonX.toList(str, ele.geneType));
					} else {
						method.invoke(obj, JsonX.toObject(str, ele.clz));
					}
				} else if (ele.clz == BigDecimal.class) {

					value = rs.getObject(mapper);
					if (Objects.isNull(value))
						continue;
					method.invoke(obj, new BigDecimal((String.valueOf(value))));
				} else {
					value = rs.getObject(mapper);
					if (Objects.isNull(value))
						continue;
					if (value instanceof BigDecimal){// oracle supported
						if (ele.clz == long.class || ele.clz == Long.class){
							method.invoke(obj, ((BigDecimal) value).longValue());
						}else if (ele.clz == Integer.class || ele.clz == int.class){
							method.invoke(obj, ((BigDecimal) value).intValue());
						}else if (ele.clz == Boolean.class || ele.clz == boolean.class){
							method.invoke(obj, ((BigDecimal) value).intValue() == 1 ? true : false);
						}else if (ele.clz == Double.class || ele.clz == double.class){
							method.invoke(obj, ((BigDecimal) value).doubleValue());
						}else if (ele.clz == Float.class || ele.clz == float.class){
							method.invoke(obj, ((BigDecimal) value).floatValue());
						}else if (ele.clz == Short.class || ele.clz == short.class){
							method.invoke(obj, ((BigDecimal) value).shortValue());
						}else if (ele.clz == Byte.class || ele.clz == byte.class){
							method.invoke(obj, ((BigDecimal) value).byteValue());
						}
					}else {
						if (ele.clz == double.class || ele.clz == Double.class) {
							method.invoke(obj, Double.valueOf(String.valueOf(value)));
						}else {
							method.invoke(obj, value);
						}
					}
				}
			}
		}catch (Exception e){
			throw new SqlTypeException("clz:" + obj.getClass() + ", property: " + tempEle.getProperty() + ", type:" + tempEle.geneType + ", value; " + value );
		}
	}

}
