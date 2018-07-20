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
package x7.dev.test;

import org.apache.log4j.Logger;
import x7.core.util.JsonX;
import x7.core.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class ServletModelCreator {
	private static Logger logger = Logger.getLogger(ServletModelCreator.class.getName());


	public static Map<String, String> createMap(HttpServletRequest request) {

		Map<String, String> map = new HashMap<String, String>();

		Map<String, String[]> inputs = request.getParameterMap();

		for (String key : inputs.keySet()) {

			String[] value = inputs.get(key);
			String v = value[0].trim();

			if (StringUtil.isNullOrEmpty(v))
				continue;

			key = key.replace("<", "&lt").replace(">", "&gt");
			v = v.replace("<", "&lt").replace(">", "&gt");

			map.put(key, v);
		}

		return map;
	}


	public static Map<String, Object> createMapBySimpleHttp(HttpServletRequest request) {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = jb.toString();
		str = str.trim();

		System.out.println("createMapBySimpleHttp, str:  " + str);

		Map<String, Object> map = null;

		if (str.startsWith("{") && str.endsWith("}")) {

			str = str.replace("<", "&lt").replace(">", "&gt");

			map = JsonX.toMap(str);

			System.out.println("createMapBySimpleHttp, map:  " + map);

			return map;
		}

		map = new HashMap<String, Object>();

		if (str.contains("=")) {
			String[] arr = str.split("&");
			for (String kv : arr) {
				if (kv.contains("=")) {
					String[] kvArr = kv.split("=");
					String v = kvArr[1];
					v = v.replace("<", "&lt").replace(">", "&gt");
					map.put(kvArr[0], v);
				}
			}
		}

		System.out.println("createMapBySimpleHttp, map:  " + map);

		return map;
	}

	public static <T> T create(HttpServletRequest request, Class<T> clz) {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = jb.toString();
		str = str.trim();

		if (str.startsWith("{") && str.endsWith("}")) {

			str = str.replace("<", "&lt").replace(">", "&gt");

			T t = JsonX.toObject(str, clz);

			System.out.println("ServletModelCreator.create:  " + t);

			return t;
		}

		return null;
	}
}