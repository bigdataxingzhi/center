package com.core.service;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.base.DataFactory;
import com.core.base.SearchData;
import com.core.entity.KnowledgeDetail;
import com.core.entity.User;
import com.core.es.util.IndexMessage;
import com.core.es.util.IndexTemplate;
import com.core.es.util.ServiceResult;
import com.core.repository.KnowledgDdetailRepository;
import com.core.repository.UserRepository;
import com.google.common.primitives.Longs;

@Service
public class KnowledgeService {
	
	@Autowired
	KnowledgDdetailRepository knowledgDdetailRepository;
	
	@Autowired
	ElasticSearchOptionImpl esOptional;
	
	@Autowired
	UserRepository userRepository;
	
	public ServiceResult findByKnowledgeId(Long knowledgeId){
		
		KnowledgeDetail findByKnowledge = knowledgDdetailRepository.findByKnowledgeId(knowledgeId);
		User user = userRepository.findOne(Longs.tryParse(findByKnowledge.getAuthor()));
		//更新es,将count数量加一
		try {
		SearchData searchData = esOptional.queryOneData(knowledgeId.toString());
		int count = Integer.parseInt(searchData.getCount()) + 1;
		searchData.setCount(count+"");
		IndexTemplate updateData = DataFactory.getData(IndexTemplate.class);
		BeanUtils.copyProperties(searchData, updateData);
		updateData.setSearchId(Longs.tryParse(searchData.getDocId()));
		updateData.setCount(Integer.valueOf(searchData.getCount()));
		updateData.setPublishdate(searchData.getPublishDate());
		updateData.setSearchId(Longs.tryParse(searchData.getDocId()));
		
		esOptional.handleMessage(updateData, IndexMessage.INDEX, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(user!=null){
			findByKnowledge.setAuthor(user.getUsername());
		}
		return ServiceResult.of(findByKnowledge);
		
		
	}

	@Transactional
	public void saveknowledge(KnowledgeDetail knowledgeDetail) {
		knowledgDdetailRepository.save(knowledgeDetail);
		
	}

	@Transactional
	public void deleteByBatch(String[] ids) {
			
		for (String id : ids) {
		KnowledgeDetail findByKnowledge = this.knowledgDdetailRepository.findByKnowledgeId(Longs.tryParse(id));
		this.knowledgDdetailRepository.delete(findByKnowledge.getId());
			
		}
		
	}

}
