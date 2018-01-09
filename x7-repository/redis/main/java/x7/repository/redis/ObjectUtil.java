package x7.repository.redis;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import x7.core.util.JsonX;
import x7.core.web.Pagination;

public class ObjectUtil {

	public static byte[] toBytes(Object obj) throws UnsupportedEncodingException {
		if (obj == null)
			return null;
		String str = JsonX.toJson(obj);
		byte[] bytes = null;
		bytes = str.getBytes("UTF-8");
		return bytes;
	}

	public static <T> T toObject(byte[] bytes, Class<T> clz) {
		if (bytes == null)
			return null;
		try {
			String str = new String(bytes, "UTF-8");
			T t = JsonX.toObject(str, clz);
			return t;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	
	public static List<Map<String,Object>> toMapList(byte[] bytes) {
		if (bytes == null)
			return null;
		try {
			String str = new String(bytes, "UTF-8");
			return (List)JsonX.toList(str, Map.class);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}


	public static <T> List<T> toList(byte[] bytes, Class<T> clz) {
		if (bytes == null)
			return null;
		try {
			String str = new String(bytes, "UTF-8");
			List<T> list = JsonX.toList(str, clz);
			return list;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T> Pagination<T> toPagination(byte[] bytes, Class<T> clz) {
		if (bytes == null)
			return null;
		try {
			String str = new String(bytes, "UTF-8");
			Pagination<T> pagination = JsonX.toObject(str, Pagination.class);
			List tempList = pagination.getList();
			if (!tempList.isEmpty()) {
				if (!JsonX.isJsonable(clz))
					return pagination;
				
				List<T> list = new ArrayList<T>();
				for (Object jsonObject : tempList) {
					T t = JsonX.toObject(jsonObject, clz);
					list.add(t);
				}
				pagination.getList().clear();
				pagination.getList().addAll(list);
			}
			return pagination;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

}
