package io.fortumo.service;

import java.util.Date;
import java.util.List;

import io.fortumo.service.domain.Payment;

public interface SearchService {
	public Payment search(String merchant, String msisdn, String country, String operator);
	public List<Payment> searchForDateRangeReport(String merchant, Date startDate, Date endDate);
	
}
