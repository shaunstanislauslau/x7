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
package x7.core.config;

import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;

import x7.core.util.KeyUtil;

public class Configs {

	private static Map<String, Object> map = new ConcurrentHashMap<String, Object>();

	private static String space = null;
	
	public static String localAddress;
	public static String remoteAddress;
	
	public static void setConfigSpace(String configSpace) {
		map.put("x7.config.space", configSpace);
	}
	
	public static Map<String, Object> referMap(String s) {
		space = s;
		return map;
	}
	
	private static String getKey(String key){
		if (space == null || space.trim().equals(""))
			return key;
		return space+"."+key;
	}

	public static Object get(String keyStr) {
		
		keyStr = getKey(keyStr);
		
		List<String> keyList = KeyUtil.getKeyList(keyStr);

		Map<String, Object> tempMap = map;
		for (String key : keyList) {
			Object obj = tempMap.get(key);
			if (obj == null)
				return tempMap = null;
			if (obj instanceof Map) {
				tempMap = (Map<String, Object>) obj;
			} else {
				return obj;
			}
		}
		return tempMap;
	}

	public static int getIntValue(String key) {
		
		Integer value = 0;

		try {
			value = Integer.valueOf(get(key) + "");
		} catch (MissingResourceException mre) {
			String err = "请检查配置文件config/*.txt, 缺少key:" + key;
			System.err.println(err);
			mre.printStackTrace();
		} catch (Exception e) {
			String err = "请检查配置文件config/*.txt, 发现了:" + key + "=" + map.get(key);
			System.err.println(err);
			e.printStackTrace();
		}
		return value;
	}

	public static Map<String, Object> getMap(String key) {

		Object obj = get(key);
		if (obj instanceof Map)
			return (Map<String, Object>) obj;
		return null;
	}

	public static String getString(String key) {

		String value = "";

		try {
			value = get(key) + "";
		} catch (MissingResourceException mre) {
			String err = "请检查配置文件config/*.txt, 缺少key:" + key;
			System.err.println(err);
			mre.printStackTrace();

		} catch (Exception e) {
			String err = "请检查配置文件config/*.txt, 发现了:" + key + "=" + map.get(key);
			System.err.println(err);
			e.printStackTrace();

		}
		return value;
	}

	public static long getLongValue(String key) {

		Long value = 0L;

		try {
			value = Long.valueOf(get(key) + "");
		} catch (MissingResourceException mre) {
			String err = "请检查配置文件config/*.txt, 缺少key:" + key;
			System.err.println(err);
			mre.printStackTrace();

		} catch (Exception e) {
			String err = "请检查配置文件config/*.txt, 发现了:" + key + "=" + map.get(key);
			System.err.println(err);
			e.printStackTrace();

		}
		return value;
	}

	public static boolean isTrue(String key) {

		String value = "";

		try {
			value = get(key) + "";
			return Boolean.parseBoolean(value);
		} catch (MissingResourceException mre) {
			String err = "请检查配置文件config/*.txt, 缺少key:" + key;
			System.err.println(err);
			mre.printStackTrace();

		} catch (Exception e) {
			String err = "请检查配置文件config/*.txt, 发现了:" + key + "=" + map.get(key);
			System.err.println(err);
			e.printStackTrace();

		}
		return false;
	}

}