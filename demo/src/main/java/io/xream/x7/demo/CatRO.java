package io.xream.x7.demo;

import x7.core.web.ResultMapping;
import x7.core.web.TokenedAndPagedRo;

import java.util.HashMap;
import java.util.Map;

public class CatRO extends TokenedAndPagedRo implements ResultMapping {

	private String catFriendName;
	
	private Map<String, Object> resultKeyMap = new HashMap<String,Object>();
	
	
	public String getCatFriendName() {
		return catFriendName;
	}
	public void setCatFriendName(String catFriendName) {
		this.catFriendName = catFriendName;
	}
	public void setResultKeyMap(Map<String, Object> resultKeyMap) {
		this.resultKeyMap = resultKeyMap;
	}

	private String[] resultKeys;
	@Override
	public String[] getResultKeys() {
		return resultKeys;
	}

	public void setResultKeys(String[] arr){
		this.resultKeys = arr;
	}
}
