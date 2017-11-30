package x7.repository.mapper;

import java.util.HashMap;
import java.util.Map;

import x7.core.config.Configs;
import x7.repository.ConfigKey;

public interface Mapper {

	String CREATE = "CREATE";
	String REFRESH = "REFRESH";
	String REMOVE = "REMOVE";
	String QUERY = "QUERY";
	String QUERY_TWO = "QUERY_TWO";
	String LOAD = "LOAD";
	String MAX_ID = "MAX_ID";
	String COUNT = "COUNT";
	String PAGINATION = "PAGINATION";
	String CREATE_TABLE = "CREATE_TABLE";
	String INDEX = "INDEX";

	interface Interpreter {
		String getTableSql(Class clz);

		String getRefreshSql(Class clz);

		String getQuerySql(Class clz);

		String getLoadSql(Class clz);

		String getMaxIdSql(Class clz);

		String getCreateSql(Class clz);

		String getPaginationSql(Class clz);

		String getCount(Class clz);
	}

	interface Dialect {
		
		String MYSQL = "mysql";
		String ORACLE = "oracle";
		
		String BYTE = "BYTE";
		String INT = "INT";
		String LONG = "LONG";
		String BIG = "BIG";
		String STRING = "STRING";
		String TEXT = "TEXT";
		String LONG_TEXT = "LONG_TEXT";
		
		
		interface Pagination {
			String ORACLE_PAGINATION = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM ( ${SQL} ) A   WHERE ROWNUM <= ${END}  )  WHERE RN > ${BEGIN} ";
			String ORACLE_PAGINATION_REGX_SQL = "${SQL}";
			String ORACLE_PAGINATION_REGX_BEGIN = "${BEGIN}";
			String ORACLE_PAGINATION_REGX_END = "${END}";
			
			public static String match(String sql,long start, long rows) {
				String repository = Configs.getString(ConfigKey.REPOSITORY);
				if (repository.toLowerCase().equals("oracle")){
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
						put(BYTE, 			"number(3, 0)");
						put(INT, 		 	"number(10, 0)");
						put(LONG,	 		"number(18, 0)");
						put(BIG,			"number(19, 2)");
						put(STRING,			"varchar2");
						put(TEXT,			"clob");
						put(LONG_TEXT,		"clob");
					}
				}

				);
			}
		};
		
		Map<String, Map<String, String>> mysqlMap = new HashMap<String, Map<String, String>>() {
			{
				put(CREATE_TABLE, new HashMap<String, String>() {
					{
						put(BYTE,		"tinyint(1)");
						put(INT,		"int(11)");
						put(LONG,		"bigint(13)");
						put(BIG,		"decimal((15,2)");
						put(STRING,		"varchar");
						put(TEXT,		"text");
						put(LONG_TEXT,	"longtext");
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
		
		default String get(String dbType, String sqlType, String lang) {
			return map.get(dbType).get(sqlType).get(lang);
		}
	}
}
