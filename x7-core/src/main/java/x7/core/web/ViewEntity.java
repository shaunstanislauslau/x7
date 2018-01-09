package x7.core.web;

/**
 * 
 * Simple Rest Response PROTOCAL
 * 
 * @author Sim
 *
 */
public class ViewEntity {

	public final static String OK = "OK";
	public final static String FAIL = "FAIL";

	private String status;
	private Object body;

	public String getStatus() {
		return status;
	}

	public Object getBody() {
		return body;
	}

	public static ViewEntity toast(String str) {
		ViewEntity message = new ViewEntity();
		message.status = FAIL;
		message.body = str;
		return message;
	}

	public static ViewEntity ok(Object obj) {

		ViewEntity message = new ViewEntity();
		message.status = OK;
		message.body = obj;

		return message;

	}

	public static ViewEntity ok() {
		return ok(null);
	}

	@Override
	public String toString() {
		return "RestView [status=" + status + ", body=" + body + "]";
	}

}
