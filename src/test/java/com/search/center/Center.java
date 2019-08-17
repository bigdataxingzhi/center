package com.search.center;

import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class Center {
	
	private static final org.slf4j.Logger log = LoggerFactory.getLogger(Center.class);

	@Autowired
	TransportClient esClient;
	
	@Test
	public void testEsClinet() {
		
		List<DiscoveryNode> connectedNodes = esClient.connectedNodes();
		log.info(esClient.toString());
	}
}
