package org.renci.xorest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class XORestController {
	private static final String XOREST_CACHE_TIMEOUT_PROPERTY = "xorest.cache.timeout";
	private XoDbAccess xda;
	private Map<String, Integer> cache = new HashMap<String, Integer>();
	private Map<String, Date> cacheTs = new HashMap<String, Date>();
	private static int DEFAULT_CACHE_TIME_MS = 60000;
	
	Log log = org.apache.commons.logging.LogFactory.getLog(XORestController.class);
	
	@Autowired 
	private ApplicationContext ctx;

	@Autowired
	public XORestController(XoDbAccess xda) {
		this.xda = xda;
	}
	
	@ResponseStatus(value=HttpStatus.NOT_IMPLEMENTED, reason="Counting of this object is not implemented") 
	public class ObjectNotFound extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ObjectNotFound(String s) {
			super(s);
		}
	}
	
	protected synchronized Integer getCachedValue(String ob) {
		String query = ob.toLowerCase();
		Date ts = cacheTs.get(query);
		
		// consult cache, maybe
		if (ts != null) {
			Calendar cl = Calendar.getInstance();
			Calendar clTs = Calendar.getInstance();
			clTs.setTime(ts);
			
			String toString = ctx.getEnvironment().getProperty(XOREST_CACHE_TIMEOUT_PROPERTY);

			int toInt = DEFAULT_CACHE_TIME_MS;
			if (toString != null) {
				try {
					toInt = Integer.parseInt(toString);
				} catch (Exception e) {
					;
				}
			}
			
			clTs.add(Calendar.MILLISECOND, toInt);
			
			if (clTs.after(cl) && cache.containsKey(query)) {
				log.info("Using cached value for " + query + " with timeout " + toInt);
				// get cached value
				return cache.get(query);
			} else
				log.info("Cache value for " + query + " too old");
		}
		
		log.info("Attempting query for " + query);
		
		// otherwise set cache value
		int val = xda.getIntCount(query);

		if (val == Integer.MIN_VALUE) {
			log.error("Query for " + query + " not defined");
			throw new ObjectNotFound("Query for " + query + " is not defined");
		}
		
		log.info("Updating cache value for " + query + " with value " + val);
		cache.put(query, val);
		cacheTs.put(query, new Date());
		return val;
	}
	
	/**
	 * Return a count of specific object type: slice, site, user
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/count/{obj}", method=RequestMethod.GET)
	@ResponseBody
	public Integer count(@PathVariable("obj") String obj) {

		if (obj.length() > 255) {
			log.error("Query string too long, rejecting ");
			throw new ObjectNotFound("Query string too long");
		}
		log.info("Invoked count " + obj);
		return getCachedValue(obj);
	}
	
}
