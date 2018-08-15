package x7.core.bean;

public enum Predicate implements SqlScript{

	EQ(" = "),
	LT(" < "),
	GT(" > "),
	LTE(" <= "),
	GTE(" >= "),
	NE(" <> "),
	LIKE(" LIKE "),
	IN(" IN "),
	NOT_IN(" NOT IN "),
	IS_NOT_NULL(" IS NOT NULL "),
	IS_NULL(" IS NULL "),
	BETWEEN(" BETWEEN "),
	SUB_BEGIN("( "),
	SUB_END(" )"),
	SUB(" SUB ");

	private String sqlOper;
	private Predicate(String sqlOper){
		this.sqlOper = sqlOper;
	}
	@Override
	public String sql(){
		return this.sqlOper;
	}
}
