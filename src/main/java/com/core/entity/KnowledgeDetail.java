package com.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class KnowledgeDetail {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long id;
	
	@Column(name="knowledge_id",unique=true)
	public Long knowledgeId;
	
	@Lob
	@Column(name="knowledge_info")
	public String info;
	

	@Column(name="knowledge_title")
	public String title;
	
	@Column(name="knowledge_author")
	public String author;
	
	
	@Lob
	@Column(name="knowledge_gainian")
	public String gainian;
	
	@Lob
	@Column(name="knowledge_target")
	public String target;
	
	@Lob
	@Column(name="knowledge_use")
	public String use;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(Long knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getGainian() {
		return gainian;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setGainian(String gainian) {
		this.gainian = gainian;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	

}
