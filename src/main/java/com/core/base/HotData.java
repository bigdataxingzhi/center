package com.core.base;

import java.io.Serializable;
import java.time.LocalDate;

public class HotData implements Serializable {


	private static final long serialVersionUID = 994555176719343064L;

	private String docId;
	
	private String dataInfo;
	
	private Boolean rank;
	
	private int count;
	
	

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(String dataInfo) {
		this.dataInfo = dataInfo;
	}

	public Boolean getRank() {
		return rank;
	}

	public void setRank(Boolean rank) {
		this.rank = rank;
	}
	
	

		
	
}
