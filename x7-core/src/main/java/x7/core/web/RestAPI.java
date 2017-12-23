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
		
		public static Result parse (String responseStr, Signed signed){
			
			Result result = JsonX.toObject(responseStr, Result.class);
			if (result.getStatus().equals(FAIL))
				return result;
			
			if (! result.getSign().equals(signed.sign()))
				throw new SignException("SIGN UNAVAILABLE");
			
			return result;
		}
		
	}

	public static class Result {

		private String status;
		private Object body;
		private String userId;
		private long time;
		private String sign;

		public static Result toast(String message) {
			Result apiResult = new Result();
			apiResult.setStatus(FAIL);
			apiResult.setBody(message);
			return apiResult;
		}

		public static Result ok(Object result, Signed signed) {

			Result api = new Result();
			api.setStatus(OK);
			api.setBody(result);
			api.setUserId(signed.getUserId());
			api.setTime(signed.getTime());
			api.setSign(signed.sign());
			return api;

		}

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
			return "Result [status=" + status + ", body=" + body + ", userId=" + userId
					+ ", time=" + time + ", sign=" + sign + "]";
		}

	}
}
