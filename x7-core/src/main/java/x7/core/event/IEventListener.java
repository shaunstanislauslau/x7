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

import java.util.EventListener;


/**
 * <li>无状态事件监听器</li><br>
 * 多线程事件框架,必须考虑逻辑上的先后顺序，多任务导致的可能的冲突<br>
 * 在处理事件上，框架保证事件的先进先出
 * @author Wangyan
 *
 */
public interface IEventListener extends EventListener{

	/**
	 * 监听器被设计为无状态的服务端模式，不根据实例数重复添加监听器<br>
	 * 因此，任何对象必须从event里获得，举例：<br>
	 * HeroEvent he = (HeroEvent) event;<br>
	 * Scene scene = he.getHero().getScene();<br>
	 * 特别提醒： <br>
	 * 内部类可以工作，但设计上导致不可以直接访问外部类的属性,语法上无法检测<br>
	 * 不建议使用匿名内部类
	 * @param event
	 */
	void handle(IEvent event);

}



