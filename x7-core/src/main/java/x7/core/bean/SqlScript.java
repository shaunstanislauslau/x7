package x7.core.bean;

public interface SqlScript {

	String SELECT = "SELECT";
	String DISTINCT = " DISTINCT ";
	String WHERE = " WHERE ";
	String FROM = "FROM";
	String LIMIT = " LIMIT ";
	String SET = " SET ";
	String UPDATE = "UPDATE";
	String IN = " IN ";

	String NONE = "";
	String SPACE = " ";
	String PLACE_HOLDER = "?";
	String EQ_PLACE_HOLDER = " = ?";
	String LIKE_HOLDER = "%";
	String POINT = ".";
	String COMMA = " ,";
	String STAR = "*";
	String UNDER_LINE = "_";
	String LEFT_PARENTTHESIS = "(";
	String RIGHT_PARENTTHESIS = ")";
	String WELL_NO = "#";
	String SINGLE_QUOTES = "'";


	String sql();
}
