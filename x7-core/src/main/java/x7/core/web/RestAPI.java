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
	
	interface Signed {
		String sign ();
		long getTime();
		String getUserId();
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
	
	public class RequestMessage {
		
		private Object body;
		private String userId;
		private long time;
		private String sign;
		
		public boolean isSigned(Signed signed) {
			return sign.endsWith(signed.sign());
		}
		
		public Object getBody() {
			return body;
		}
		public void setBody(Object body) {
			this.body = body;
		}
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
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
			return "RequestMessage [body=" + body + ", userId=" + userId + ", time=" + time + ", sign=" + sign + "]";
		}
	}
	
	public static class Callback {
		
		public static Message parse (String responseStr, Signed signed){
			
			Message result = JsonX.toObject(responseStr, Message.class);
			if (result.getStatus().equals(FAIL))
				return result;
			
			if (! result.getSign().equals(signed.sign()))
				throw new SignException("SIGN UNAVAILABLE");
			
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
			message.setUserId(signed.getUserId());
			message.setTime(signed.getTime());
			message.setSign(signed.sign());
			return message;

		}
	}

	public static class Message {

		private String status;
		private Object body;
		private String userId;
		private long time;
		private String sign;


		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Object getBody() {
			return body;
		}

		public void setBody(Object body) {
			this.body = body;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
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
			return "Message [status=" + status + ", body=" + body + ", userId=" + userId
					+ ", time=" + time + ", sign=" + sign + "]";
		}

	}
}
