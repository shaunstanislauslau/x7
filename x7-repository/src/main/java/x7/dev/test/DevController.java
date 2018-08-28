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
package x7.dev.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x7.core.config.Configs;
import x7.core.util.StringUtil;
import x7.core.web.ViewEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/dev")
public class DevController {


	@RequestMapping("test")
	public ViewEntity test(HttpServletRequest req) {
		
		boolean isDev = Configs.isTrue("x7.developing");
		
		if (!isDev) {
			return ViewEntity.toast("NOT DEV OR NOT TEST");
		}
		
		Map<String,String> map = ServletModelCreator.createMap(req);
		
		String simpleName = map.get("class.name");
		if (StringUtil.isNullOrEmpty(simpleName)){
			return ViewEntity.toast("lose express: class.name=");
		}
		
		
//		CriteriaBuilder.Fetchable criteriaBuilder = CriteriaBuilder.buildFetchable(isDev, map);
//		
//		Criteria.Fetch criteriaJoinable = (Criteria.Fetch) criteriaBuilder.get();
//
//		List<Map<String,Object>> list = SqlRepository.getInstance().list(criteriaJoinable);

		return ViewEntity.ok(null);
	}
}
