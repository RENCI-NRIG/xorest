package org.renci.xorest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="xorest")
public class Config {
	private List<Map<String, String>> queries = new ArrayList<Map<String, String>>();
	
	public List<Map<String, String>> getQueries() {
		return queries;
	}
}
