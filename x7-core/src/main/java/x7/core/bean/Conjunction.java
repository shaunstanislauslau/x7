package x7.core.bean;

public enum Conjunction implements SqlScript{

	AND(" AND "),
	OR(" OR "),
	ORDER_BY(" ORDER BY "),
	GROUP_BY(" GROUP BY "),
	HAVING(" HAVING "),
	WHERE(" WHERE "),
	;
	
	private String sqlOper;
	private Conjunction(String sqlOper){
		this.sqlOper = sqlOper;
	}
	@Override
	public String sql() {
		return this.sqlOper;
	}
	
}
