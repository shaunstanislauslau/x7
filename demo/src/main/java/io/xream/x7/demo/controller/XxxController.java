package io.xream.x7.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x7.core.bean.Criteria;
import x7.core.bean.CriteriaBuilder;
import x7.core.bean.condition.InCondition;
import x7.core.bean.condition.RefreshCondition;
import x7.core.web.Pagination;
import x7.core.web.ViewEntity;
import io.xream.x7.demo.CatRO;
import io.xream.x7.demo.Cat;
import io.xream.x7.demo.CatRepository;
import io.xream.x7.demo.CatTest;
import io.xream.x7.demo.CatTestRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/xxx")
public class XxxController {

	@Autowired
	private CatTestRepository repository;// sample

	@Autowired
	private CatRepository catRepository;// sample

	@RequestMapping("/refresh")
	public ViewEntity refreshByCondition(@RequestBody Cat cat){

//		CriteriaBuilder builder = CriteriaBuilder.buildCondition();
//		builder.and().eq("type","NL");
//		builder.and().eq("id",2);
//
//		CriteriaCondition criteriaCondition = builder.get();

//		this.catRepository.create(cat);

		RefreshCondition<Cat> refreshCondition = new RefreshCondition<Cat>(null);
		refreshCondition.and().eq("type","NL");
		refreshCondition.refresh("test=test+1002");


		this.catRepository.refresh(cat);

//		if (true){
//			throw new RuntimeException("xxxxxxxxxxxxxxxxxxxx");
//		}

		return ViewEntity.ok();
	}

	@RequestMapping("/test")
	public ViewEntity test(@RequestBody CatRO ro) {


		
		{// sample, send the json by ajax from web page
			Map<String, Object> catMap = new HashMap<>();
			catMap.put("id", "");
//			catMap.put("catFriendName", "");
//			catMap.put("time", "");

			Map<String, Object> dogMap = new HashMap<>();
			dogMap.put("number", "");
			dogMap.put("userName", "");

//			ro.getResultKeyMap().put("catTest", catMap);
//			ro.getResultKeyMap().put("dogTest", dogMap);
		}


		String[] resultKeys = {
				"catTest.id",
//				"dotTest.number",
//				"dotTest.userName"
		};

		ro.setResultKeys(resultKeys);

		List<Object> inList = new ArrayList<>();
		inList.add("xxxxx");
		inList.add("gggg");

		CriteriaBuilder.ResultMappedBuilder builder = CriteriaBuilder.buildResultMapped(CatTest.class,ro);
//		builder.distinct("catTest.id").reduce(Criteria.ReduceType.COUNT,"catTest.id").groupBy("catTest.id");
		builder.and().in("catTest.catFriendName", inList);


//		builder.or().beginSub().eq("dogTest.userName","yyy")
//				.or().like("dogTest.userName",null)
//				.or().likeRight("dogTest.userName", "xxx")
//				.endSub();
//		builder.or().beginSub().eq("dogTest.userName", "uuu").endSub();
		

		String sourceScript = "catTest LEFT JOIN dogTest on catTest.dogId = dogTest.id";

		Criteria.ResultMapped resultMapped = builder.get();
		resultMapped.setSourceScript(sourceScript);

		Pagination<Map<String,Object>> pagination = repository.find(resultMapped);

		Cat cat = this.catRepository.get(110);

		System.out.println("____cat: " + cat);

		List<Long> idList = new ArrayList<>();
		idList.add(109L);
		idList.add(110L);
		InCondition inCondition = new InCondition("id",idList);
		List<Cat> catList = this.catRepository.in(inCondition);

		System.out.println("____catList: " + catList);

		return ViewEntity.ok(pagination);

	}



}
