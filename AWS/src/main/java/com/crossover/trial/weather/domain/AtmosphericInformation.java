package com.crossover.trial.weather.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.crossover.trial.weather.exception.WeatherException;

/**
 * encapsulates sensor information for a particular location
 */
public class AtmosphericInformation {
	
	private Map<DataPointType, DataPoint> dataPointMap = new ConcurrentHashMap<DataPointType, DataPoint>();

    
    /** the last time this data was updated, in milliseconds since UTC epoch */
    private long lastUpdateTime;

    public AtmosphericInformation() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void updateDataPoint(DataPointType dataPointType, DataPoint dataPoint) throws WeatherException {
		dataPointType.validate(dataPoint);
		this.lastUpdateTime = System.currentTimeMillis();
		dataPointMap.put(dataPointType, dataPoint);
	}
    
    public DataPoint getDataPoint(DataPointType type) {
    	
        return dataPointMap.get(type);
    }
    
    public long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

}
