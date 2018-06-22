package x7.repository.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

public class JedisTest {

	public static void test (JedisPool pool, String exception){
		
		Jedis jedis = pool.getResource();
		if (Objects.isNull(jedis))
			throw new RuntimeException(exception);
		pool.returnBrokenResource(jedis);
	}
	
}
