package com.core.base;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * @author 星志
 *
 */
public class SearchPojo extends ManagerPojo implements Serializable{

	
	private static final long serialVersionUID = -4288197604896487386L;

	
	private List<?> gusses = Lists.newArrayList();
 	

	public List<?> getGusses() {
		return gusses;
	}

	public void setGusses(List<?> gusses) {
		this.gusses = gusses;
	}

	
	
}
