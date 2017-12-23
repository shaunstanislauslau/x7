package x7.core.web;

/**
 * 
 * Simple Rest Response PROTOCAL
 * 
 * @author Sim
 *
 */
public class RestView {

	public final static String OK = "OK";
	public final static String FAIL = "FAIL";

	private String status;
	private Object result;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static RestView toast(String str) {
		RestView message = new RestView();
		message.setStatus(FAIL);
		message.setResult(str);
		return message;
	}

	public static RestView ok(Object obj) {

		RestView message = new RestView();
		message.setStatus(OK);
		message.setResult(obj);

		return message;

	}

	public static RestView ok() {

		return ok(null);

	}

	@Override
	public String toString() {
		return "RestView [status=" + status + ", result=" + result + "]";
	}

}
