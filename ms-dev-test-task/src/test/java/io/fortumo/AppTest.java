package io.fortumo;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import io.fortumo.service.domain.Payment;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AppTest {

	TestRestTemplate restTemplate = new TestRestTemplate();
	HttpHeaders headers = new HttpHeaders();
	String merchantUUID;
	Date createOn;

	@Test
	public void testRetrieveSingleRecord() {

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(createURLWithPort("/"))
		        // Add query parameter
		        .queryParam("country", "AR");
		Payment payment ;
		assertThat(payment = this.restTemplate.getForObject(builder.toUriString(),Payment.class)).isNotNull();
		merchantUUID = payment.getMerchantUUID();
		createOn = payment.getUpdatedAt();
	}
	
	@Test
	public void testRetrieveSingleRecordCountry() {

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(createURLWithPort("/"))
		        // Add query parameter
		        .queryParam("country", "EE");
		Payment payment ;
		assertThat(payment = this.restTemplate.getForObject(builder.toUriString(),Payment.class)).isNotNull();
		merchantUUID = payment.getMerchantUUID();
		createOn = payment.getUpdatedAt();
	}
	
	@Test
	public void testRetrieveSingleRecordCountry1() {

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(createURLWithPort("/"))
		        // Add query parameter
		        .queryParam("country", "AD");
		Payment payment ;
		assertThat(payment = this.restTemplate.getForObject(builder.toUriString(),Payment.class)).isNotNull();
		merchantUUID = payment.getMerchantUUID();
		createOn = payment.getUpdatedAt();
	}
	
	@Test
	public void testRetrieveSingleRecordCountry2() {

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(createURLWithPort("/"))
		        // Add query parameter
		        .queryParam("country", "AO");
		Payment payment ;
		assertThat(payment = this.restTemplate.getForObject(builder.toUriString(),Payment.class)).isNotNull();
		merchantUUID = payment.getMerchantUUID();
		createOn = payment.getUpdatedAt();
	}
	
	@Test
	public void testRetrieveSingleDayReport() {

		testRetrieveSingleRecord();
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("merchant", merchantUUID);

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(createURLWithPort("/{merchant}/singleDay"))
		        // Add query parameter
		        .queryParam("searchDate", new SimpleDateFormat("yyyy-MM-dd").format(createOn));
		System.out.println(builder.buildAndExpand(uriParams).toUriString());
		assertThat(this.restTemplate.getForObject(builder.buildAndExpand(uriParams).toUriString(),String.class)).isNotEmpty();
		
	}
	@Test
	public void testRetrieveSingleDayReport2() {

		testRetrieveSingleRecordCountry();
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("merchant", merchantUUID);

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(createURLWithPort("/{merchant}/singleDay"))
		        // Add query parameter
		        .queryParam("searchDate", new SimpleDateFormat("yyyy-MM-dd").format(createOn));
		System.out.println(builder.buildAndExpand(uriParams).toUriString());
		assertThat(this.restTemplate.getForObject(builder.buildAndExpand(uriParams).toUriString(),String.class)).isNotEmpty();
		
	}
	
	@Test
	public void testRetrieveQuarterReport() {

		testRetrieveSingleRecordCountry1();
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("merchant", merchantUUID);

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(createURLWithPort("/{merchant}/quarter"))
		        // Add query parameter
		        .queryParam("searchDate", new SimpleDateFormat("yyyy-MM-dd").format(createOn));
		System.out.println(builder.buildAndExpand(uriParams).toUriString());
		assertThat(this.restTemplate.getForObject(builder.buildAndExpand(uriParams).toUriString(),String.class)).isNotEmpty();
		
	}
	
	@Test
	public void testRetrieveQuarterReport2() {

		testRetrieveSingleRecordCountry2();
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("merchant", merchantUUID);

		// Query parameters
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(createURLWithPort("/{merchant}/quarter"))
		        // Add query parameter
		        .queryParam("searchDate", new SimpleDateFormat("yyyy-MM-dd").format(createOn));
		System.out.println(builder.buildAndExpand(uriParams).toUriString());
		assertThat(this.restTemplate.getForObject(builder.buildAndExpand(uriParams).toUriString(),String.class)).isNotEmpty();
		
	}
	

	private String createURLWithPort(String uri) {
		return "http://localhost:12000" + uri;
	}

}
