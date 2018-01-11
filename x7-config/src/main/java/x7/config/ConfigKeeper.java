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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import x7.core.config.Configs;
import x7.core.keeper.IKeeper;
import x7.core.type.DataEventType;


public class ConfigKeeper implements IKeeper{

	private  Map<String, Object> map = Configs.referMap(null);
	
	private static ConfigKeeper instance;
	public static ConfigKeeper getInstance(){
		if (instance == null){
			instance = new ConfigKeeper();
		}
		return instance;
	}
	
	public Map<String, Object> getMap(){
		return map;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onChanged(DataEventType type, List<String> keyList, Object obj){
		if (! CONFIG_ROOT.equals(keyList.get(0)))
			return;
		keyList.remove(0);
		
		String configSpace = Configs.getString("x7.config.space");
		if (! configSpace.equals(keyList.get(0)))
			return;
		keyList.remove(0);
		
		int size = keyList.size();
		if (size == 0)
			return;
		Map<String, Object> mapObject = map;
		int length = size - 1;
		for (int i = 0; i < length; i++) {
			String key = keyList.get(i);
			
			Object o = mapObject.get(key);
			if (o == null){
				o = new ConcurrentHashMap<String,Object>();
				mapObject.put(key, o);
			}
			mapObject = (Map<String, Object>) o;
		}
		
		switch (type){
		case CREATE:
			mapObject.put(keyList.get(length), obj);
			break;
		case REFRESH:
			mapObject.put(keyList.get(length), obj);
			break;
		case REMOVE:
			mapObject.remove(keyList.get(length));
			break;
		}
	}
	
	
}
