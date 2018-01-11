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
package x7.core.event;

import java.util.TreeMap;

/**
 * 游戏事件派发器的简单实现<br>
 * 简化版的实现,能满足ARPG游戏的需求
 * <br>
 * 
 * @author Wangyan
 * 
 */
public class EventDispatcher implements IEventDispatcher {
	

//	public EventDispatcher() {
//		addEventListeners();
//	}

//	public abstract void addEventListeners();

//	@Override
	public static void addEventListener(String eventType, IEventListener listener) {

		TreeMap<String, IEventListener> listenerMap = listenersMap
				.get(eventType);

		if (listenerMap == null) {
			listenerMap = new TreeMap<String, IEventListener>();
			listenersMap.put(eventType, listenerMap);
		} 
		String key = createKey(listener);
		if (! (listenerMap.containsKey(key))){
			listenerMap.put(key, listener);
		}


	}

//	@Override
	public static void removeEventListener(String eventType,
			IEventListener listener) {
		TreeMap<String, IEventListener> listenerMap = listenersMap
				.get(eventType);

		if (listenerMap != null) {
			listenerMap.remove(createKey(listener));
			if (listenerMap.size() == 0) {
				listenersMap.remove(eventType);
			}
		}

	}

//	@Override
	public static void dispatch(IEvent event) {
		TreeMap<String, IEventListener> listenerMap = listenersMap
				.get(event.getType());
		if (listenerMap == null)
			return;
		for (IEventListener listener : listenerMap.values()) {
			if (listener != null) {
				try{
					listener.handle(event);
				} catch(Exception e) {
					System.out.println("\nException, while handling event: " + event + "\n");
					e.printStackTrace();
				}
			}
		}

	}
	/**
	 * 创建KEY
	 */
	private static String createKey(IEventListener listener){
		if (listener.getClass().getName().contains("IEventListener")){
			return listener.getClass().getName()+listener.hashCode();
		}
		return listener.getClass().getName();
	}

}
