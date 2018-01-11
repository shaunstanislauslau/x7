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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import x7.core.bean.BeanElement;
import x7.core.bean.Parsed;
import x7.core.repository.X;
import x7.core.repository.SqlFieldType;
import x7.core.search.Search;
import x7.core.search.TagParsed;


public class BeanUtilX extends BeanUtil {

	@SuppressWarnings("rawtypes")
	public static List<BeanElement> getElementList(Class clz) {

		List<Field> fl = new ArrayList<Field>();

		if (clz.getSuperclass() != Object.class) {
			fl.addAll(Arrays.asList(clz.getSuperclass().getDeclaredFields()));
		}
		fl.addAll(Arrays.asList(clz.getDeclaredFields()));

		/*
		 * 排除transient
		 */
		Map<String, Field> filterMap = new HashMap<String, Field>();
		Map<String, Field> allMap = new HashMap<String, Field>();
		for (Field f : fl) {
			allMap.put(f.getName(), f);

			if (f.getModifiers() >= 128) {
				filterMap.put(f.getName(), f);
			}

			/*
			 * ignored anno
			 */
			X.Ignore p = f.getAnnotation(X.Ignore.class);
			if (p != null) {
				filterMap.put(f.getName(), f);
			}
		}

		Set<String> mns = new HashSet<String>();
		List<Method> ml = new ArrayList<Method>();
		if (clz.getSuperclass() != Object.class) {
			ml.addAll(Arrays.asList(clz.getSuperclass().getDeclaredMethods()));
		}
		ml.addAll(Arrays.asList(clz.getDeclaredMethods())); // 仅仅XxxMapped子类

		for (Method m : ml) {
			mns.add(m.getName());
		}

		List<BeanElement> filterList = new ArrayList<BeanElement>();
		for (Method m : ml) {
			String name = m.getName();
			if (!(name.startsWith("set") || name.startsWith("get") || name.startsWith("is")))
				continue;

			String key = getProperty(name);
			BeanElement be = null;
			for (BeanElement b : filterList) {
				if (b.getProperty().equals(key)) {
					be = b;
					break;
				}
			}
			if (be == null) {
				be = new BeanElement();
				be.setProperty(key); 
				filterList.add(be);
			}
			if (name.startsWith("set")) {
				be.setter = name;
			} else if (name.startsWith("get")) {
				be.getter = name;
				be.clz = m.getReturnType();
			} else if (name.startsWith("is")) {
				be.getter = name;
				be.clz = m.getReturnType();
				be.setProperty(name);
				String setter = getSetter(name); // FIXME 可能有BUG
				if (mns.contains(setter)) {
					be.setter = setter;
				}
			}

		}

		/*
		 * 找出有setter 和 getter的一对
		 */
		Iterator<BeanElement> ite = filterList.iterator();
		while (ite.hasNext()) {// BUG, 这里去掉了boolen属性
			BeanElement be = ite.next();
			if (!be.isPair()) {
				ite.remove();
			}
		}

		/*
		 * 去掉transient
		 */
		for (String key : filterMap.keySet()) {
			Iterator<BeanElement> beIte = filterList.iterator();
			while (beIte.hasNext()) {
				BeanElement be = beIte.next();
				if (be.getProperty().equals(key)) {
					beIte.remove();
					break;
				}
			}
		}

		List<BeanElement> list = new ArrayList<BeanElement>();

		for (BeanElement element : filterList) {

			parseAnno(clz, element, allMap.get(element.getProperty()));

			Class ec = element.clz;
			if (element.sqlType == null) {
				if (ec == int.class || ec == Integer.class) {
					element.sqlType = SqlFieldType.INT;
					element.length = 11;
				} else if (ec == long.class || ec == Long.class) {
					element.sqlType = SqlFieldType.LONG;
					element.length = 13;
				} else if (ec == double.class || ec == Double.class) {
					element.sqlType = SqlFieldType.DOUBLE;
					element.length = 13;
				} else if (ec == float.class || ec == Float.class) {
					element.sqlType = SqlFieldType.FLOAT;
					element.length = 13;
				} else if (ec == boolean.class || ec == Boolean.class) {
					element.sqlType = SqlFieldType.BYTE;
					element.length = 1;
				} else if (ec == Date.class) {
					element.sqlType = SqlFieldType.DATE;
				} else if (ec == String.class) {
					element.sqlType = SqlFieldType.VARCHAR;
					if (element.length == 0)
						element.length = 60;
				} else if (ec == BigDecimal.class){
					element.sqlType = SqlFieldType.DECIMAL;
				} else if (ec.isEnum()){
					element.sqlType = SqlFieldType.VARCHAR;
					if (element.length == 0)
						element.length = 40;
				}else {
					element.isJson = true;
					if (clz == List.class) {
						Field field = null;
						try {
							field = clz.getDeclaredField(element.getProperty());
						} catch (Exception e) {
							e.printStackTrace();
						}
						ParameterizedType pt = (ParameterizedType) field.getGenericType();
						Class geneTyep = (Class) pt.getActualTypeArguments()[0];
						element.geneType = geneTyep;
					}
					element.sqlType = SqlFieldType.VARCHAR;
					if (element.length == 0)
						element.length = 512;
				}
			} else if (element.sqlType.contains(SqlFieldType.TEXT)) {
				element.length = 0;
			} else {
				element.sqlType = SqlFieldType.VARCHAR;
			}

			list.add(element);
		}

		try {
			for (BeanElement be : list) {
				try {
					be.setMethod = clz.getDeclaredMethod(be.setter, be.clz);
				} catch (NoSuchMethodException e) {
					be.setMethod = clz.getSuperclass().getDeclaredMethod(be.setter, be.clz);
				}
				try {
					be.getMethod = clz.getDeclaredMethod(be.getter);
				} catch (NoSuchMethodException e) {
					be.getMethod = clz.getSuperclass().getDeclaredMethod(be.getter);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public static void parseCacheableAnno(Class clz, Parsed parsed) {
		X.NoCache p = (X.NoCache) clz.getAnnotation(X.NoCache.class);
		if (p != null) {
			parsed.setNoCache(true);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String parseAnno(Class clz, BeanElement ele, Field f) {
		String type = null;
		Method m = null;
		try {
			m = clz.getDeclaredMethod(ele.getter);
		} catch (NoSuchMethodException e) {

		}
		if (m != null) {
			X p = m.getAnnotation(X.class);
			if (p != null) {
				ele.sqlType = p.type();
				ele.length = p.length();
			}
		}

		if (f != null) {
			X p = f.getAnnotation(X.class);
			if (p != null) {
				ele.sqlType = p.type();
				ele.length = p.length();
			}
			
			X.Mapping mapping = (X.Mapping) f.getAnnotation(X.Mapping.class);
			if (mapping != null) {
				if (StringUtil.isNotNull(mapping.value()))
					ele.mapper = mapping.value();
			}

			X.IsMobile isMobile = f.getAnnotation(X.IsMobile.class);
			if (isMobile != null) {
				ele.isMobile = true;
			}

			X.IsEmail isEmail = f.getAnnotation(X.IsEmail.class);
			if (isEmail != null) {
				ele.isEmail = true;
			}

			X.NotNull notNull = f.getAnnotation(X.NotNull.class);
			if (notNull != null) {
				ele.notNull = true;
			}
		}

		return type;
	}

	@SuppressWarnings({ "rawtypes" })
	public static void parseKey(Parsed parsed, Class clz) {

		Map<Integer, String> map = parsed.getKeyMap();
		Map<Integer, Field> keyFieldMap = parsed.getKeyFieldMap();
		List<Field> list = new ArrayList<Field>();

		try {

			list.addAll(Arrays.asList(clz.getDeclaredFields()));
			Class sc = clz.getSuperclass();
			if (sc != Object.class) {
				list.addAll(Arrays.asList(sc.getDeclaredFields()));
			}
		} catch (Exception e) {

		}

		for (Field f : list) {
			X.Key a = f.getAnnotation(X.Key.class);
			if (a != null){
				map.put(X.KEY_ONE, f.getName());
				f.setAccessible(true);
				keyFieldMap.put(X.KEY_ONE, f);
			}
			X.Sharding s = f.getAnnotation(X.Sharding.class);
			if (s != null){
				map.put(X.KEY_SHARDING, f.getName());
				f.setAccessible(true);
				keyFieldMap.put(X.KEY_SHARDING, f);
			}
		}
	}

	/**
	 * 默认值为0的不做查询条件<br>
	 * 额外条件从另外一个map参数获得<br>
	 * boolean必须从另外一个map参数获得
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static Map<String, Object> getRefreshMap(Parsed parsed, Object obj) {

		Map<String, Object> map = new HashMap<String, Object>();

		Class clz = obj.getClass();
		try {
			for (BeanElement element : parsed.getBeanElementList()) {

				Method method = element.getMethod;
				Object value = method.invoke(obj);
				Class type = method.getReturnType();
				String property = element.getProperty();
				if (type == int.class) {
					if ((int) value != 0) {
						map.put(property, value);
					}
				} else if (type == Integer.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == long.class) {
					if ((long) value != 0) {
						map.put(property, value);
					}
				} else if (type == Long.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == double.class) {
					if ((double) value != 0) {
						map.put(property, value);
					}
				} else if (type == Double.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == float.class) {
					if ((float) value != 0) {
						map.put(property, value);
					}
				} else if (type == Float.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == boolean.class) {
					if ((boolean) value != false) {
						map.put(property, value);
					}
				} else if (type == Boolean.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == String.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type.isEnum()){
					if (value != null) {
						map.put(property, value.toString());
					}
				}else if (type == Date.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == BigDecimal.class){
					if (value != null) {
						map.put(property, value);
					}
				}else if (element.isJson) {
				
					if (value != null) {
						String str = JsonX.toJson(value);
						map.put(property, str);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	/**
	 * 默认值为0的不做查询条件<br>
	 * 额外条件从另外一个map参数获得<br>
	 * boolean必须从另外一个map参数获得
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static Map<String, Object> getQueryMap(Parsed parsed, Object obj) {

		Map<String, Object> map = new HashMap<String, Object>();

		Class clz = obj.getClass();
		try {
			for (BeanElement element : parsed.getBeanElementList()) {

				Method method = element.getMethod;
				Object value = method.invoke(obj);
				Class type = method.getReturnType();

				String property = element.getProperty();
				
				if (type == long.class) {
					if ((long) value != 0) {
						map.put(property, value);
					}
				} else if (type == Long.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == String.class) {
					if (value != null && !value.equals("")) {
						map.put(property, value);
					}
				}else if (type.isEnum()){
					if (value != null) {
						map.put(property, value.toString());
					}
				} else if (type == int.class) {
					if ((int) value != 0) {
						map.put(property, value);
					}
				} else if (type == Integer.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == double.class) {
					if ((double) value != 0) {
						map.put(property, value);
					}
				} else if (type == Double.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == float.class) {
					if ((float) value != 0) {
						map.put(property, value);
					}
				} else if (type == Float.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == boolean.class) {
					if ((boolean) value != false) {
						map.put(property, value);
					}
				} else if (type == BigDecimal.class){
					if (value != null) {
						map.put(property, value);
					}
				}else if (type == Boolean.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else if (type == Date.class) {
					if (value != null) {
						map.put(property, value);
					}
				} else {
					if (value != null) {
						map.put(property, value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("_queryMap: " + map);

		return map;

	}

	public static String getIndexClzName(Class clz) {
		String name = clz.getName();
		name = name + "Index";
		return name;
	}



	public static void parseSearch(Parsed parsed, Class clz) {

		Search pClz = (Search) clz.getAnnotation(Search.class);
		if (pClz == null)
			return;
		parsed.setSearchable(true);

		for (Field f : clz.getDeclaredFields()) {

			Search.keywords pp = (Search.keywords) f.getAnnotation(Search.keywords.class);
			if (pp != null) {
				parsed.getKeywordsList().add(f.getName());
			} else {

				Search pc = (Search) f.getAnnotation(Search.class);

				if (pc != null) {
					Class cl = f.getType();
					String name = f.getName();
					String prefix = name + ".";
					parseSearch(prefix, parsed, cl);
				} else {
					Search.tag pt = (Search.tag) f.getAnnotation(Search.tag.class);
					if (pt != null) {
						Class cl = f.getType();
						String name = f.getName();
						String prefix = name + ".";
						parseSearch(prefix, parsed, cl);

						TagParsed tag = new TagParsed();
						tag.setType(pt.type());
						tag.setField(f);
						String tagKey = pt.type().getSimpleName() + "Tag";
						tagKey = getByFirstLower(tagKey);
						tag.setTagKey(tagKey);// !!!important
						f.setAccessible(true);

						parsed.getTagMap().put(name, tag);//

					}
				}

			}

		}
	}

	private static void parseSearch(String prefix, Parsed parsed, Class clz) {
		Search pClz = (Search) clz.getAnnotation(Search.class);
		if (pClz == null)
			return;

		for (Field f : clz.getDeclaredFields()) {

			Search.keywords pp = (Search.keywords) f.getAnnotation(Search.keywords.class);
			if (pp != null) {
				parsed.getKeywordsList().add(prefix + f.getName());
			} else {
				Search pc = (Search) f.getAnnotation(Search.class);
				if (pc != null) {
					Class cl = f.getType();
					String name = f.getName();
					prefix += name + ".";
					parseSearch(prefix, parsed, cl);
				} else {
					Search.tag pt = (Search.tag) f.getAnnotation(Search.tag.class);
					if (pt != null) {
						Class cl = f.getType();
						String name = f.getName();
						prefix += name + ".";
						parseSearch(prefix, parsed, cl);

						TagParsed tag = new TagParsed();
						tag.setType(pt.type());
						tag.setField(f);
						String tagKey = pt.type().getSimpleName() + "Tag";
						tagKey = getByFirstLower(tagKey);
						tag.setTagKey(tagKey);// !!!important
						f.setAccessible(true);

						parsed.getTagMap().put(name, tag);//

						// parsed.getKeywordsList().add(prefix + name);
					}
				}
			}
		}
	}
	
	
	public static <T> void sort(Class<T> clz, List<T> list,String property, boolean isAsc) {
		list.sort(
				(a, b) -> compare(clz, property, isAsc, a,b)
					);
	}
	
	private static <T> int compare(Class clz, String orderBy, boolean isAsc, T a, T b){
		try {
			int scValue = isAsc ? 1 : -1;
			Field field = clz.getDeclaredField(orderBy);
			field.setAccessible(true);
			Object valueA = field.get(a);
			Object valueB = field.get(b);
			if (field.getType() == String.class){
				int intA = valueA.toString().charAt(0);
				int intB = valueB.toString().charAt(0);
				if (intA > intB)
					return 1 * scValue;
				if (intA < intB)
					return -1 * scValue;
				return 0;
			}else {
				BigDecimal bdA = new BigDecimal(valueA.toString().toCharArray());
				BigDecimal bdB = new BigDecimal(valueB.toString().toCharArray());
				return bdA.compareTo(bdB) * scValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static void sort( List<Map<String,Object>> list,String property, boolean isAsc) {
		list.sort(
				(a, b) -> compare(property, isAsc, a,b)
					);
	}

	private static int compare(String property, boolean isAsc, Map<String, Object> a, Map<String, Object> b) {

		int scValue = isAsc ? 1 : -1;
		
		Object valueA = a.get(property);
		Object valueB = b.get(property);
		
		if (valueA instanceof String) {
			int intA = valueA.toString().charAt(0);
			int intB = valueB.toString().charAt(0);
			if (intA > intB)
				return 1 * scValue;
			if (intA < intB)
				return -1 * scValue;
			return 0;
		}else {
			BigDecimal bdA = new BigDecimal(valueA.toString().toCharArray());
			BigDecimal bdB = new BigDecimal(valueB.toString().toCharArray());
			return bdA.compareTo(bdB) * scValue;
		}
		
	}
	
	
	public static String mapper(String sql, Parsed parsed) {
		
		if (parsed.isNoSpec())
			return sql;
		
		sql = mapperName(sql, parsed);
		
		for (String property : parsed.getPropertyMapperMap().keySet()){
			sql = sql.replaceAll(property, parsed.getMapper(property));
		}
		return sql;
	}
	
	public static String mapperName(String sql, Parsed parsed) {
		
		String clzName = parsed.getClzName();
		clzName = BeanUtil.getByFirstLower(clzName);
		String tableName = parsed.getTableName();
		
		return mapperName (sql, clzName, tableName);
	}

	public static String mapperName(String sql, String clzName, String tableName) {
		
		if (sql.endsWith(clzName)){
			sql += " ";
		}
		sql = sql.replace(" " +clzName+" ", " "+tableName+" ");
		
		return sql;
	}

}
