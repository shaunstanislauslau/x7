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
package x7.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;

/**
 * 
 * 
 * @author Sim
 *
 */
public class JsonX {

	public static String toJson(List list){
		if (list == null)
			return null;
		if (list.isEmpty()){
			return "[]";
		}
		return JSON.toJSONString(list);
	}
	
	public static String toJson(Map map){
		if (map == null)
			return null;
		if (map.isEmpty()){
			return "{}";
		}
		return JSON.toJSONString(map);
	}
	
	public static <T> List<T> toList(String json, Class<T> clz){
		if (json == null || json.equals(""))
			return new ArrayList<T>();
		return JSON.parseArray(json, clz);
	}
	
	public static Map toMap(String json){
		if (json == null || json.equals(""))
			return new HashMap();
		return (Map) JSON.parse(json);
	}
	
	public static String toJson(Object obj){
		if (obj == null)
			return null;
		return JSON.toJSONString(obj);
	}
	
	public static <T> T toObject(String json, Class<T> clz){
		if (json == null || json.equals(""))
			return null;
		return JSON.parseObject(json, clz);
	}
	
	public static <T> T toObject(Object jsonObject, Class<T> clz){
		if (Objects.isNull(jsonObject))
			return null;

		return JSON.toJavaObject((JSON)jsonObject, clz);
	}
	
	public static Map<String,Object> toMap(Object obj){
		return (Map<String,Object>) JSON.toJSON(obj);
	}
	
	public static boolean isJsonable(Class clz) {
		if ( clz == String.class
						|| clz == long.class || clz == Long.class 
						|| clz == int.class || clz == Integer.class
						|| clz == boolean.class || clz == Boolean.class
						|| clz == double.class || clz == Double.class
						|| clz == float.class || clz == Float.class
						|| clz == short.class || clz == Short.class
						|| clz == byte.class || clz == Byte.class
						|| clz == BigDecimal.class || clz == Date.class
						)
			return false;
		return true;
	}
}
