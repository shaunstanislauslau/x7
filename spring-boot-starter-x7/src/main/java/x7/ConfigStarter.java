package x7;

import x7.config.ConfigBuilder;
import x7.core.util.StringUtil;

public class ConfigStarter {

	public ConfigStarter(boolean centralized, String configSpace, String localAddress, String remoteAddress){
		if (!centralized) {
			if (StringUtil.isNullOrEmpty(localAddress) || StringUtil.isNullOrEmpty(configSpace))
				return;
		}
		ConfigBuilder.build(centralized, configSpace, localAddress, remoteAddress);

	}
}
