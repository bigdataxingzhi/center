package com.core.es.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;

/**
 * 
 * @author 星志
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexTemplate {

	private Long searchId;
	private String docId;

	private List<SearchSuggest> suggest = null;

	private String item;
	private String title;
	private String subtitle;
	private String author;
	private String publishdate;
	private Integer count = 0;
	private String  info;
	private Integer love = 0;
	

	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public Long getSearchId() {
		return searchId;
	}
	public void setSearchId(Long searchId) {
		this.searchId = searchId;
	}
	public List<SearchSuggest> getSuggest() {
		return suggest;
	}
	public void setSuggest(List<SearchSuggest> suggest) {
		this.suggest = suggest;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublishdate() {
		return publishdate;
	}
	public void setPublishdate(String publishdate) {
		this.publishdate = publishdate;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Integer getLove() {
		return love;
	}
	public void setLove(Integer love) {
		this.love = love;
	}
	
	

}
