package com.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.core.base.AbilityEnum;
import com.core.base.DataFactory;
import com.core.base.HotData;
import com.core.base.SearchData;
import com.core.entity.User;
import com.core.es.util.BucketResult;
import com.core.es.util.IndexMessage;
import com.core.es.util.IndexTemplate;
import com.core.es.util.RentSearch;
import com.core.es.util.SearchSort;
import com.core.es.util.SearchSuggest;
import com.core.es.util.ServiceMultiResult;
import com.core.es.util.ServiceResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

/**
 * 
 * @author 星志
 *
 */
@Service
public class ElasticSearchOptionImpl {
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchOptionImpl.class);

	private static final String INDEX_NAME = "javaee";

	private static final String INDEX_TYPE = "knowledge";

	private String searchAgg = "searchAgg";

	@Autowired
	private TransportClient esClient;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public ElasticSearchOptionImpl() {
		super();

	}

	public void handleMessage(IndexTemplate indexTemplate, String option,String[] ids) throws Exception {

		switch (option) {
		// 创建索引
		case IndexMessage.INDEX:
			this.createOrUpdateIndex(indexTemplate, 0);
			break;
		// 删除索引
		case IndexMessage.REMOVE:
			this.removeIndex(ids, 0);
			break;
		default:
			logger.warn("Not support message content " + option);
			break;
		}

	}

	// 创建或者更新索引
	private void createOrUpdateIndex(IndexTemplate indexTemplate, int retry) throws Exception {
		Long docId = Longs.tryParse(indexTemplate.getDocId());
		// 判断查询结果
		boolean success;

		/** 按照searchId进行查询 */
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(QueryBuilders.termQuery("docId", docId));
		logger.debug(requestBuilder.toString());
		/** 得到查询返回的SearchResponse接口 */
		SearchResponse searchResponse = requestBuilder.get();
		/** 得到查询总数 */
		long totalHit = searchResponse.getHits().getTotalHits();
		/** es中查到的总数为0,进入添加数据分支 */
		if (totalHit == 0) {
			success = create(indexTemplate);
		}
		/** es中查到的总数为1,进入更新分支 */
		else if (totalHit == 1) {
			String esId = searchResponse.getHits().getAt(0).getId();
			success = update(esId, indexTemplate);
		}
		/** es中查到的总数大于0,则发生了异常,则先删除所有,在添加. */
		else {
			success = deleteAndCreate(totalHit, indexTemplate);
		}

		// 如果失败,则重新尝试.
		if (!success) {
			this.index(indexTemplate, retry + 1);
		} else {
			logger.debug("Index success with index " + docId);

		}
	}

	private void removeIndex(String[] ids, int retry) {
		for (String docId : ids) {
			
			removeOne(docId);
		}
	}

	private void removeOne(String searchId) {
		Long indexId = Longs.tryParse(searchId);
				
		DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
				.filter(QueryBuilders.termQuery("docId", indexId)).source(INDEX_NAME);

		logger.debug("Delete by query for house: " + builder);

		BulkByScrollResponse response = builder.get();
		long deleted = response.getDeleted();
		logger.debug("Delete total " + deleted);
	}

	public void index(IndexTemplate indexTemplate) throws Exception {
		this.index(indexTemplate, 0);
	}

	private void index(IndexTemplate indexTemplate, int retry) throws Exception {
		// 如果上一步消息处理时发生异常,则应该重试几次
		// 重试次数未打上线时,可重试
		if (retry > IndexMessage.MAX_RETRY) {
			logger.error("Retry index times over 3 for index: " + indexTemplate.getSearchId() + " Please check it!");
			return;
		}
		createOrUpdateIndex(indexTemplate, retry);

	}

	public boolean create(IndexTemplate indexTemplate) {
		if (!updateSuggest(indexTemplate)) {
			return false;
		}

		try {
			/**
			 * 向es中添加索引
			 */
			IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
					.setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON)
					.setId(indexTemplate.getSearchId().toString()).get();

			logger.debug("Create index with index: " + indexTemplate.getSearchId());
			/** 创建成功 */
			if (response.status() == RestStatus.CREATED) {
				return true;
			} else {
				return false;
			}
		} catch (JsonProcessingException e) {
			logger.error("Error to index :" + indexTemplate.getSearchId(), e);
			return false;
		}
	}

	private boolean update(String esId, IndexTemplate indexTemplate) {
		if (!updateSuggest(indexTemplate)) {
			return false;
		}

		try {
			/**
			 * 向es中更新索引
			 */
			UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
					.setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

			logger.debug("Update index {}: ", indexTemplate.getSearchId());
			if (response.status() == RestStatus.OK) {
				return true;
			} else {
				return false;
			}
		} catch (JsonProcessingException e) {
			logger.error("Error to index :{}", indexTemplate.getSearchId(), e);
			return false;
		}
	}

	private boolean deleteAndCreate(long totalHit, IndexTemplate indexTemplate) {
		/** 按实例文档进行删除. */
		DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
				.filter(QueryBuilders.termQuery("docId", indexTemplate.getDocId())).source(INDEX_NAME);

		// 打印查询语句
		logger.debug("Delete by query for house: " + builder);

		/** 注意此处写法 */
		BulkByScrollResponse response = builder.get();
		long deleted = response.getDeleted();
		// 如果在ES中删除的总数不等于传入的总数,打印警告
		if (deleted != totalHit) {
			logger.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
			return false;
		} else {
			return true;
		}
	}

	public void remove(Long houseId) {
		this.remove(houseId, 0);
	}

	private void remove(Long searchId, int retry) {
		if (retry > IndexMessage.MAX_RETRY) {
			logger.error("Retry remove times over 3 for index: " + searchId + " Please check it!");
			return;
		}

	}

	/**
	 * es 按照传入的RentSearch所包装的条件进行查询.
	 * 
	 * @param rentSearch
	 * @return
	 */

	public ServiceMultiResult<HotData> queryHotdata(String itemValue, RentSearch rentSearch, SearchSort searchSort) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.must(QueryBuilders.termQuery("item", itemValue));
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder).addSort(searchSort.getSortKey(rentSearch.getOrderBy()), // 默认排序字段
						SortOrder.fromString(rentSearch.getOrderDirection())// 排序方式
				).setFrom(rentSearch.getStart())// 开始数
				.setSize(rentSearch.getSize());// 查询几条

		logger.debug(requestBuilder.toString());

		List<HotData> hotdatas = new ArrayList<>();
		SearchResponse response = requestBuilder.get();
		if (response.status() != RestStatus.OK) {
			logger.warn("Search status is no ok for " + requestBuilder);
			return new ServiceMultiResult<>(0, hotdatas);
		}
		// SearchHit为返回结果
		for (SearchHit hit : response.getHits()) {
			HotData hotData = new HotData();
			System.out.println(hit.getSource());
			StringBuffer hotBuffer = new StringBuffer();
			hotBuffer.append(hit.getSource().get("title")).append("之");
			hotBuffer.append(hit.getSource().get("subtitle"));
			hotData.setDataInfo(hotBuffer.toString());
			hotData.setRank(AbilityEnum.of((int) hit.getSource().get("count")));
			hotData.setDocId(hit.getSource().get("docId").toString());
			hotData.setCount((int)hit.getSource().get("count"));
			hotdatas.add(hotData);
			logger.info("Search status is ok for" + hotdatas);
		}
		return new ServiceMultiResult<>(response.getHits().totalHits, hotdatas);
	}
	
	
	public long getTotalHits(String titleValue, String subtitleValue,String author) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		if(StringUtils.isNotEmpty(titleValue) && StringUtils.isNotEmpty(subtitleValue)) {
		boolQueryBuilder.must(QueryBuilders.termQuery("title", titleValue));
		boolQueryBuilder.should(QueryBuilders.matchQuery("subtitle", subtitleValue));
		}
		
		if(StringUtils.isNotEmpty(author)) {
			boolQueryBuilder.must(QueryBuilders.termQuery("author", author));
	}
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder).setFetchSource(false);
		logger.debug(requestBuilder.toString());
		SearchResponse response = requestBuilder.get();
		long total = response.getHits().totalHits;
		return response.getHits().totalHits;
		
	}
	
	
	public long getTotalHitsByRange(String dataMin, String dateMax) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		
		if(StringUtils.isEmpty(dataMin) && StringUtils.isNotEmpty(dateMax)) {
			return this.getTotalHits(null, null, user.getUserId().toString());
		}
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("publishdate");
		if(StringUtils.isNotEmpty(dateMax) ) {
				rangeQuery.lte(dateMax);
			}
		
		if(StringUtils.isNotEmpty(dataMin)) {
			rangeQuery.gte(dataMin);
		}
	
		boolQueryBuilder.filter(rangeQuery);

		boolQueryBuilder.must(QueryBuilders.termQuery("author", user.getUserId().toString()));

		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder).setFetchSource(false);
		logger.debug(requestBuilder.toString());
		SearchResponse response = requestBuilder.get();
		long total = response.getHits().totalHits;
		return response.getHits().totalHits;
		
	}
	
	
	public long getTotalHitsForRetry(String value) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.should(QueryBuilders.termQuery("item", value));
		boolQueryBuilder.should(QueryBuilders.matchQuery("title", value));
		boolQueryBuilder.should(QueryBuilders.matchQuery("subtitle", value));
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder).setFetchSource(false);
		logger.debug(requestBuilder.toString());
		SearchResponse response = requestBuilder.get();
		long total = response.getHits().totalHits;
		return response.getHits().totalHits;
		
	}
	
	public ServiceMultiResult<SearchData> queryData(String titleValue, String subtitleValue,RentSearch rentSearch) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.must(QueryBuilders.termQuery("title", titleValue));
		boolQueryBuilder.should(QueryBuilders.matchQuery("subtitle", subtitleValue));
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder)
				.setFrom(rentSearch.getStart())// 开始数
				.setSize(rentSearch.getSize());// 查询几条

		logger.debug(requestBuilder.toString());

		List<SearchData> searchDatas = new ArrayList<>();
		SearchResponse response = requestBuilder.get();
		if (response.status() != RestStatus.OK) {
			logger.warn("Search status is no ok for " + requestBuilder);
			return new ServiceMultiResult<>(0, searchDatas);
		}
		// SearchHit为返回结果
		for (SearchHit hit : response.getHits()) {
			SearchData searchData = new SearchData();
			System.out.println(hit.getSource());
			StringBuffer hotBuffer = new StringBuffer();
			hotBuffer.append(hit.getSource().get("title")).append("之");
			hotBuffer.append(hit.getSource().get("subtitle"));
			searchData.setTitle(hotBuffer.toString());
			searchData.setInfo((String)hit.getSource().get("info"));
			searchData.setPublishDate(hit.getSource().get("publishdate").toString());
			searchData.setDocId(hit.getSource().get("docId").toString());
			searchData.setLove((int)hit.getSource().get("love"));
			searchData.setCount(hit.getSource().get("count").toString());

			searchDatas.add(searchData);
			logger.info("Search status is ok for" + searchDatas);
		}
		return new ServiceMultiResult<>(response.getHits().totalHits, searchDatas);
	}

/**
 * 当且仅当传入的数据查询为空时，进行重试
 * @param titleValue
 * @param subtitleValue
 * @param rentSearch
 * @return
 */
	public ServiceMultiResult<SearchData> queryDataForRetry(String value,RentSearch rentSearch) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.should(QueryBuilders.termQuery("item", value));
		boolQueryBuilder.should(QueryBuilders.matchQuery("title", value));
		boolQueryBuilder.should(QueryBuilders.matchQuery("subtitle", value));
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder)
				.setFrom(rentSearch.getStart())// 开始数
				.setSize(rentSearch.getSize());// 查询几条

		logger.debug(requestBuilder.toString());

		List<SearchData> searchDatas = new ArrayList<>();
		SearchResponse response = requestBuilder.get();
		if (response.status() != RestStatus.OK) {
			logger.warn("Search status is no ok for " + requestBuilder);
			return new ServiceMultiResult<>(0, searchDatas);
		}
		// SearchHit为返回结果
		for (SearchHit hit : response.getHits()) {
			SearchData searchData = new SearchData();
			System.out.println(hit.getSource());
			StringBuffer hotBuffer = new StringBuffer();
			hotBuffer.append(hit.getSource().get("title")).append("之");
			hotBuffer.append(hit.getSource().get("subtitle"));
			searchData.setTitle(hotBuffer.toString());
			searchData.setInfo((String)hit.getSource().get("info"));
			searchData.setPublishDate(hit.getSource().get("publishdate").toString());
			searchData.setDocId(hit.getSource().get("docId").toString());
			searchData.setLove((int)hit.getSource().get("love"));
			searchData.setCount(hit.getSource().get("count").toString());
			searchDatas.add(searchData);
			logger.info("Search status is ok for" + searchDatas);
		}
		return new ServiceMultiResult<>(response.getHits().totalHits, searchDatas);
	}
	
	
	
	
	
	
	
	/**
	 * 查询猜你想搜
	 * @param value
	 * @param rentSearch
	 * @return
	 */
	public ServiceMultiResult<SearchData> queryLoveData(String value,RentSearch rentSearch,SearchSort searchSort) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.should(QueryBuilders.matchQuery("item", value));
		boolQueryBuilder.should(QueryBuilders.matchQuery("title", value));
		boolQueryBuilder.should(QueryBuilders.matchQuery("subtitle", value));
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder)
				.setQuery(boolQueryBuilder).addSort(searchSort.getSortKey(rentSearch.getOrderBy()), // 默认排序字段
						SortOrder.fromString(rentSearch.getOrderDirection())// 排序方式
				)
				.setFrom(0)// 开始数
				.setSize(5);// 查询几条

		logger.debug(requestBuilder.toString());

		List<SearchData> searchDatas = new ArrayList<>();
		SearchResponse response = requestBuilder.get();
		if (response.status() != RestStatus.OK) {
			logger.warn("Search status is no ok for " + requestBuilder);
			return new ServiceMultiResult<>(0, searchDatas);
		}
		// SearchHit为返回结果
		for (SearchHit hit : response.getHits()) {
			SearchData searchData = new SearchData();
			System.out.println(hit.getSource());
			StringBuffer hotBuffer = new StringBuffer();
			hotBuffer.append(hit.getSource().get("title")).append("之");
			hotBuffer.append(hit.getSource().get("subtitle"));
			searchData.setTitle(hotBuffer.toString());
			searchData.setPublishDate(hit.getSource().get("publishdate").toString());
			searchData.setDocId(hit.getSource().get("docId").toString());
			searchData.setLove((int)hit.getSource().get("love"));

			searchDatas.add(searchData);
			logger.info("Search status is ok for" + searchDatas);
		}
		return new ServiceMultiResult<>(response.getHits().totalHits, searchDatas);
	} 
	
	
	
	
	public SearchData queryOneData(String docId) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		boolQueryBuilder.must(QueryBuilders.termQuery("docId", docId));
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder)
				.setFrom(0)// 开始数
				.setSize(1);// 查询几条

		logger.debug(requestBuilder.toString());

		SearchData searchData = new SearchData();
		SearchResponse response = requestBuilder.get();
		if (response.status() != RestStatus.OK) {
			logger.warn("Search status is no ok for " + requestBuilder);
			return new SearchData();
		}
		// SearchHit为返回结果
		for (SearchHit hit : response.getHits()) {
			searchData.setTitle((String)hit.getSource().get("title"));
			searchData.setSubtitle((String)hit.getSource().get("subtitle"));
			searchData.setInfo((String)hit.getSource().get("info"));
			searchData.setPublishDate(hit.getSource().get("publishdate").toString());
			searchData.setDocId(hit.getSource().get("docId").toString());
			searchData.setLove((int)hit.getSource().get("love"));
			searchData.setAuthor((String)hit.getSource().get("author"));
			searchData.setCount(hit.getSource().get("count").toString());
			searchData.setItem(hit.getSource().get("item").toString());
			
			logger.info("Search status is ok for" + searchData);
		}
		return searchData;
	} 
	

	private RangeQueryBuilder bulidQuery(RentSearch rentSearch) {
		if(rentSearch.getRangeDate()==null) {
			return null;
		}else {
			
		
		RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("publishdate");

		if(StringUtils.isNotEmpty(rentSearch.getRangeDate().getMax())) {
			rangeQuery.gte(rentSearch.getRangeDate().getMin());
			
		}
		
		if(StringUtils.isNotEmpty(rentSearch.getRangeDate().getMin())) {
			rangeQuery.lte(rentSearch.getRangeDate().getMax());

		}
		
		return rangeQuery;
		}
		

	}
	
	public ServiceMultiResult<SearchData> queryByAuthor(RentSearch rentSearch, SearchSort searchSort) {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		RangeQueryBuilder rangeQuery = this.bulidQuery(rentSearch);
		if(rangeQuery!=null) {
			boolQueryBuilder.filter(rangeQuery);
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		boolQueryBuilder.must(QueryBuilders.termQuery("author", user.getUserId().toString()));

		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.setQuery(boolQueryBuilder).addSort(searchSort.getSortKey(rentSearch.getOrderBy()), // 默认排序字段
						SortOrder.fromString(rentSearch.getOrderDirection())// 排序方式
				).setFrom(rentSearch.getStart())// 开始数
				.setSize(rentSearch.getSize());// 查询几条

		logger.debug(requestBuilder.toString());

		List<SearchData> searchDatas = new ArrayList<>();
		SearchResponse response = requestBuilder.get();
		if (response.status() != RestStatus.OK) {
			logger.warn("Search status is no ok for " + requestBuilder);
			return new ServiceMultiResult<>(0, searchDatas);
		}
		// SearchHit为返回结果
		for (SearchHit hit : response.getHits()) {
			SearchData searchData = DataFactory.getData(SearchData.class);
			searchData.setDocId(hit.getSource().get("docId").toString());
			searchData.setInfo((String)hit.getSource().get("info"));
			searchData.setPublishDate(hit.getSource().get("publishdate").toString());
			searchData.setAuthor((String)hit.getSource().get("author"));
			searchData.setTitle(hit.getSource().get("title").toString());
			searchData.setSubtitle(hit.getSource().get("subtitle").toString());
			searchData.setAuthor(user.getUsername());
			searchDatas.add(searchData);
		}
		return new ServiceMultiResult<>(response.getHits().totalHits, searchDatas);
	}
	
	
	
	
	/**
	 * 使用分词器对输入的信息进行分词,然后将分析结果放入indexTemplate.suggest
	 * 
	 * @param indexTemplate
	 * @return
	 */
	private boolean updateSuggest(IndexTemplate indexTemplate) {
		// 将需要分词的字段填入分词器
		AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(this.esClient, AnalyzeAction.INSTANCE,
				INDEX_NAME, indexTemplate.getAuthor(), indexTemplate.getItem(), indexTemplate.getSubtitle(),
				indexTemplate.getTitle(),indexTemplate.getItem());
		// 使用ik分词器的ik_smart进行分词.
		requestBuilder.setAnalyzer("ik_max_word");
		// 开始分词
		AnalyzeResponse response = requestBuilder.get();
		// 得到分词结果
		List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
		// 没有分词信息
		if (tokens == null) {
			logger.warn("Can not analyze token for field: {}", indexTemplate.getAuthor(), indexTemplate.getItem(),
					indexTemplate.getSubtitle(), indexTemplate.getTitle());
			return false;
		}
		// 遍历分词结果,依次设置值
		List<SearchSuggest> suggests = new ArrayList<>();
		for (AnalyzeResponse.AnalyzeToken token : tokens) {
			/// 排除数字类型
			if ("<NUM>".equals(token.getType())) {
				continue;
			}

			SearchSuggest suggest = new SearchSuggest();
			suggest.setInput(token.getTerm());
			suggests.add(suggest);
		}

		// 添加不分词的字段
		SearchSuggest suggest = new SearchSuggest();
		suggest.setInput(indexTemplate.getItem() + " " + indexTemplate.getSubtitle());
		

		suggests.add(suggest);

		// 完善indexTemplate
		indexTemplate.setSuggest(suggests);
		return true;
	}

	/**
	 * 自动补全输入的关键字
	 * 
	 * @param prefix
	 * @return
	 */

	public ServiceResult<List<String>> suggest(String prefix) {
		CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(5);// 搜索prefix,搜索5个

		SuggestBuilder suggestBuilder = new SuggestBuilder();

		// autocomplete 随意起名
		suggestBuilder.addSuggestion("autocomplete", suggestion);

		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
				.suggest(suggestBuilder);

		logger.debug(requestBuilder.toString());

		SearchResponse response = requestBuilder.get();
		// 得到suggest
		Suggest suggest = response.getSuggest();
		if (suggest == null) {
			return ServiceResult.of(new ArrayList<>());
		}
		// 得到结果
		Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

		int maxSuggest = 0;
		// set过滤,方便去重
		Set<String> suggestSet = new HashSet<>();
		// 对结果进行过滤
		for (Object term : result.getEntries()) {
			if (term instanceof CompletionSuggestion.Entry) {
				CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;

				if (item.getOptions().isEmpty()) {
					continue;
				}

				for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
					// 查看set集合中是否已经含有该字段
					String tip = option.getText().string();
					if (suggestSet.contains(tip)) {
						continue;
					}
					suggestSet.add(tip);
					maxSuggest++;
				}
			}
			// 如果set中元素大于5,跳出循环
			if (maxSuggest > 5) {
				break;
			}
		}
		List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[] {}));
		return ServiceResult.of(suggests);
	}

	/**
	 * 根据字段名聚合数据 查询结果为单条记录时使用
	 */

	public ServiceResult<Long> aggregateOne(String aggregationField) {
		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch("javaee").setTypes("knowledge")
				// .setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms("itemagg")// 起一个聚合名
						.field(aggregationField)// 对哪个字段进行聚合
				).setSize(0);// 开始数

		logger.debug(requestBuilder.toString());

		SearchResponse response = requestBuilder.get();
		if (response.status() == RestStatus.OK) {
			// 传入聚合名进行查询
			Terms terms = response.getAggregations().get("itemagg");
			// 判断是否查出了聚合数据
			if (terms.getBuckets() != null && !terms.getBuckets().isEmpty()) {
				// 注意:把聚合字段传入
				return ServiceResult.of(terms.getBucketByKey(aggregationField).getDocCount());
			}
		} else {
			logger.warn("Failed to Aggregate for:{} " + aggregationField);

		}
		return ServiceResult.of(0L);
	}

	/**
	 * 根据字段名聚合数据 查询结果为多条记录时使用
	 */

	public ServiceMultiResult<BucketResult> mapAggregate(String aggregationField) {
		TermsAggregationBuilder aggBuilder = AggregationBuilders.terms(searchAgg).field(aggregationField).size(5)
				.order(Terms.Order.count(false));

		SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)

				.addAggregation(aggBuilder);

		logger.info(requestBuilder.toString());

		SearchResponse response = requestBuilder.execute().actionGet();
		List<BucketResult> buckets = new ArrayList<>();
		if (response.status() != RestStatus.OK) {
			logger.warn("Aggregate status is not ok for " + requestBuilder);
			return new ServiceMultiResult<>(0, buckets);
		}

		Terms terms = response.getAggregations().get(searchAgg);
		for (Terms.Bucket bucket : terms.getBuckets()) {
			buckets.add(new BucketResult(bucket.getKeyAsString(), bucket.getDocCount()));
		}

		return new ServiceMultiResult<>(response.getHits().getTotalHits(), buckets);
	}

}
