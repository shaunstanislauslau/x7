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

import x7.core.mq.MessageEvent;

import java.util.Objects;
import java.util.TreeMap;

public class EventDispatcherX extends EventDispatcher{

	public static void dispatch(MessageEvent event)  {
		TreeMap<String, IEventListener> listenerMap = listenersMap
				.get(event.getType());
		if (listenerMap == null)
			return;
		
		System.out.println(event.getType() + ", listenerMap.size = " + listenerMap.size());
		
		for (IEventListener listener : listenerMap.values()) {
			if (listener != null) {
				try{
					if (event.getReTimes() == 0){
						listener.handle(event);
					}else if ( !Objects.isNull(event.getTag()) && event.getTag().equals(listener.getClass().getName())){
						listener.handle(event);
					}
					
				}catch (Exception e){
					
					MessageListenerException mle = new MessageListenerException("Exception, listener: " + listener  + ", event: " + event);
					mle.setTag(listener.getClass().getName());
					
					throw mle;
				}
			}
		}

	}
}
