package x7.core.tx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TxTraceManager {

	private final static Map<String, TxTraceable> map = new ConcurrentHashMap<String, TxTraceable>();

	public static Map<String, TxTraceable> getMap() {
		return map;
	}
	
	public static void add(TxTraceable obj) {
		String key = obj.getTxKeyed().key();
		map.put(key, obj);
	}
	
	public static TxTraceable get(String key) {
		return map.get(key);
	}
	
	public static TxTraceable remove(String key){
		return map.remove(key);
	}
}
