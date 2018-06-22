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

public class MySqlDialect implements Mapper.Dialect {

	private Map<String, String> map = new HashMap<String, String>() {
		{

			put(DATE, "timestamp");
			put(BYTE, "tinyint(1)");
			put(INT, "int(11)");
			put(LONG, "bigint(13)");
			put(BIG, "decimal(15,2)");
			put(STRING, "varchar");
			put(TEXT, "text");
			put(LONG_TEXT, "longtext");
			put(INCREAMENT, "AUTO_INCREMENT");
			put(ENGINE, "ENGINE=InnoDB DEFAULT CHARSET=utf8");

		}

	};

	public String match(String sql, long start, long rows) {

		StringBuilder sb = new StringBuilder();
		sb.append(sql).append(" LIMIT ").append(start).append(",").append(rows);
		return sb.toString();

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

				if (ele.clz.isEnum()) {
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

					if (ele.clz == double.class || ele.clz == Double.class) {
						method.invoke(obj, Double.valueOf(String.valueOf(value)));
					} else {
						method.invoke(obj, value);
					}

				}
			}
		} catch (Exception e) {
			throw new SqlTypeException("clz:" + obj.getClass() + ", property: " + tempEle.getProperty() + ", type:"
					+ tempEle.geneType + ", value; " + value);
		}

	}
}
