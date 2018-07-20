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

import org.springframework.context.ApplicationEvent;
import x7.core.event.Event;
import x7.core.event.EventOwner;
import x7.core.util.JsonX;

import java.io.Serializable;


public class MessageEvent<T> extends ApplicationEvent implements Event, Serializable{

	private static final long serialVersionUID = -4325175147748849706L;
	private String type; //topic
	private String body;
	private long reTimes;
	private String tag;


	public MessageEvent(Object obj){
		super(obj);
		this.type = obj.toString();
	}

	public String getType() {
		return type;
	}

	@Override
	public EventOwner getOwner() {
		return null;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getReTimes() {
		return reTimes;
	}

	public void setReTimes(long reTimes) {
		this.reTimes = reTimes;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public T get(){
		return (T) JsonX.toObjectByClassName(this.body,this.tag);
	}

	@Override
	public String toString() {
		return "MessageEvent [type=" + type + ", body=" + body + ", reTimes=" + reTimes + ", tag=" + tag
				+ "]";
	}

}
