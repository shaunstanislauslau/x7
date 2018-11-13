package x7.core.bean;

public interface SqlScript {

	String SELECT = "SELECT";
	String DISTINCT = " DISTINCT ";
	String WHERE = " WHERE ";
	String SPACE = " ";
	String PLACE_HOLDER = "?";
	String EQ_PLACE_HOLDER = " = ?";
	String LIKE_HOLDER = "%";
	String POINT = ".";
	String COMMA = " ,";
	String STAR = "*";
	String FROM = "FROM";
	String LIMIT = " LIMIT ";
	String SET = " SET ";
	String UPDATE = "UPDATE";

	String sql();
}
