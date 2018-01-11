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
package x7.repository.redis;

import java.util.List;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import x7.core.config.Configs;

public class JedisConnector_Cache3 {

	private static JedisPool pool;  
	
	private static JedisConnector_Cache3 instance;
	
	public static JedisConnector_Cache3 getInstance(){
		if (instance == null){
			instance = new JedisConnector_Cache3();
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxTotal(Configs.getIntValue("x7.redis.max"));
			config.setMaxIdle(Configs.getIntValue("x7.redis.idle"));
			config.setJmxEnabled(true);
			config.setJmxNamePrefix("redis-cahce3");
			config.setTestOnBorrow(true);
			
			pool = new JedisPool( config, Configs.getString("x7.redis.ip.cache3"), Configs.getIntValue("x7.redis.port.cache3"));  //6379
		}
		return instance;
	}
	
	private JedisConnector_Cache3(){
		
	}
	
	public Jedis get(){
		return pool.getResource();
	}
	
	public void close(Jedis jedis){
		pool.returnResource(jedis);
	}
	
	public void closeBroken(Jedis jedis){
		pool.returnBrokenResource(jedis);
	}
	
	public void set(String key, String value){
		if (key == null || key.equals("") ) 
			return;
		set(key.getBytes(),value.getBytes());
	}
	
	public void set(byte[] key, byte[] value){
		
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return;
			jedis.set(key,value);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
	}
	
	public void set(byte[] key, byte[] value, int validSeconds){
		
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return;
			jedis.set(key,value);
			jedis.expire(key, validSeconds);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
	}
	
	public String get(String key){
		
		String str = null;
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return null;
			str = jedis.get(key);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
		return str;
	}
	
	public List<byte[]> mget(byte[][] keyArr){
		
		if (keyArr == null || keyArr.length == 0)
			return null;
		
		List<byte[]> byteList = null;
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return null;
			byteList = jedis.mget(keyArr);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
		return byteList;
	}
	
	public byte[] get(byte[] key){
		
		byte[] value = null;
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return null;
			value = jedis.get(key);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
		return value;
	}
	
	public void delete(byte[] key){
		Jedis jedis = null;
		try{
			jedis = get();
			if (jedis == null)
				return;
			jedis.del(key);
			pool.returnResource(jedis);
		}catch(Exception e){
			pool.returnBrokenResource(jedis);
		}
		
	}
	

}
