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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import x7.core.bean.BeanElement;
import x7.core.util.JsonX;
import x7.repository.exception.PersistenceException;

public class ResultSetUtil {

	public static <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles)
			throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		for (BeanElement ele : eles) {
			tempEle = ele;
			Method method = ele.setMethod;
			String mapper = ele.getMapper();
			if (ele.clz.isEnum()) {
				Object v = rs.getObject(mapper);
				if (v==null)
					continue;
				String str = v.toString();

				Method m = ele.clz.getDeclaredMethod("valueOf", String.class);
				Object e = m.invoke(null, str);
				method.invoke(obj, e);

			} else if (ele.isJson) {
				String str = rs.getString(mapper);
				if (ele.clz == Map.class) {
					method.invoke(obj, JsonX.toMap(str));
				} else if (ele.clz == List.class) {
					method.invoke(obj, JsonX.toList(str, ele.geneType));
				} else {
					method.invoke(obj, JsonX.toObject(str, ele.clz));
				}
			} else if (ele.clz.getSimpleName().toLowerCase().equals("double")) {
				Object v = rs.getObject(mapper);
				if (v != null) {
					method.invoke(obj, Double.valueOf(String.valueOf(v)));
				}
			} else if (ele.clz == BigDecimal.class) {

				Object v = rs.getObject(mapper);
				if (v != null) {
					method.invoke(obj, new BigDecimal((String.valueOf(v))));
				}
			} else {
				method.invoke(obj, rs.getObject(mapper));
			}
		}
	}

}
