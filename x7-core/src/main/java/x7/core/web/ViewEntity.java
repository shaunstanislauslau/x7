package x7.core.web;

/**
 * 
 * Simple Rest Response PROTOCAL
 * 
 * @author Sim
 *
 */
public class ViewEntity<T> {

	public final static String OK = "OK";
	public final static String FAIL = "FAIL";

	private String status;
	private T body;

	public String getStatus() {
		return status;
	}

	public Object getBody() {
		return body;
	}

	public static ViewEntity<String> toast(String str) {
		ViewEntity<String> message = new ViewEntity<String>();
		message.status = FAIL;
		message.body = str;
		return message;
	}

	public static <T> ViewEntity<T> ok(T obj) {

		ViewEntity<T> message = new ViewEntity<T>();
		message.status = OK;
		message.body = obj;

		return message;

	}

	public static ViewEntity ok() {

		return ok(null);

	}

	@Override
	public String toString() {
		return "ViewEntity [status=" + status + ", body=" + body + "]";
	}

}
