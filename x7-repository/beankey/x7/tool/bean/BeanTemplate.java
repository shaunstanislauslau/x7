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
package x7.tool.bean;

import java.util.ArrayList;
import java.util.List;

public class BeanTemplate {

	private String packageName;
	private String clzName;
	private List<String> propList = new ArrayList<String>();
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClzName() {
		return clzName;
	}
	public void setClzName(String clzName) {
		this.clzName = clzName;
	}
	public List<String> getPropList() {
		return propList;
	}
	public void setPropList(List<String> propList) {
		this.propList = propList;
	}
	@Override
	public String toString() {
		return "BeanTemplate [packageName=" + packageName + ", clzName=" + clzName + ", propList=" + propList + "]";
	}
	
}
