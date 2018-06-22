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
import x7.config.ConfigBuilder;
import x7.core.config.Configs;
import x7.core.util.StringUtil;
import x7.tool.bean.BeanTemplate;
import x7.tool.generator.KeyGenerator;

import java.util.Set;

public class App {

	
	public static void main(String[] args) {
		
		ConfigBuilder.build(false, "generated", "../config", null);
		
		Config.BASE = "../"+Configs.getString("x7.bean.project") + "/generated/" ;
		Config.PKG = Configs.getString("x7.bean.package");
		
		if (StringUtil.isNullOrEmpty(Configs.getString("x7.bean.project")))
			throw new RuntimeException("can't find configKey of [generated/*] 'x7.bean.project'");
		
		if (StringUtil.isNullOrEmpty(Config.PKG))
			throw new RuntimeException("can't find configKey of [generated/*] 'x7.bean.package'");
		
		String projectPath = Config.BASE;
		String template_path = "template/beankey.vm";
		
		Template template = Velocity.getTemplate(template_path);
		
		Set<BeanTemplate> list = BeanKey.createTemplateSet();

		KeyGenerator.generate(projectPath, list, template);

	}
}
