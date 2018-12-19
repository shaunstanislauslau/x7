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
package x7.config;

import x7.config.zk.ZkBase;
import x7.core.config.Configs;
import x7.core.util.StringUtil;

public class ConfigBuilder {

	private static ConfigBuilder instance;
	public static ConfigBuilder newInstance(){
		if (instance == null){
			instance = new ConfigBuilder();
		}
		return instance;
	}
	
	public static void build(boolean centralized, String configSpace, String localAddress, String remoteAddress){
		if (instance == null){
			instance = new ConfigBuilder();
			init(centralized,configSpace, localAddress, remoteAddress);
		}
	}
	
	private static void init(boolean centralized, String configSpace, String localAddress, String remoteAddress) {
		
		Configs.setConfigSpace(configSpace);

		if (StringUtil.isNullOrEmpty(remoteAddress) && StringUtil.isNullOrEmpty(localAddress))
			return;

		if (centralized){
			if (centralized && StringUtil.isNotNull(remoteAddress)){
				ZkBase.getInstance().init(remoteAddress);
				ZkBase.getInstance().add(ConfigKeeper.getInstance());
			}
			return;
		}

		if (StringUtil.isNotNull(localAddress)){
			Configs.localAddress = localAddress;
			TextParser.getInstance().load(localAddress, configSpace);
		}else if (StringUtil.isNotNull(remoteAddress)){
			ZkBase.getInstance().init(remoteAddress);
			ZkBase.getInstance().add(ConfigKeeper.getInstance());
		}

	}
	
}
