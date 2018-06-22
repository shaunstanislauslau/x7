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
package x7.tool;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import x7.tool.bean.BeanTemplate;
import x7.tool.generator.KeyGenerator3;

import java.util.List;

public class App3 {

	
	public static void main(String[] args) {
//
//		ConfigBuilder.build(false, "generated", "../config", null);
//
//		Config.BASE = "../"+Configs.getString("x7.bean.project") + "/generated/" ;
//		Config.PKG = Configs.getString("x7.bean.package");
//
//		if (StringUtil.isNullOrEmpty(Config.BASE))
//			throw new RuntimeException("can't find configKey of [generated/*] 'x7.bean.project'");
//
//		if (StringUtil.isNullOrEmpty(Config.PKG))
//			throw new RuntimeException("can't find configKey of [generated/*] 'x7.bean.package'");
		
		String projectPath = Config.BASE;
		String template_path = "template/beankey3.vm";
		
		Template template = Velocity.getTemplate(template_path);
		
		List<BeanTemplate> list = BeanKey3.createTemplateList();
		
		for (BeanTemplate beanTemplate : list) {
			KeyGenerator3.generate(projectPath, beanTemplate, template);
		}
	}
}
