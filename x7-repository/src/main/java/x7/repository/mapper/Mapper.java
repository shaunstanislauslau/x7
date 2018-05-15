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
package x7.repository.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import x7.core.bean.BeanElement;
import x7.core.config.Configs;
import x7.repository.ConfigKey;
import x7.repository.DbType;

public interface Mapper {

	String CREATE = "CREATE";
	String REFRESH = "REFRESH";
	String REMOVE = "REMOVE";
	String QUERY = "QUERY";
	String LOAD = "LOAD";
	String MAX_ID = "MAX_ID";
	String COUNT = "COUNT";
	String TAG = "TAG";
	String CREATE_TABLE = "CREATE_TABLE";
	String INDEX = "INDEX";
	

	interface Interpreter {
		
		String getTableSql(Class clz);

		String getRefreshSql(Class clz);

		String getQuerySql(Class clz);

		String getLoadSql(Class clz);

		String getMaxIdSql(Class clz);

		String getCreateSql(Class clz);

		String getTagSql(Class clz);

		String getCount(Class clz);
	}
	
	public static String getSqlTypeRegX(BeanElement be){
	
			Class clz = be.clz;
			if (clz == Date.class || clz == java.sql.Date.class || clz == java.sql.Timestamp.class) {
				return Dialect.DATE;
			} else if (clz == String.class) {
				return Dialect.STRING;
			} else if (clz.isEnum()) {
				return Dialect.STRING;
			} else if (clz == int.class || clz == Integer.class) {
				return Dialect.INT;
			} else if (clz == long.class || clz == Long.class) {
				return Dialect.LONG;
			} else if (clz == double.class || clz == Double.class) {
				return Dialect.BIG;
			} else if (clz == float.class || clz == Float.class) {
				return Dialect.BIG;
			} else if (clz == BigDecimal.class) {
				return Dialect.BIG;
			} else if (clz == boolean.class || clz == Boolean.class) {
				return Dialect.BYTE;
			} else if (clz == short.class || clz == Short.class) {
				return Dialect.INT;
			} else if (clz == byte.class || clz == Byte.class) {
				return Dialect.BYTE;
			} 
			return Dialect.TEXT;
		
	}

	interface Dialect {
		
		String MYSQL = "mysql";
		String ORACLE = "oracle";
		
		String DATE = " ${DATE}";
		String BYTE = " ${BYTE}";
		String INT = " ${INT}";
		String LONG = " ${LONG}";
		String BIG = " ${BIG}";
		String STRING = " ${STRING}";
		String TEXT = " ${TEXT}";
		String LONG_TEXT = " ${LONG_TEXT}";
		String INCREAMENT = " ${INCREAMENT}";
		String ENGINE = " ${ENGINE}";
		
		interface Pagination {
			String ORACLE_PAGINATION = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM ( ${SQL} ) A   WHERE ROWNUM <= ${END}  )  WHERE RN > ${BEGIN} ";
			String ORACLE_PAGINATION_REGX_SQL = "${SQL}";
			String ORACLE_PAGINATION_REGX_BEGIN = "${BEGIN}";
			String ORACLE_PAGINATION_REGX_END = "${END}";
			
			public static String match(String sql,long start, long rows) {
				String dbType = DbType.value;
				if (dbType.toLowerCase().equals("oracle")){
					return ORACLE_PAGINATION
							.replace(ORACLE_PAGINATION_REGX_END, String.valueOf(start + rows))
							.replace(ORACLE_PAGINATION_REGX_BEGIN, String.valueOf(start))
							.replace(ORACLE_PAGINATION_REGX_SQL, sql);
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append(sql).append(" LIMIT ").append(start).append(",").append(rows);
				return sb.toString(); 
			}
		}
		
		Map<String, Map<String, String>> oracleMap = new HashMap<String, Map<String, String>>() {
			{
				put(CREATE_TABLE, new HashMap<String, String>() {
					{
						put(DATE,           "date");
						put(BYTE, 			"number(3, 0)");
						put(INT, 		 	"number(10, 0)");
						put(LONG,	 		"number(18, 0)");
						put(BIG,			"number(19, 2)");
						put(STRING,			"varchar2");
						put(TEXT,			"clob");
						put(LONG_TEXT,		"clob");
						put(INCREAMENT,       "");
						put(ENGINE,       "");
					}
				}

				);
			}
		};
		
		Map<String, Map<String, String>> mysqlMap = new HashMap<String, Map<String, String>>() {
			{
				put(CREATE_TABLE, new HashMap<String, String>() {
					{
						put(DATE,       "timestamp");
						put(BYTE,		"tinyint(1)");
						put(INT,		"int(11)");
						put(LONG,		"bigint(13)");
						put(BIG,		"decimal(15,2)");
						put(STRING,		"varchar");
						put(TEXT,		"text");
						put(LONG_TEXT,	"longtext");
						put(INCREAMENT, "AUTO_INCREMENT");
						put(ENGINE,     "ENGINE=InnoDB DEFAULT CHARSET=utf8");
					}
				}

				);
			}
		};

		Map<String,Map<String, Map<String, String>>> map = new HashMap<String,Map<String, Map<String, String>>>(){
			{
				put (MYSQL, mysqlMap);
				put (ORACLE, oracleMap);
			}
		};
		
		public static String match(String sql, String dbType, String sqlType) {
			String dateV = map.get(dbType).get(sqlType).get(DATE);
			String byteV = map.get(dbType).get(sqlType).get(BYTE);
			String intV = map.get(dbType).get(sqlType).get(INT);
			String longV = map.get(dbType).get(sqlType).get(LONG);
			String bigV = map.get(dbType).get(sqlType).get(BIG);
			String textV = map.get(dbType).get(sqlType).get(TEXT);
			String longTextV = map.get(dbType).get(sqlType).get(LONG_TEXT);
			String stringV = map.get(dbType).get(sqlType).get(STRING);
			String increamentV = map.get(dbType).get(sqlType).get(INCREAMENT);
			String engineV = map.get(dbType).get(sqlType).get(ENGINE);
			
			return sql.replace(DATE.trim(), dateV)
					.replace(BYTE.trim(), byteV).replace(INT.trim(), intV).replace(LONG.trim(), longV)
					.replace(BIG.trim(), bigV).replace(TEXT.trim(), textV).replace(LONG_TEXT.trim(), longTextV)
					.replace(STRING.trim(), stringV)
					.replace(INCREAMENT.trim(), increamentV)
					.replace(ENGINE.trim(), engineV);
		}
	}
}
