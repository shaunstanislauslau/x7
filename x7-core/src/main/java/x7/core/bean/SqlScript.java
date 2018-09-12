package x7.core.bean;

public interface SqlScript {

	String SELECT = "SELECT";
	String DISTINCT = " DISTINCT ";
	String SPACE = " ";
	String PLACE_HOLDER = "?";
	String LIKE_HOLDER = "%";

	String sql();
}
