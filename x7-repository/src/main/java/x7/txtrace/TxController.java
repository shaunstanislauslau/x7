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
package x7.txtrace;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x7.core.tx.TxTraceable;
import x7.core.web.ViewEntity;

@RestController
@RequestMapping(TxTraceable.ROOT)
public class TxController {

	@RequestMapping(TxTraceable.CONFIRM + "{key}")
	public ViewEntity confirm(@PathVariable("key") String key){
		try{
			boolean flag = TxTracer.confirm(key);
			return ViewEntity.ok(flag);
		}catch(Exception e){
			return ViewEntity.ok(false);
		}
	}
	
	@RequestMapping(TxTraceable.CANCEL + "{key}")
	public ViewEntity cancel(@PathVariable("key") String key){
		
		try{
			boolean flag = TxTracer.cancel(key);
			return ViewEntity.ok(flag);
		}catch(Exception e){
			return ViewEntity.ok(false);
		}
	}
	
	
	@RequestMapping(TxTraceable.OK + "{key}")
	public ViewEntity ok(@PathVariable("key") String key){
		
		try{
			boolean flag = TxTracer.ok(key);
			return ViewEntity.ok(flag);
		}catch(Exception e){
			return ViewEntity.ok(false);
		}
	}
	
	@RequestMapping(TxTraceable.FAIL + "{key}")
	public ViewEntity fail(@PathVariable("key") String key){
		
		try{
			boolean flag = TxTracer.fail(key);
			return ViewEntity.ok(flag);
		}catch(Exception e){
			return ViewEntity.ok(false);
		}
	}
}
