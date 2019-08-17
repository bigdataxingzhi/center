package com.core.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.base.DataFactory;
import com.core.base.HotData;
import com.core.base.SearchData;
import com.core.base.SearchPojo;
import com.core.dto.KnowledgeDetailDto;
import com.core.entity.KnowledgeDetail;
import com.core.es.util.BucketResult;
import com.core.es.util.IndexMessage;
import com.core.es.util.IndexTemplate;
import com.core.es.util.RentSearch;
import com.core.es.util.SearchSort;
import com.core.es.util.ServiceMultiResult;
import com.core.es.util.ServiceResult;
import com.core.service.ElasticSearchOptionImpl;
import com.core.service.KnowledgeService;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;


@Controller
@RequestMapping(value = "/search")
public class SearchController {
	

	@Autowired
	ElasticSearchOptionImpl esOption;

	@Autowired
	KnowledgeService knowledgeService;
	
	/**
	 * 关键词字段补全接口
	 * @param prefix
	 * @return
	 */
	@RequestMapping(value = "suggest", method = RequestMethod.GET)
	@ResponseBody
	public List<String> searchSuggest(String prefix) {

		ArrayList<String> result = Lists.newArrayList();

		prefix = prefix.trim();

		if (StringUtils.isEmpty(prefix)) {

		} else {
			ServiceResult<List<String>> serviceResult = esOption.suggest(prefix);
			result = (ArrayList<String>) serviceResult.getResult();
		}

		return result;
	}

	
	
	/**
	 * 根据关键词分页查询数据
	 * @param param
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(value = "searchData", method = RequestMethod.GET)
	@ResponseBody
	public SearchPojo searchData(String param,int page,int size) {

		
		SearchPojo searchPojo = DataFactory.getData(SearchPojo.class);
		if(StringUtils.isEmpty(param)) {
			searchPojo.setRows(Lists.newArrayList());
			searchPojo.setTotal(0L);
			return searchPojo;
		}
		SearchSort searchSort = new SearchSort("love");
		RentSearch rentSearch = new RentSearch();	
		int start = (page - 1) * 5;
		rentSearch.setStart(start);
		rentSearch.setSize(5);
		String[] split = StringUtils.split(param," ");
		SearchData searchData = DataFactory.getData(SearchData.class);
		if(split.length==2) {
			searchPojo.setTotal(esOption.getTotalHits(split[0], split[1],null));
			//如果传入两个参数，则按照他title与subtitle进行搜索
			ServiceMultiResult<SearchData> queryData = esOption.queryData(split[0], split[1],rentSearch);
			
			searchPojo.setRows(queryData.getResult());
			
		}else {
			searchPojo.setTotal(esOption.getTotalHits(split[0], split[0],null));
			ServiceMultiResult<SearchData> queryData = null;
			if(searchPojo.getTotal()==0) {
				searchPojo.setTotal(esOption.getTotalHitsForRetry(split[0]));
				/*should ==>title  should==>item*/

				queryData = esOption.queryDataForRetry(split[0], rentSearch);
			}else {
				/*must==>title    should subtitle*/
				 queryData = esOption.queryData(split[0], split[0],rentSearch);
			}
			if(queryData==null) {
				searchPojo.setRows(Lists.newArrayList());
			}else {
				
				searchPojo.setRows(queryData.getResult());
			}
		}
		
		/**
		 * 查询猜你想搜
		 */
		RentSearch searchGuss = new RentSearch();
		searchGuss.setStart(0);
		searchGuss.setSize(3);
		searchGuss.setOrderDirection("desc");
 		ServiceMultiResult<SearchData> queryLoveData = esOption.queryLoveData(split[0], searchGuss, searchSort);
 		if(queryLoveData.getResult() == null) {
 			searchPojo.setGusses(Lists.newArrayList());
 		}else{
 			
 			searchPojo.setGusses(queryLoveData.getResult());
 		}
		return searchPojo;

	}
	
	
	
	/**
	 * 查询热搜排行榜
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "aggHotdate", method = RequestMethod.GET)
	@ResponseBody
	public List<HotData> searchAggHotdate(int page) {

		ArrayList<HotData> result = Lists.newArrayList();

		ServiceMultiResult<BucketResult> mapAggregate = esOption.mapAggregate("item");
		List<BucketResult> bucketResults = mapAggregate.getResult();
		String key = bucketResults.get(page-1).getKey();
		RentSearch rentSearch = new RentSearch();
		rentSearch.setOrderBy("count");
		rentSearch.setOrderDirection("desc");
		int start = (1 - 1) * 6;
		rentSearch.setStart(start);
		rentSearch.setSize(6);
		SearchSort searchSort = new SearchSort("count");
		ServiceMultiResult<HotData> queryHotdata = esOption.queryHotdata(key, rentSearch, searchSort);
		result = (ArrayList<HotData>) queryHotdata.getResult();
			
			return result;
		
	}
	
	/*private List<HotData> searchAggHotdateforRetry(int page,int retry) {

		ArrayList<HotData> result = Lists.newArrayList();
		if(retry>5) {
			return result;
		}

		ServiceMultiResult<BucketResult> mapAggregate = esOption.mapAggregate("item");
		List<BucketResult> bucketResults = mapAggregate.getResult();
		String key = bucketResults.get(retry).getKey();
		RentSearch rentSearch = new RentSearch();
		rentSearch.setOrderBy("count");
		rentSearch.setOrderDirection("desc");
		int start = (page - 1) * 6;
		rentSearch.setStart(start);
		rentSearch.setSize(6);
		SearchSort searchSort = new SearchSort("count");
		ServiceMultiResult<HotData> queryHotdata = esOption.queryHotdata(key, rentSearch, searchSort);
		result = (ArrayList<HotData>) queryHotdata.getResult();
		if(result.size()>0) {
			
			return result;
		}else {
			return searchAggHotdateforRetry(page,retry+1);
		}

	}*/
	
	/**
	 * 点赞功能实现
	 */
	@RequestMapping("love")
	@ResponseBody
	public void love(String docId, String love) {
		if(StringUtils.isNotEmpty(docId) && StringUtils.isNotEmpty(love)) {
			SearchData searchData = this.esOption.queryOneData(docId);
			searchData.setLove(Integer.valueOf(love));
			IndexTemplate indexTemplate = DataFactory.getData(IndexTemplate.class);
			BeanUtils.copyProperties(searchData, indexTemplate);
			indexTemplate.setSearchId(Longs.tryParse(searchData.getDocId()));
			indexTemplate.setCount(Integer.valueOf(searchData.getCount()));
			indexTemplate.setPublishdate(searchData.getPublishDate());
			try {
				this.esOption.handleMessage(indexTemplate,IndexMessage.INDEX,null);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
	}
	
	/**
	 * 根据id展示知识点详情
	 * @param docId
	 * @return
	 */
	@RequestMapping("detail/{docId}")
	public ModelAndView getDetail(@PathVariable(value="docId",required=true) String docId) {
		
		ServiceResult result=  this.knowledgeService.findByKnowledgeId(Longs.tryParse(docId));
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("detail");
		modelAndView.addObject("knowledge", result.getResult());
		return modelAndView;
	}
	
	
	
	
	

}
