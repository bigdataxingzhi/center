package com.core.base;

/**
 * 
 * @author 星志
 *
 */
public class DataFactory {
	
	public static <T> T getData(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
