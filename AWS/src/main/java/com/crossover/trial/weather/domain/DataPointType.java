package com.crossover.trial.weather.domain;

import com.crossover.trial.weather.exception.WeatherException;

/**
 * The various types of data points we can collect.
 *
 * @author code test administrator
 */
public enum DataPointType {
    WIND,
    TEMPERATURE,
    HUMIDTY,
    PRESSURE,
    CLOUDCOVER,
    PRECIPITATION;
    
    public void validate(DataPoint dataPoint) throws WeatherException {
        boolean valid = true;

        switch (this) {
        case WIND:
            valid = dataPoint.getMean() >= 0;    
            break;
        case TEMPERATURE:
            valid = dataPoint.getMean() >= -50 && dataPoint.getMean() < 100;
            break;
        case HUMIDTY:
            valid = dataPoint.getMean() >= 0 && dataPoint.getMean() < 100;
            break;
        case PRESSURE:
            valid = dataPoint.getMean() >= 650 && dataPoint.getMean() < 800;
            break;
        case CLOUDCOVER:
            valid = dataPoint.getMean() >= 0 && dataPoint.getMean() < 100;
            break;
        case PRECIPITATION:
            valid = dataPoint.getMean() >= 0 && dataPoint.getMean() < 100;
            break;
        default:
        	break;
        }
        if(!valid){
        	throw new WeatherException("Invalid atmospheric data parameter");
        }
    }
}
