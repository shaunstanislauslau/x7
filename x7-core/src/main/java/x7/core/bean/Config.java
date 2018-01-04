package x7.core.bean;

import java.io.Serializable;

import x7.core.repository.X;

public class Config implements Serializable{

	private static final long serialVersionUID = 2987956602648283202L;
	@X.ID
	private String keyX;
	private String value;
	public String getKeyX() {
		return keyX;
	}
	public void setKeyX(String key) {
		this.keyX = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Config [keyX=" + keyX + ", value=" + value + "]";
	}
}
