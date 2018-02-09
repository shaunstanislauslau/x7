package x7.core.bean;

public enum Predicate implements SqlScript{

	EQ(" = "),
	LT(" < "),
	GT(" > "),
	LTE(" <= "),
	GTE(" >= "),
	NE(" <> "),
	LIKE(" like "),
	IN(" in "),
	NOT_IN(" not in "),
	IS_NOT_NULL(" is not null"),
	IS_NULL(" is null"),
	BETWEEN(" between "),
	SUB_BEGIN("( "),
	SUB_END(" )");

	private String sqlOper;
	private Predicate(String sqlOper){
		this.sqlOper = sqlOper;
	}
	@Override
	public String sql(){
		return this.sqlOper;
	}
}
