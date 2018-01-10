package x7.dev.test;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.config.Configs;
import x7.core.util.StringUtil;
import x7.core.web.ViewEntity;
import x7.repository.Repositories;


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
		
		String simpleName = map.get(CriteriaBuilder.CLASS_NAME);
		if (StringUtil.isNullOrEmpty(simpleName)){
			return ViewEntity.toast("lose express: class.name=");
		}
		
		
		CriteriaBuilder.Fetchable criteriaBuilder = CriteriaBuilder.buildFetchable(isDev, map);
		
		Criteria.Fetch criteriaJoinable = (Criteria.Fetch) criteriaBuilder.get();

		List<Map<String,Object>> list = Repositories.getInstance().list(criteriaJoinable);

		return ViewEntity.ok(list);
	}
}
