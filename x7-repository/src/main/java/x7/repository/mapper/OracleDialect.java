package x7.repository.mapper;

import x7.core.bean.BeanElement;
import x7.core.util.JsonX;
import x7.core.util.StringUtil;
import x7.repository.exception.SqlTypeException;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OracleDialect implements Mapper.Dialect {

	private Map<String, String> map = new HashMap<String, String>() {
		{
			put(DATE, "date");
			put(BYTE, "number(3, 0)");
			put(INT, "number(10, 0)");
			put(LONG, "number(18, 0)");
			put(BIG, "number(19, 2)");
			put(STRING, "varchar2");
			put(TEXT, "clob");
			put(LONG_TEXT, "clob");
			put(INCREAMENT, "");
			put(ENGINE, "");
		}

	};

	private final static String ORACLE_PAGINATION = "SELECT * FROM (SELECT A.*, ROWNUM RN FROM ( ${SQL} ) A   WHERE ROWNUM <= ${END}  )  WHERE RN > ${BEGIN} ";
	private final static String ORACLE_PAGINATION_REGX_SQL = "${SQL}";
	private final static String ORACLE_PAGINATION_REGX_BEGIN = "${BEGIN}";
	private final static String ORACLE_PAGINATION_REGX_END = "${END}";

	public String match(String sql, long start, long rows) {

		return ORACLE_PAGINATION.replace(ORACLE_PAGINATION_REGX_END, String.valueOf(start + rows))
				.replace(ORACLE_PAGINATION_REGX_BEGIN, String.valueOf(start)).replace(ORACLE_PAGINATION_REGX_SQL, sql);

	}

	public String match(String sql, String sqlType) {
		String dateV = map.get(DATE);
		String byteV = map.get(BYTE);
		String intV = map.get(INT);
		String longV = map.get(LONG);
		String bigV = map.get(BIG);
		String textV = map.get(TEXT);
		String longTextV = map.get(LONG_TEXT);
		String stringV = map.get(STRING);
		String increamentV = map.get(INCREAMENT);
		String engineV = map.get(ENGINE);

		return sql.replace(DATE.trim(), dateV).replace(BYTE.trim(), byteV).replace(INT.trim(), intV)
				.replace(LONG.trim(), longV).replace(BIG.trim(), bigV).replace(TEXT.trim(), textV)
				.replace(LONG_TEXT.trim(), longTextV).replace(STRING.trim(), stringV)
				.replace(INCREAMENT.trim(), increamentV).replace(ENGINE.trim(), engineV);
	}

	@Override
	public <T> void initObj(T obj, ResultSet rs, BeanElement tempEle, List<BeanElement> eles) {
		
		Object value = null;
		try {
			value = null;
			for (BeanElement ele : eles) {
				tempEle = ele;
				Method method = ele.setMethod;
				String mapper = ele.getMapper();

				if (mapper.contains("`")){
					mapper = mapper.replace("`","");
				}

				if ( ele.clz == Boolean.class) {
					int ib = rs.getInt(mapper);
					value = ib;
					method.invoke(obj, ib == 1 ? true : false);
				}else if (ele.clz.isEnum()) {
					value = rs.getObject(mapper);
					if (Objects.isNull(value))
						continue;
					String str = value.toString();

					Method m = ele.clz.getDeclaredMethod("valueOf", String.class);
					Object e = m.invoke(null, str);
					value = e;
					method.invoke(obj, e);

				} else if (ele.isJson) {
					String str = rs.getString(mapper);
					if (StringUtil.isNullOrEmpty(str))
						continue;
					value = str;
					if (ele.clz == Map.class) {
						method.invoke(obj, JsonX.toMap(str));
					} else if (ele.clz == List.class) {
						method.invoke(obj, JsonX.toList(str, ele.geneType));
					} else {
						method.invoke(obj, JsonX.toObject(str, ele.clz));
					}
				} else if (ele.clz == BigDecimal.class) {

					value = rs.getObject(mapper);
					if (Objects.isNull(value))
						continue;
					method.invoke(obj, new BigDecimal((String.valueOf(value))));
				} else {
					value = rs.getObject(mapper);
					if (Objects.isNull(value))
						continue;
					if (value instanceof BigDecimal){// oracle supported
						if (ele.clz == long.class || ele.clz == Long.class){
							method.invoke(obj, ((BigDecimal) value).longValue());
						}else if (ele.clz == Integer.class || ele.clz == int.class){
							method.invoke(obj, ((BigDecimal) value).intValue());
						}else if (ele.clz == Boolean.class || ele.clz == boolean.class){
							method.invoke(obj, ((BigDecimal) value).intValue() == 1 ? true : false);
						}else if (ele.clz == Double.class || ele.clz == double.class){
							method.invoke(obj, ((BigDecimal) value).doubleValue());
						}else if (ele.clz == Float.class || ele.clz == float.class){
							method.invoke(obj, ((BigDecimal) value).floatValue());
						}else if (ele.clz == Short.class || ele.clz == short.class){
							method.invoke(obj, ((BigDecimal) value).shortValue());
						}else if (ele.clz == Byte.class || ele.clz == byte.class){
							method.invoke(obj, ((BigDecimal) value).byteValue());
						}
					}else {
						if (ele.clz == double.class || ele.clz == Double.class) {
							method.invoke(obj, Double.valueOf(String.valueOf(value)));
						}else {
							method.invoke(obj, value);
						}
					}
				}
			}
		}catch (Exception e){
			throw new SqlTypeException("clz:" + obj.getClass() + ", property: " + tempEle.getProperty() + ", type:" + tempEle.geneType + ", value; " + value );
		}
		
	}

}
