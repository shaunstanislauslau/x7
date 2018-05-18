package x7.core.bean;

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
		String json = JsonX.toJson(obj);
		byte[] bytes = Base64.encodeBase64(json.getBytes());
		text = new String(bytes);
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public T getObj() {
		if (Objects.isNull(obj) && !Objects.isNull(text)){
			byte[] bytes = Base64.decodeBase64(text);
			String json = new String(bytes);

			obj = (T) JsonX.toObject(json, clz);
			return obj;
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
