package x7.repository.redis;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import x7.core.bean.BeanSerial;
import x7.core.repository.ISerialWR;
import x7.repository.exception.PersistenceException;


public class PersistenceUtil {

	public static byte[] toBytes(Object obj){
		if (obj == null)
			return null;
		
		String clzName = obj.getClass().getName();
		ISerialWR wr = BeanSerial.get(clzName);
		if (wr != null) {
			try {
				ByteBuffer buffer = wr.write(obj);
				buffer.flip();
				return buffer.array();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			return ObjectUtil.toBytes(obj);
		} catch (UnsupportedEncodingException e) {
			throw new PersistenceException(e.getMessage());
		}
	}
	
	public static <T> T toObject(Class<T> clz, byte[] bytes){
		if (bytes == null)
			return null;
		
		String clzName = clz.getName();
		ISerialWR wr = BeanSerial.get(clzName);
		if (wr != null) {
			try {
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				return wr.read(buffer);
			} catch (Exception e) {
				System.out.println("toObject(Class<T> clz, byte[] bytes) 1-------------> " +  clz.getName());
				e.printStackTrace();
				return null;
			}
		}
		
		return ObjectUtil.toObject(bytes, clz);
	}
	
	public static List<Map<String,Object>> toMapList(byte[] bytes){
		if (bytes == null)
			return null;
		
		return ObjectUtil.toMapList(bytes);
	}
	
}
