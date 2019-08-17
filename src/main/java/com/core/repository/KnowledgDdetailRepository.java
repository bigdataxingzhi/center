package com.core.repository;

import com.core.entity.KnowledgeDetail;

public interface KnowledgDdetailRepository extends BaseRepository<KnowledgeDetail>{
	
	KnowledgeDetail findByKnowledgeId(Long knowledgeId);

}
