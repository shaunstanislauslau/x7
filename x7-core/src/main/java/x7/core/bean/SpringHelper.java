package x7.core.bean;

//import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SpringHelper implements ApplicationContextAware {

//	private static Logger logger = Logger.getLogger(SpringHelper.class);

	private static ApplicationContext applicationContext;

	private static Map<Method, String> mappingMap = new HashMap<Method, String>();

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		applicationContext = arg0;
		getMapPaths();
//		logger.info("SpringHelper started, static methods[getContext,getObject,getRequestMapping] ");
	}

	public static Object getObject(String beanName) {
		Object object = null;
		try {
			object = applicationContext.getBean(beanName);
		} catch (Exception e) {
//			logger.error(e);
		}
		return object;
	}

	public static ApplicationContext getContext() {
		return applicationContext;
	}

	public static <T> T getObject(Class<T> clazz) {
		try {
			return applicationContext.getBean(clazz);
		} catch (Exception e) {
//			logger.error(e);
		}
		return null;
	}
	
	public static String getRequestMapping(Method method) {
		return mappingMap.get(method);
	}

	private static void getMapPaths() {
		RequestMappingHandlerMapping rmhp = getObject(RequestMappingHandlerMapping.class);
		if (rmhp != null) {
			Map<RequestMappingInfo, HandlerMethod> map = rmhp.getHandlerMethods();
			for (RequestMappingInfo info : map.keySet()) {
				String mapping = info.getPatternsCondition().toString().replace("[", "").replace("]", "");
				HandlerMethod hm = map.get(info);
				mappingMap.put(hm.getMethod(), mapping);
			}
		}
	}

}