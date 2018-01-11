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
