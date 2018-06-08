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
package x7.repository.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Tx仅仅是本地事务，不支持分布式事务<br>
 * 同一事务下，严格禁止切换线程，必须同步执行<br>
 * 使用时，优先考虑乐观锁, refresh(obj, map);<br>
 * <br>
 * Sample：<br>
 * <br>
 * <hr>
 * <br>
 * 
 * Tx.begin(); <br>
 * (ok) Tx.commit(); <br>
 * (exception) Tx.rollback(); <br>
 * <br>
 * 
 * @author Sim
 *
 */
public class Tx {

	private static Map<String, Connection> connMap = new ConcurrentHashMap<>();
	private static Map<String, List<Statement>> map = new ConcurrentHashMap<String, List<Statement>>();

	private static Map<String,String> kkMap = new ConcurrentHashMap<>();
	
	private static List<Statement> remove() {
		String key = getKey();
		return map.remove(key);
	}
	
	
	protected static void add(Connection connection){
		String key = getKey();
		connMap.put(key, connection);
	}
	
	protected static Connection getConnection(){
		String key = getKey();
		return connMap.get(key);
	}

	protected static boolean isNoBizTx() {
		String key = getKey();
		return !map.containsKey(key);
	}

	protected static void add(Statement stmt) {
		String key = getKey();
		List<Statement> list = map.get(key);
		list.add(stmt);
	}

	public static void begin() {
		String key = getKey();
		map.put(key, new ArrayList<Statement>());
	}

	
	public static void commit() {
		
		String key = getKey();
		Connection connection = connMap.remove(key);
		
		if (connection == null){
			remove();
			return;
		}
		
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			List<Statement> list = remove();
			if (list != null) {
				for (Statement stmt : list) {
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static void rollback() {
		
		String key = getKey();
		Connection connection = connMap.remove(key);
		
		if (connection == null){
			remove();
			return;
		}
		
		try {
			connection.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
			List<Statement> list = remove();
			if (list != null) {
				for (Statement stmt : list) {
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getKey(){
		long threadId = Thread.currentThread().getId();
		String key = String.valueOf(threadId);
		String xKey = kkMap.get(key);
		if (Objects.isNull(xKey))
			return key;
		return xKey;
	}
	
	public static class X {
		
		private static List<Statement> remove(String xKey) {
			String key = xKey;
			return map.remove(key);
		}
		
		public static void begin(String xKey) {
			long threadId = Thread.currentThread().getId();
			String key = String.valueOf(threadId);
			kkMap.put(key, xKey);
			map.put(xKey, new ArrayList<Statement>());
		}

		
		public static void commit(String xKey) {
			
			String key = xKey;
			Connection connection = connMap.remove(key);
			
			if (connection == null){
				remove(key);
				return;
			}
			
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("TX commit fail, key = " + xKey);
			}finally{
				
				List<Statement> list = remove(key);
				if (list != null) {
					for (Statement stmt : list) {
						try {
							stmt.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		public static void rollback(String xKey) {
			
			String key = xKey;
			Connection connection = connMap.remove(key);
			
			if (connection == null){
				remove(key);
				return;
			}
			
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("TX rollback fail, key = " + xKey);
			}finally{
				
				List<Statement> list = remove(key);
				if (list != null) {
					for (Statement stmt : list) {
						try {
							stmt.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
