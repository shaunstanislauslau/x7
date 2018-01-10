package x7.tool;

import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;

import x7.config.ConfigBuilder;
import x7.core.config.Configs;
import x7.core.util.StringUtil;
import x7.tool.bean.BeanTemplate;
import x7.tool.generator.KeyGenerator3;

public class App3 {

	
	public static void main(String[] args) {
		
		ConfigBuilder.build(false, "generated", "../config", null);
		
		Config.BASE = "../"+Configs.getString("x7.bean.project") + "/generated/" ;
		Config.PKG = Configs.getString("x7.bean.package");
		
		if (StringUtil.isNullOrEmpty(Config.BASE))
			throw new RuntimeException("can't find configKey of [generated/*] 'x7.bean.project'");
		
		if (StringUtil.isNullOrEmpty(Config.PKG))
			throw new RuntimeException("can't find configKey of [generated/*] 'x7.bean.package'");
		
		String projectPath = Config.BASE;
		String template_path = "template/beankey3.vm";
		
		Template template = Velocity.getTemplate(template_path);
		
		List<BeanTemplate> list = BeanKey3.createTemplateList();
		
		for (BeanTemplate beanTemplate : list) {
			KeyGenerator3.generate(projectPath, beanTemplate, template);
		}
	}
}
