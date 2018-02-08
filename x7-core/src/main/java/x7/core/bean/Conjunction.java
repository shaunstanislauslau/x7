package x7.core.bean;

public enum Conjunction implements SqlScript{

	AND(" and "),
	OR(" or "),
	ORDER_BY(" order by "),
	GROUP_BY(" group by "),
	HAVING(" having "),
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
