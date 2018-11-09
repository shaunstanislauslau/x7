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

import com.fasterxml.jackson.annotation.JsonIgnore;
import x7.core.util.BeanUtil;
import x7.core.web.Direction;
import x7.core.web.Fetched;
import x7.core.web.Paged;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * @author sim
 *
 */
public class Criteria implements Paged, Serializable {

	private static final long serialVersionUID = 7088698915888081349L;

	private Class<?> clz;
	@JsonIgnore
	private transient Parsed parsed;
	private boolean isScroll;
	private int page;
	private int rows;
	private String orderBy;
	private Direction direction = Direction.DESC;

	private List<Object> valueList = new ArrayList<Object>();
	
	private List<X> listX = new ArrayList<X>();

	private transient DataPermission dataPermission;//String,Or List<String>   LikeRight | In

	protected transient boolean isWhere = true;

	public Criteria(){}

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
		sb.append(SqlScript.SPACE).append(SqlScript.FROM).append(SqlScript.SPACE).append(BeanUtil.getByFirstLower(getClz().getSimpleName()));
		return false;
	}

	private transient String countDistinct = "COUNT(*) count";
	protected void setCountDistinct(String str){
		this.countDistinct = str;
	}
	protected String getCountDistinct(){
		return this.countDistinct;
	}

	private transient String customedResultKey = SqlScript.STAR;
	protected void setCustomedResultKey(String str){
		this.customedResultKey = str;
	}

	protected String resultAllScript() {
		return customedResultKey;
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

	public DataPermission getDataPermission() {
		return dataPermission;
	}

	public void setDataPermission(DataPermission dataPermission) {
		this.dataPermission = dataPermission;
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
		return "Criteria{" +
				"isScroll=" + isScroll +
				", page=" + page +
				", rows=" + rows +
				", orderBy='" + orderBy + '\'' +
				", direction=" + direction +
				", valueList=" + valueList +
				", listX=" + listX +
				", dataPermission=" + dataPermission +
				", isWhere=" + isWhere +
				", countDistinct='" + countDistinct + '\'' +
				", customedResultKey='" + customedResultKey + '\'' +
				", clz=" + clz +
				'}';
	}

	public class ResultMapped extends Criteria {

		private List<String> resultList = new ArrayList<String>();
		private String sourceScript;
		private Distinct distinct;
		private String groupBy;
		private List<Reduce> reduceList = new ArrayList<>();
		private MapMapper mapMapper;

		public Distinct getDistinct() {
			return distinct;
		}

		public List<Reduce> getReduceList() {
			return reduceList;
		}

		public String getGroupBy() {
			return groupBy;
		}

		public void setGroupBy(String groupBy) {
			this.groupBy = groupBy;
		}

		public void setReduceList(List<Reduce> reduceList) {
			this.reduceList = reduceList;
		}

		public void setDistinct(Distinct distinct) {
			this.distinct = distinct;
		}

		public MapMapper getMapMapper() {
			return this.mapMapper;
		}

		public void setMapMapper(MapMapper mapMapper) {
			this.mapMapper = mapMapper;
		}

		public String getResultScript() {
			if (resultList.isEmpty()){
				return SqlScript.STAR;
			}else{
				StringBuilder sb = new StringBuilder();
				int i = 0;
				int size = resultList.size() - 1;
				for (String str : resultList){
					String mapper = getMapMapper().mapper(str);
					sb.append(mapper);
					if (i < size){
						sb.append(SqlScript.COMMA);
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
				sb.append(SqlScript.SPACE).append(SqlScript.FROM).append(SqlScript.SPACE).append(BeanUtil.getByFirstLower(getClz().getSimpleName()));
				return false;
			} else {
				String temp = sourceScript.trim();
				if (temp.startsWith(SqlScript.FROM) || temp.startsWith(SqlScript.FROM.toLowerCase())) {
					sb.append(sourceScript);
				} else {
					sb.append(SqlScript.SPACE).append(SqlScript.FROM).append(SqlScript.SPACE).append(sourceScript);
				}
				return true;
			}
		}


		@Override
		protected String resultAllScript() {
			if (Objects.nonNull(super.customedResultKey)&&!super.customedResultKey.equals(SqlScript.STAR)){
				return super.customedResultKey;
			}else {
				int size = 0;
				String column = "";
				if (resultList.isEmpty()) {
					column += (SqlScript.SPACE + SqlScript.STAR + SqlScript.SPACE);
				} else {
					size = resultList.size();
					for (int i = 0; i < size; i++) {
						column = column + SqlScript.SPACE + resultList.get(i);
						if (i < size - 1) {
							column += SqlScript.COMMA;
						}
					}
				}
				return column;
			}

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
			return "ResultMapped{" +
					"resultList=" + resultList +
					", sourceScript='" + sourceScript + '\'' +
					", criteria='" + super.toString() + '\'' +
					'}';
		}

	}
	
	public static class X {
		private static final long serialVersionUID = 7088698915888083256L;
		private Conjunction conjunction;
		private Predicate predicate;
		private String key;
		private Object value;
		private List<X> subList;
		private X parent;
		private transient String script;
		public X(){}
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
		public List<X> getSubList() {
			return subList;
		}
		public void setSubList(List<X> subList) {
			this.subList = subList;
		}
		public X getParent() {
			return parent;
		}
		public void setParent(X parent) {
			this.parent = parent;
		}
		public String getScript() {
			return script;
		}
		public void setScript(String script) {
			this.script = script;
		}

		@Override
		public String toString() {
			return "X{" +
					"conjunction=" + conjunction +
					", predicate=" + predicate +
					", key=" + key +
					", value=" + value +
					", subList=" + subList +
					", script=" + script +
					'}';
		}
	}

	public enum ReduceType {
		SUM,
		COUNT,
		MAX,
		MIN,
		AVG
	}

	public static class MapMapper {
		private Map<String, String> propertyMapperMap = new HashMap<String, String>();
		private Map<String, String> mapperPropertyMap = new HashMap<String, String>();

		public Map<String, String> getPropertyMapperMap() {
			return propertyMapperMap;
		}

		public Map<String, String> getMapperPropertyMap() {
			return mapperPropertyMap;
		}

		public void put(String property, String mapper) {
			this.propertyMapperMap.put(property, mapper);
			this.mapperPropertyMap.put(mapper, property);
		}

		public String mapper(String property) {
			return this.propertyMapperMap.get(property);
		}

		public String property(String mapper) {
			return this.mapperPropertyMap.get(mapper);
		}

		@Override
		public String toString() {
			return "MapMapper [propertyMapperMap=" + propertyMapperMap + ", mapperPropertyMap=" + mapperPropertyMap
					+ "]";
		}
	}

}