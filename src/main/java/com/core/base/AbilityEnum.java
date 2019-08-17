package com.core.base;

import java.time.LocalDate;

/**
 * 
 * @author 星志
 *
 */
public enum  AbilityEnum {
    excellent(true,4500,9000),  //热搜
    general(false,0,4500);     //普通

    private boolean name;

    private int scoreRangemin;

    private int scoreRangemax;

    AbilityEnum(boolean name, int scoreRangemin, int scoreRangemax) {
        this.name = name;
        this.scoreRangemin = scoreRangemin;
        this.scoreRangemax = scoreRangemax;
    }

    

    public boolean isName() {
		return name;
	}



	public void setName(boolean name) {
		this.name = name;
	}



	public int getScoreRangemin() {
		return scoreRangemin;
	}



	public void setScoreRangemin(int scoreRangemin) {
		this.scoreRangemin = scoreRangemin;
	}



	public int getScoreRangemax() {
		return scoreRangemax;
	}



	public void setScoreRangemax(int scoreRangemax) {
		this.scoreRangemax = scoreRangemax;
	}



	public static boolean of(int value) {
        for (AbilityEnum status : AbilityEnum.values()) {
            if(value>=status.getScoreRangemin() && value <= status.getScoreRangemax()) {
            	return status.isName();
            }
            	
        }
        
        return true;
    }
	
	


}



