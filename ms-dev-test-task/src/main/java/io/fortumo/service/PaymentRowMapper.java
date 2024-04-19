package io.fortumo.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import io.fortumo.service.domain.Country;
import io.fortumo.service.domain.Merchant;
import io.fortumo.service.domain.Operator;
import io.fortumo.service.domain.Payment;

public class PaymentRowMapper implements RowMapper<Payment>{

		  @Override
		    public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			  Payment payment = new Payment();
			  payment.setId(rs.getInt("id"));
			  payment.setCreatedAt(rs.getDate("created_at"));
			  payment.setUpdatedAt(rs.getDate("updated_at"));
			  payment.setMerchantUUID(rs.getString("merchant_uuid"));
			  payment.setOperatorCode(rs.getString("operator_code"));
			  payment.setCountryCode(rs.getString("country_code"));
			  payment.setMsisdn(rs.getString("msisdn"));
			  payment.setAmount(rs.getLong("amount"));
			  
			  if(rs.getMetaData().getColumnCount() > 8){
					  Merchant merchant = new Merchant();
					  merchant.setId(rs.getInt("m_id"));
					  merchant.setUuid(rs.getString("m_uuid"));
					  merchant.setLogin(rs.getString("m_login"));
					  payment.setMerchant(merchant);
					  Operator operator = new Operator();
					  operator.setId(rs.getInt("o_id"));
					  operator.setCode(rs.getString("o_code"));
					  operator.setName(rs.getString("o_name"));
					  payment.setOperator(operator);
					  Country country= new Country();
					  country.setId(rs.getInt("c_id"));
					  country.setCode(rs.getString("c_code"));
					  country.setName(rs.getString("c_name"));
					  payment.setCountry(country);
  
			  }
			  			  
		        return payment;
		    }

		}