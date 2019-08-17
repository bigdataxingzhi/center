package com.core.base;

import java.io.Serializable;
import java.util.List;

public class ManagerPojo implements Serializable {

	private static final long serialVersionUID = 1L;

	private long total = 0;
	
	private List<?> rows = null;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}
	
	
	
}
