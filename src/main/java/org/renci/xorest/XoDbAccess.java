package org.renci.xorest;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class XoDbAccess {
	private JdbcTemplate jdbcTemplate;
	
	private Map<String, String> queries;
	
	@Autowired
	private Config cf;
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        
        queries = new HashMap<String, String>();
        for (Map<String, String> m: cf.getQueries()) {
        	queries.put(m.get("name"), m.get("query"));
        }
    }
    
    /**
     * Invoke a query from config or -1
     * @param name
     * @return
     */
    public int getIntCount(String name) {
    	try {
    		if (queries.containsKey(name)) {
    			int rowCount = this.jdbcTemplate.queryForObject(queries.get(name), Integer.class);
    			return rowCount;
    		} 
    	} catch (Exception e) {
    		return Integer.MIN_VALUE;
    	}
    	return Integer.MIN_VALUE;
    }
    
    public boolean hasQuery(String name) {
    	return queries.containsKey(name);
    }
}
