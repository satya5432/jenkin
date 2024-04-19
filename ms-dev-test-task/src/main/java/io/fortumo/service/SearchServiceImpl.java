package io.fortumo.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.fortumo.service.domain.Payment;

@Service
public class SearchServiceImpl implements SearchService {
	
	//cache doesn't have eviction built in.
	private ConcurrentMap<String, FutureTask<List<Payment>>> dataCacheQurter = new ConcurrentHashMap<>();
	private ConcurrentMap<String, AtomicInteger> dataCacheQurterCounter = new ConcurrentHashMap<>();
	private ConcurrentMap<String, FutureTask<Payment>> dataCacheSingle = new ConcurrentHashMap<>();
	private ConcurrentMap<String, AtomicInteger> dataCacheSingleConter = new ConcurrentHashMap<>();
	private String dfStr = "ddMMyyyy";
	private String qdfStr = "yyyy-MM-dd";
	private static final String MERCHANT ="merchant_uuid";
	private static final String MSISDN ="msisdn";
	private static final String COUNTRY ="country_code";
	private static final String OPERATOR ="operator_code";
	private final static Logger log = Logger.getLogger(SearchServiceImpl.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional(readOnly = true)
	@Override
	public Payment search(String merchant, String msisdn, String country, String operator) {
		if(log.isDebugEnabled()){
			log.debug("Starting search method.");
		}
		final String key = merchant+"_"+msisdn+"_"+country+"_"+operator;
		
		long startTime = System.currentTimeMillis();
		
		AtomicInteger cn;
		Payment returnValue = null;
		
		AtomicInteger counter = dataCacheSingleConter.putIfAbsent(key, cn= new AtomicInteger(0));
		if(counter == null){
			counter = cn;
			counter.incrementAndGet();
		}
		
		
		Callable<Payment> call = new Callable<Payment>() {
		      public Payment call() {
		    	  StringBuilder sql = new StringBuilder("SELECT * FROM payments");

		  		if (merchant != null || msisdn != null || country != null || operator != null) {
		  			sql.append(" Where ");
		  		}
		  		List<String> props = new ArrayList<>();
		  		boolean first = true;

		  		appendWhere(merchant,MERCHANT, sql, props, first);
		  		appendWhere(msisdn,MSISDN, sql, props, first);
		  		appendWhere(country,COUNTRY, sql, props, first);
		  		appendWhere(operator,OPERATOR, sql, props, first);

		  		sql.append(" LIMIT 1");

		  		return jdbcTemplate.queryForObject(sql.toString(), props.toArray(), new PaymentRowMapper());
		      }
		  };
		
		FutureTask<Payment> ft; ;
		  Future<Payment> f = dataCacheSingle.putIfAbsent(key, ft= new FutureTask<Payment>(call)); //atomic
		  if (f == null) { //this means that the cache had no mapping for the key
		      f = ft;
		      ft.run();
		  }
		  try {
			  returnValue= f.get();
		} catch (InterruptedException | ExecutionException e) {
			//return null;
		} //wait on the result being available if it is being calculated in another thread
		int count = counter.decrementAndGet();
		if(count <=0){
			dataCacheSingle.remove(key);
		}
		
		if(log.isDebugEnabled()){
			log.debug("End search method.");
		}
		log.info("Search : time taken: "+ (System.currentTimeMillis() -startTime)+" ms.");
		return returnValue;

	}

	private void appendWhere(String param,String column, StringBuilder sql, List<String> props, boolean first) {
		if (param != null) {
			if (!first) {
				sql.append(" AND ");
				first = false;
			}
			sql.append(column+" = ?");
			props.add(param);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<Payment> searchForDateRangeReport(String merchant, Date startDate, Date endDate) {
		
		DateFormat df = new SimpleDateFormat(dfStr);
		DateFormat qdf = new SimpleDateFormat(qdfStr);
		final String key = merchant+"_"+df.format(startDate)+"_"+ df.format(endDate);
		String startDateStr = qdf.format(startDate);
		String endDateStr = qdf.format(endDate);
		long startTime = System.currentTimeMillis();
		AtomicInteger cn;
		List<Payment> returnValue = null;
		
		AtomicInteger counter = dataCacheQurterCounter.putIfAbsent(key, cn= new AtomicInteger(0));
		if(counter == null){
			counter = cn;
			counter.incrementAndGet();
		}
		Callable<List<Payment>> call = new Callable<List<Payment>>() {
		      public List<Payment> call() {
		          return jdbcTemplate.query("select p.*, m.id m_id, m.uuid m_uuid, m.login m_login, o.id o_id, o.code o_code,o.name o_name, c.id c_id, c.code c_code, "+
							"c.name c_name from payments p inner join merchants m on p.merchant_uuid= m.uuid "+ 
							"inner join operators o on p.operator_code = o.code inner join countries c on p.country_code=c.code where merchant_uuid = ?"+
			" and created_at::date >= date '"+startDateStr+"' and created_at::date < date '"+endDateStr+"'", new Object[]{merchant},
		                new PaymentRowMapper());
		      }
		  };
		
		FutureTask<List<Payment>> ft; ;
		  Future<List<Payment>> f = dataCacheQurter.putIfAbsent(key, ft= new FutureTask<List<Payment>>(call)); //atomic
		  if (f == null) { //this means that the cache had no mapping for the key
		      f = ft;
		      ft.run();
		  }
		  try {
			  returnValue = f.get();
		} catch (InterruptedException | ExecutionException e) {
			//return null;
		} //wait on the result being available if it is being calculated in another thread
		  int count = counter.decrementAndGet();
			if(count <=0){
				dataCacheQurter.remove(key);
			}
			
			if(log.isDebugEnabled()){
				log.debug("End search method.");
			}
			log.info("searchForDateRangeReport : startDate: "+startDateStr+" endDate :"+endDateStr+"time taken: "+ (System.currentTimeMillis() -startTime)+" ms.");
		return returnValue;
	}

}
