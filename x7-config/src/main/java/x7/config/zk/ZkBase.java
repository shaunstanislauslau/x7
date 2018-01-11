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
package x7.config.zk;

import java.util.List;

import org.apache.zookeeper.CreateMode;

import x7.core.keeper.IKeeper;

/**
 * ZkBase singleton 为基础的整个服务器集群的一致性而设置<br>
 * 如果连接其他Zk集群推荐创建新的ZkClient实例<br>
 * 
 * @author Sim
 *
 */
public class ZkBase {

	private static ZkClient zkClient = null;
	
	private static ZkBase instance = null;
	
	public static ZkBase getInstance() {
		if (instance == null) {
			instance = new ZkBase();
		}
		return instance;
	}
	
	public static void init(String address){
		zkClient = new ZkClient(address);
	}

	public void add(IKeeper keeper) {
		this.zkClient.add(keeper);
	}
	
	public void create(String path, String value, CreateMode mode) {
		this.zkClient.create(path, value, mode);
	}
	
	public void refresh(String path, String value) {
		this.zkClient.refresh(path, value);
	}
	
	public void remove(String path) {
		this.zkClient.remove(path);
	}
	
	public String get(String path) {
		return this.zkClient.get(path);
	}
	
	public List<String> getChildren(String path) {
		return this.zkClient.getChildren(path);
	}
}
