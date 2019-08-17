package com.core.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.base.ApiResponse;
import com.core.base.DataFactory;
import com.core.base.ManagerPojo;
import com.core.base.SearchData;
import com.core.dto.KnowledgeDetailDto;
import com.core.entity.KnowledgeDetail;
import com.core.entity.User;
import com.core.es.util.IndexMessage;
import com.core.es.util.IndexTemplate;
import com.core.es.util.RangeDate;
import com.core.es.util.RentSearch;
import com.core.es.util.SearchSort;
import com.core.es.util.ServiceMultiResult;
import com.core.es.util.ServiceResult;
import com.core.service.ElasticSearchOptionImpl;
import com.core.service.KnowledgeService;
import com.core.util.IDUtils;
import com.google.common.primitives.Longs;

@Controller
public class ManagerController {
	@Autowired
	ElasticSearchOptionImpl searchService;
	
	@Autowired
	KnowledgeService knowledgeService;
	
	/**
	 * 根据日期范围分页查询知识点
	 * @param page
	 * @param size
	 * @param dataRange
	 * @return
	 */
	@RequestMapping("/manager/findPage")
	@ResponseBody
	public ManagerPojo findByAuthor(int page,int size,String dataRange) {
		RentSearch rentSearch = new RentSearch();	
		int start = (page - 1) * size;
		rentSearch.setStart(start);
		rentSearch.setSize(size);
		rentSearch.setOrderBy("publishdate");
		rentSearch.setOrderDirection("desc");
		SearchSort searchSort = new SearchSort("publishdate");
		
		if(StringUtils.isNotEmpty(dataRange)) {
			return this.findByDateRange(rentSearch, searchSort,dataRange);
		}
		ServiceMultiResult<SearchData> queryByAuthor = searchService.queryByAuthor(rentSearch,searchSort);
		ManagerPojo pojo = new ManagerPojo();
		pojo.setRows(queryByAuthor.getResult());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		pojo.setTotal(searchService.getTotalHits(null, null, user.getUserId().toString()));
		return pojo;
		
	}
	
	
	/**
	 * 查询单条记录
	 * @param docId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/manager/findOne")
	public SearchData findOne(@RequestParam(value="id") String docId) {
		if(StringUtils.isNotEmpty(docId)) {
			SearchData searchData = this.searchService.queryOneData(docId);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User)authentication.getPrincipal();
			searchData.setAuthor(user.getUsername());
			return searchData;
		}
			return new SearchData();	
	}
	
	
	

	private ManagerPojo findByDateRange(RentSearch rentSearch,SearchSort searchSort,String dataRange) {
		if(StringUtils.isEmpty(dataRange)) {
			return new ManagerPojo();
		}
		String[] range = StringUtils.split(dataRange,"~");
		RangeDate rangeDate = new RangeDate();
		rangeDate.setMin(range[0]);
		rangeDate.setMax(range[1]);
		rentSearch.setRangeDate(rangeDate);
		ServiceMultiResult<SearchData> queryByAuthor = searchService.queryByAuthor(rentSearch,searchSort);
		ManagerPojo pojo = new ManagerPojo();
		pojo.setRows(queryByAuthor.getResult());
		pojo.setTotal(searchService.getTotalHitsByRange(range[0], range[1]));
		return pojo;
		
	}
	
	
	/**
	 * 更新数据
	 * @param searchData
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/manager/update",method=RequestMethod.POST)
	public ApiResponse update(@RequestBody SearchData searchData) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		IndexTemplate indexTemplate = DataFactory.getData(IndexTemplate.class);
		BeanUtils.copyProperties(searchData, indexTemplate);
		indexTemplate.setSearchId(Longs.tryParse(searchData.getDocId()));
		indexTemplate.setCount(Integer.valueOf(searchData.getCount()));
		indexTemplate.setPublishdate(searchData.getPublishDate());
		indexTemplate.setAuthor(user.getUserId().toString());
		String replace = StringUtils.replace(indexTemplate.getInfo(), "\n", "");
		replace = StringUtils.replace(replace, "\t", "");
		indexTemplate.setInfo(replace);
	try {
		this.searchService.handleMessage(indexTemplate,IndexMessage.INDEX,null);
		return  ApiResponse.ofSuccess(null);
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	return ApiResponse.ofMessage(500, "经过系统3次重试，修改仍然失败,请认真核对数据后，再次尝试提交！");

}
	
	

	/**
	 * 批量删除数据
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/manager/delete")
	public ApiResponse update(String[] ids) {
	try {
		this.knowledgeService.deleteByBatch(ids);
		this.searchService.handleMessage(null, IndexMessage.REMOVE, ids);
		
		return  ApiResponse.ofSuccess(null);
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	return ApiResponse.ofMessage(500, "经过系统3次重试，删除数据部分失败,请认真核对数据后，再次尝试提交！");

}
	
private void formatString(KnowledgeDetailDto knowledgeDetailDto){
	String info = StringUtils.replace(knowledgeDetailDto.getInfo(), "\n", "");
	info = StringUtils.replace(info, "\t", "");
	knowledgeDetailDto.setInfo(info);
	String replacegainian = StringUtils.replace(knowledgeDetailDto.getGainian(), "\n", "");
	replacegainian = StringUtils.replace(replacegainian, "\t", "");
	knowledgeDetailDto.setGainian(replacegainian);
	String target = StringUtils.replace(knowledgeDetailDto.getTarget(), "\n", "");
	target = StringUtils.replace(target, "\t", "");
	knowledgeDetailDto.setTarget(target);
	String use = StringUtils.replace(knowledgeDetailDto.getUse(), "\n", "");
	use = StringUtils.replace(use, "\t", "");
	knowledgeDetailDto.setUse(use);
	}
	
	@RequestMapping(value = "/manager/save",method = RequestMethod.POST)
	public ModelAndView getDetail(KnowledgeDetailDto knowledgeDetailDto ) throws Exception {
		StringBuffer titleBuffer = new StringBuffer();
		titleBuffer.append(knowledgeDetailDto.getTitle()).append("之");
		titleBuffer.append(knowledgeDetailDto.getSubtitle());
		//TODO 设置用户
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		knowledgeDetailDto.setAuthor(user.getUserId().toString());
		Long docID = IDUtils.genDocId();
		knowledgeDetailDto.setKnowledgeId(docID);
		long currentTimeMillis = System.currentTimeMillis();
		Date date = new Date(currentTimeMillis);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String publishDate = format.format(date);
		knowledgeDetailDto.setPublishdate(publishDate);
		formatString(knowledgeDetailDto);
		IndexTemplate indexTemplate  = new IndexTemplate();
		BeanUtils.copyProperties(knowledgeDetailDto, indexTemplate);
		indexTemplate.setCount(0);
		indexTemplate.setLove(0);
		indexTemplate.setDocId(docID.toString());
		indexTemplate.setSearchId(docID);
		this.searchService.handleMessage(indexTemplate, IndexMessage.INDEX, null);
		KnowledgeDetail knowledgeDetail = new KnowledgeDetail();
		BeanUtils.copyProperties(knowledgeDetailDto, knowledgeDetail);
		knowledgeDetail.setKnowledgeId(docID);
		knowledgeDetail.setTitle(titleBuffer.toString());
		this.knowledgeService.saveknowledge(knowledgeDetail);
		
		ServiceResult result=  this.knowledgeService.findByKnowledgeId(docID);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("detail");
		modelAndView.addObject("knowledge", result.getResult());
		return modelAndView;
	}
	
	
}