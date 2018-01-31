/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package x7.core.web;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import x7.core.search.Tag;

/**
 * Pagination
 * @author sim
 *
 * @param <T>
 */
public class Pagination<T> implements Paged, Serializable{
	
	private static final long serialVersionUID = -3917421382413274341L;

	private int rows = 20;
	private int page = 1;
	private long totalRows = -1;
	private List<T> list = new ArrayList<T>();
	private List<String> keyList = new ArrayList<String>();
	private boolean isScroll;
	private String orderBy;
	private Direction direction = Direction.DESC;
	
	private Tag tag;
	
	public Pagination(){
	}
	
	public Pagination(int page, int rows, String orderBy){
		setPage(page);
		setRows(rows);
		this.orderBy = orderBy;
	}
	
	public Pagination(int page, int rows, String orderBy, Direction direction){
		setPage(page);
		setRows(rows);
		this.orderBy = orderBy;
		this.direction = direction;
	}
	
	public Pagination(Paged paged){
		setScroll(paged.isScroll());
		if (paged.getPage() > 0)
			setPage(paged.getPage());
		if (paged.getRows() > 0)
			setRows(paged.getRows());
		if (paged.getOrderBy() != null){
			this.orderBy = paged.getOrderBy();
		}
		if (paged.getDirection() != null){
			this.direction = paged.getDirection();
		}
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction sc) {
		this.direction = sc;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public long getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(long totalRows) {
		this.totalRows = totalRows;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public List<String> getKeyList() {
		return keyList;
	}

	public void setKeyList(List<String> keyList) {
		this.keyList = keyList;
	}

	public boolean isScroll() {
		return isScroll;
	}

	public void setScroll(boolean isScroll) {
		this.isScroll = isScroll;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public int getTotalPages() {
		int totalPages = (int) (totalRows / rows);
		if (totalRows % rows > 0)
			totalPages += 1;
		return totalPages;
	}
	
	public int getPage() {
		if (isScroll){
			return page;
		}
		if (totalRows == -1)
			return page;
		if (totalRows == 0)
			return 1;
		int maxPage = (int) (totalRows / rows);
		if (totalRows % rows > 0)
			maxPage += 1;
		if (page > maxPage)
			page = maxPage;
		if (page < 1)
			return 1;
		return page;
	}
	
	@Override
	public String toString() {
		return "Pagination [rows=" + rows + ", page=" + page + ", totalRows=" + totalRows + ", list=" + list
				+ ", keyList=" + keyList + ", isScroll=" + isScroll + ", orderBy=" + orderBy + ", sc=" + direction + ", tag="
				+ tag + "]";
	}

}
