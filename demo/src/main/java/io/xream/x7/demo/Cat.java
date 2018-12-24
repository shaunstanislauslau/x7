package io.xream.x7.demo;

import x7.core.repository.X;

import java.util.List;

public class Cat {

	@X.Key
	private long id;
	@X.Mapping("cat_type")
	private String type;
	private String taxType;
	private long dogId;
	private long test;
	private List<Long> list;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		System.out.println("JSON->OBJ,SETTER");
		this.type = type;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public long getDogId() {
		return dogId;
	}

	public void setDogId(long dogId) {
		this.dogId = dogId;
	}

	public long getTest() {
		return test;
	}

	public void setTest(long test) {
		this.test = test;
	}

	public List<Long> getList() {
		return list;
	}

	public void setList(List<Long> list) {

		this.list = list;
	}

	@Override
	public String toString() {
		return "zxt.oop.xxx.Cat{" +
				"id=" + id +
				", type='" + type + '\'' +
				", taxType='" + taxType + '\'' +
				'}';
	}
}
