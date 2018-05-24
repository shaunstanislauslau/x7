package x7.core.bean;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;

import x7.core.util.JsonX;

public class EncryptionObject<T> {

	private String text;
	private Class<T> clz;
	private transient T obj;
	
	public Class<T> getClz() {
		return clz;
	}
	public void setClz(Class<T> clz) {
		this.clz = clz;
	}
	public String getText() {
		if (!Objects.isNull(text))
			return text;
		try {
			String json = JsonX.toJson(obj);
			byte[] bytes = Base64.encodeBase64(json.getBytes("UTF-8"));
			text = new String(bytes,"UTF-8");
			return text;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

	}
	public void setText(String text) {
		this.text = text;
	}
	public T getObj() {
		if (Objects.isNull(obj) && !Objects.isNull(text)){
			byte[] bytes = Base64.decodeBase64(text);
			try {
				String json = new String(bytes,"UTF-8");
				obj = (T) JsonX.toObject(json, clz);
				return obj;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}
		return obj;
	}
	public void setObj(T obj) {
		this.obj = obj;
		this.clz = (Class<T>) obj.getClass();
	}
	@Override
	public String toString() {
		return ""+getObj();
	}
	
}
