package x7.dev.test;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import x7.core.util.JsonX;
import x7.core.util.StringUtil;

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

}