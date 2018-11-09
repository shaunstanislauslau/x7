package x7.core.bean;

public interface SqlScript {

	String SELECT = "SELECT";
	String DISTINCT = " DISTINCT ";
	String WHERE = " WHERE ";
	String SPACE = " ";
	String PLACE_HOLDER = "?";
	String LIKE_HOLDER = "%";
	String POINT = ".";
	String COMMA = " ,";
	String STAR = "*";
	String FROM = "FROM";
	String LIMIT = " LIMIT ";

	String sql();
}
