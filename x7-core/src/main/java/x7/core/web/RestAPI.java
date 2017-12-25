package x7.core.web;

import x7.core.util.JsonX;

/**
 * 
 * Simple third part API PROTOCAL
 * 
 * @author Sim
 *
 */
public interface RestAPI {
	
	String OK = "OK";
	String FAIL = "FAIL";
	String TOAST_SIGN_EXCEPTION = "SIGN UNAVAILABLE";
	
	interface Signed {
		String sign ();
		long getTime();
		String getId();
	}
	
	public static class SignException extends RuntimeException{
		
		private String message;
		public SignException(String message){
			this.message = message;
		}
		public SignException(){
		}
		public String getMessage() {
			return message;
		}
	}
	
	public class RequestMessage<T> {
		
		private T body;
		private String id;
		private long time;
		private String sign;
		
		public boolean isSigned(Signed signed) {
			return sign.equals(signed.sign());
		}
		
		public T getBody() {
			return body;
		}
		public void setBody(T body) {
			this.body = body;
		}
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getTime() {
			return time;
		}
		public void setTime(long time) {
			this.time = time;
		}
		public String getSign() {
			return sign;
		}
		public void setSign(String sign) {
			this.sign = sign;
		}
		@Override
		public String toString() {
			return "RequestMessage [body=" + body + ", id=" + id + ", time=" + time + ", sign=" + sign + "]";
		}
	}
	
	public static class Callback {
		
		public static Message parse (String responseStr, Signed signed){
			
			Message result = JsonX.toObject(responseStr, Message.class);
			if (result.getStatus().equals(FAIL))
				return result;
			
			if (! result.getSign().equals(signed.sign()))
				throw new SignException(TOAST_SIGN_EXCEPTION);
			
			return result;
		}
		
	}
	
	public static class SendMessage {
		public static Message toast(String str) {
			Message message = new Message();
			message.setStatus(FAIL);
			message.setBody(str);
			return message;
		}

		public static Message ok(Object obj, Signed signed) {

			Message message = new Message();
			message.setStatus(OK);
			message.setBody(obj);
			message.setId(signed.getId());
			message.setTime(signed.getTime());
			message.setSign(signed.sign());
			return message;

		}
	}

	public static class Message<T> {

		private String status;
		private T body;
		private String id;
		private long time;
		private String sign;


		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public T getBody() {
			return body;
		}

		public void setBody(T body) {
			this.body = body;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public String getSign() {
			return sign;
		}

		public void setSign(String sign) {
			this.sign = sign;
		}

		@Override
		public String toString() {
			return "Message [status=" + status + ", body=" + body + ", id=" + id
					+ ", time=" + time + ", sign=" + sign + "]";
		}

	}
}
