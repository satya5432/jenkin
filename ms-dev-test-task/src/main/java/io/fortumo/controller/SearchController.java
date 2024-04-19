package io.fortumo.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.fortumo.service.SearchService;
import io.fortumo.service.domain.Payment;

@RestController
public class SearchController {
	@Autowired
	private SearchService searchService;
	private final static Logger log = Logger.getLogger(SearchController.class);

	@RequestMapping("/")
	public ResponseEntity<Payment> search(@RequestParam(value = "merchant", required = false) String merchant,
			@RequestParam(value = "msisdn", required = false) String msisdn, 
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "operator", required = false) String operator) {
		
		log.info("merchant :"+merchant +" msisdn :"+msisdn+" country :"+country+" operator :"+ operator);
		//if all parameters are null then reject as it would do full table scan
		if(merchant == null && msisdn == null && country == null && operator == null){
			log.warn("all parameters are null : reject as it would do full table scan");
			return new ResponseEntity<Payment>(HttpStatus.BAD_REQUEST);
		}
		Payment payment = searchService.search(merchant, msisdn, country, operator);
		return new ResponseEntity<Payment>(payment,HttpStatus.OK);
	}
	@RequestMapping("/{merchant}/singleDay")
	public ResponseEntity<List<Payment>> searchSingleDayReport(@PathVariable(value = "merchant") String merchant,@RequestParam(value = "searchDate")@DateTimeFormat(pattern="yyyy-MM-dd") Date searchDate) {
		log.info(merchant +" "+ searchDate);
		
		if(merchant == null || merchant.trim().length() == 0){
			log.warn("merchant parameter is null");
			return new ResponseEntity<List<Payment>>(HttpStatus.BAD_REQUEST);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(searchDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date searchEndDate = cal.getTime();
		
		List<Payment> list = searchService.searchForDateRangeReport(merchant, searchDate, searchEndDate);
		
		 return new ResponseEntity<List<Payment>>(list,HttpStatus.OK);
	}
	
	@RequestMapping("/{merchant}/quarter")
	public ResponseEntity<List<Payment>> searchQuaterReport(@PathVariable(value = "merchant") String merchant, @RequestParam(value = "searchDate")@DateTimeFormat(pattern="yyyy-MM-dd") Date endDate) {
		log.info(merchant +" "+ endDate);
		if(merchant == null || merchant.trim().length() == 0){
			log.warn("merchant parameter is null");
			return new ResponseEntity<List<Payment>>(HttpStatus.BAD_REQUEST);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(endDate);
		cal.add(Calendar.MONTH, -3);
		Date startDate = cal.getTime();
		List<Payment> list = searchService.searchForDateRangeReport(merchant, startDate, endDate);
		return new ResponseEntity<List<Payment>>(list,HttpStatus.OK);
	}
}
