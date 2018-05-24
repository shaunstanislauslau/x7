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
package x7.core.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import x7.core.bean.CriteriaBuilder.FetchMapper;
import x7.core.util.BeanUtil;
import x7.core.web.Direction;
import x7.core.web.Paged;

/**
 * 简单的SQL拼接标准化
 * 
 * @author sim
 *
 */
public class Criteria implements Paged, Serializable {

	private static final long serialVersionUID = 7088698915888081349L;

	private Class<?> clz;
	private transient Parsed parsed;
	private boolean isScroll;
	private int page;
	private int rows;
	private String orderBy;
	private Direction direction = Direction.DESC;

	private List<Object> valueList = new ArrayList<Object>();
	
	private List<X> listX = new ArrayList<X>();
	
	private String dataPermissionSn;

	private FetchMapper fetchMapper;

	public List<Object> getValueList() {
		return valueList;
	}

	public void setValueList(List<Object> valueList) {
		this.valueList = valueList;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction sc) {
		this.direction = sc;
	}


	public Class<?> getClz() {
		return clz;
	}

	public void setClz(Class<?> clz) {
		this.clz = clz;
	}

	public Parsed getParsed() {
		return parsed;
	}

	public void setParsed(Parsed parsed) {
		this.parsed = parsed;
	}

	protected boolean sourceScript(StringBuilder sb) {
		sb.append(" ").append(" FROM ").append(BeanUtil.getByFirstLower(getClz().getSimpleName()));
		return false;
	}


	protected String resultAllScript() {
		return "*";
	}

	public FetchMapper getFetchMapper() {
		return fetchMapper;
	}

	public void setFetchMapper(FetchMapper fetchMapper) {
		this.fetchMapper = fetchMapper;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	public boolean isScroll() {
		return isScroll;
	}

	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public List<X> getListX() {
		return this.listX;
	}
	
	protected void add(X x) {
		this.listX.add(x);
	}

	protected String getDataPermissionSn() {
		return dataPermissionSn;
	}

	protected void setDataPermissionSn(String dataPermissionSn) {
		this.dataPermissionSn = dataPermissionSn;
	}

	public void paged(Paged paged) {
		this.orderBy = paged.getOrderBy();
		this.direction = paged.getDirection();
		this.isScroll = paged.isScroll();
		this.page = paged.getPage();
		this.rows = paged.getRows();
	}

	@Override
	public String toString() {
		return "Criteria [clz=" + clz + ", isScroll=" + isScroll 
				+ ", page=" + page + ", rows=" + rows + ", orderBy="
				+ orderBy + ", direction=" + direction + ", valueList="
				+ valueList + ", listX=" + listX + ", fetchMapper=" + fetchMapper + "]";
	}


	/**
	 * 可以连表的SQL拼接标准化, 不支持缓存<br>
	 * 业务系统尽量避免使用连表查询<br>
	 * 互联网业务系统中后期开发必须避免<br>
	 * 适合简单的报表和记录查询<br>
	 * <br>
	 * <br>
	 * xAddKey(String x)<br>
	 * <hr>
	 * <br>
	 * <li>Sample:</li><br>
	 * CriteriaFetchable builder = new
	 * CriteriaFetchable(Cat.class,null);<br>
	 * builder.xAddKey("t->id");<br>
	 * builder.xAddKey("t->name.as.catName");<br>
	 * builder.xAddKey("dog->age.as.dogAge");<br>
	 * builder.orderBy("t->id");<br>
	 * <br>
	 * List<Map<String,Object>> list =
	 * Repositories.getInstance().list(criteria);<br>
	 * <br>
	 * 
	 * @author Sim
	 */
	public class Fetch extends Criteria {

		private List<String> resultList = new ArrayList<String>();
		private String sourceScript;

		public String getResultScript() {
			if (resultList.isEmpty()){
				return "*";
			}else{
				StringBuilder sb = new StringBuilder();
				int i = 0;
				int size = resultList.size() - 1;
				for (String str : resultList){
					String mapper = getFetchMapper().mapper(str);
					sb.append(mapper);
					if (i < size){
						sb.append(",");
					}
					i++;
				}
				return sb.toString();
			}
		}

		public void setSourceScript(String sourceScript) {
			this.sourceScript = sourceScript;
		}
		

		public List<String> getResultList() {
			return resultList;
		}

		public void setResultList(List<String> columnList) {
			this.resultList = columnList;
		}


		@Override
		protected boolean sourceScript(StringBuilder sb) {
			if (sourceScript == null) {
				sb.append(" ").append(" FROM ").append(BeanUtil.getByFirstLower(getClz().getSimpleName()));
				return false;
			} else {
				String temp = sourceScript.trim();
				if (temp.startsWith("FROM") || temp.startsWith("from")) {
					sb.append(sourceScript);
				} else {
					sb.append(" ").append(" FROM ").append(sourceScript);
				}
				return true;
			}
		}


		@Override
		protected String resultAllScript() {
			int size = 0;
			String column = "";
			if (resultList.isEmpty()) {
				column += " * ";
			} else {
				size = resultList.size();
				for (int i = 0; i < size; i++) {
					column = column + " " + resultList.get(i);
					if (i < size - 1) {
						column += ",";
					}
				}
			}
			return column;
		}

		public List<String> listAllResultKey() {
			List<String> list = new ArrayList<String>();
			Parsed parsed = Parser.get(getClz());

			for (BeanElement be : parsed.getBeanElementList()) {
				list.add(be.getMapper());
			}
			return list;
		}

		@Override
		public String toString() {
			return "Fetch{" +
					"resultList=" + resultList +
					", sourceScript='" + sourceScript + '\'' +
					", criteria='" + super.toString() + '\'' +
					'}';
		}

	}
	
	public static class X {
		private Conjunction conjunction;
		private Predicate predicate;
		private String key;
		private Object value;
		public Conjunction getConjunction() {
			return conjunction;
		}
		public void setConjunction(Conjunction conjunction) {
			this.conjunction = conjunction;
		}
		public Predicate getPredicate() {
			return predicate;
		}
		public void setPredicate(Predicate predicate) {
			this.predicate = predicate;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return "X [conjunction=" + conjunction + ", predicate=" + predicate + ", key=" + key + ", value=" + value
					+ "]";
		}
	}

}