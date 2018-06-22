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
package x7.core.mq;

import x7.core.event.IEvent;

import java.io.Serializable;

/**
 * 
 * 收到来自消息中间件的消息后, 需要派发(EventDispatcher.dispatch(event)) <br>
 *
 */
public class MessageEvent implements IEvent, Serializable{

	private static final long serialVersionUID = -2529175147748847706L;
	private String type; //topic
	private String content;// IEventOwner有时候无法自动转JSON,
	private int reTimes;
	private String tag;
	
	public MessageEvent(String type, String content){
		this.type = type;
		this.content = content;
	}

	@Override
	public String getType() {
		return type;
	}

	/**
	 * IEventOwner有时候无法自动转JSON,<br>
	 * 复杂的对象无法用字符串传输
	 * 
	 */
	public String getContent() {
		return content;
	}


	public int getReTimes() {
		return reTimes;
	}

	public void setReTimes(int reTimes) {
		this.reTimes = reTimes;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "MessageEvent [type=" + type + ", content=" + content + ", reTimes=" + reTimes + ", tag=" + tag
				+ "]";
	}
}
