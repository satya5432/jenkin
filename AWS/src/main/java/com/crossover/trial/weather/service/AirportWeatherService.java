/**
 * 
 */
package com.crossover.trial.weather.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.AtmosphericInformation;
import com.crossover.trial.weather.domain.DataPoint;
import com.crossover.trial.weather.domain.DataPointType;
import com.crossover.trial.weather.exception.WeatherException;

/**
 * @author Ravi Yadav
 */
public class AirportWeatherService {
//as there in no DI , make it Singleton
	private static AirportWeatherService INSTANCE = new AirportWeatherService();
	public final static Logger LOGGER = Logger.getLogger(AirportWeatherService.class.getName());

	//private constructor
	private  AirportWeatherService() {
		
	}
	public static AirportWeatherService getInstance() {
		return INSTANCE;
	}
	

    /** earth radius in KM */
    public static final double R = 6372.8;
    /**
     * The minimum allowed latitude
     */
    public static double MIN_LATITUDE = -90.0;

    /**
     * The maximum allowed latitude
     */
    public static double MAX_LATITUDE = 90.0;

    /**
     * The minimum allowed longitude
     */
    public static double MIN_LONGITUDE = -180.0;

    /**
     * The maximum allowed longitude
     */
    public static double MAX_LONGITUDE = 180.0;

    /** all known airports */// using concurrentHashMap to avoid concurrency issues
    private Map<AirportData, AtmosphericInformation> airportData = new ConcurrentHashMap<>();

    /**
     * Internal performance counter to better understand most requested information, this map can be improved but
     * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
     * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
     * performance metrics {@link #ping()}
     */
    private Map<AirportData, Integer> requestFrequency = new ConcurrentHashMap<AirportData, Integer>();

    private Map<Double, Integer> radiusFreq = new ConcurrentHashMap<Double, Integer>();
    
    public Set<AirportData> getAirports() {
        return airportData.keySet();
    }

    public Map<AirportData, AtmosphericInformation> getAirportData() {
        return airportData;
    }

    public Map<AirportData, Integer> getRequestFrequency() {
        return requestFrequency;
    }

    public Map<Double, Integer> getRadiusFreq() {
        return radiusFreq;
    }
    
    /**
     * Given an iataCode find the airport data
     *
     * @param iataCode as a string
     * @return airport data or null if not found
     */
	public AirportData findAirportData(String iataCode) {
		return getAirports().stream().filter(ap -> ap.getIata().equals(iataCode)).findFirst().orElse(null);
	}
	
    /**
     * Records information about how often requests are made
     *
     * @param iata an iata code
     * @param radius query radius
     */
    public void updateRequestFrequency(String iata, Double radius) {
        AirportData airportData = findAirportData(iata);
        requestFrequency.put(airportData, requestFrequency.getOrDefault(airportData, 0) + 1);
        radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0));
    }
    


    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    public double calculateDistance(AirportData ad1, AirportData ad2) {
        double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
        double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    /**
     * Update the airports weather data with the collected data.
     *
     * @param iataCode the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dp a datapoint object holding pointType data
     *
     * @throws WeatherException if the update can not be completed
     */
    public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {
        AirportData airport = findAirportData(iataCode);
        if(airport == null){
        	throw new WeatherException();
        }
        AtmosphericInformation ai = getAirportData().get(airport);
        updateAtmosphericInformation(ai, pointType, dp);
    }
    

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp the actual data point
     */
    public void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) throws WeatherException {
         DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());

        ai.updateDataPoint(dptype, dp);

    }
    
    /**
     * Add a new known airport to our list.
     *
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     * @return the added airport
     * @throws WeatherException 
     */
    public void addAirport(String iataCode, double latitude, double longitude) throws WeatherException {
    	
    	// check if airport exists
    	if(findAirportData(iataCode) !=null){
    		LOGGER.log(Level.INFO, "Airport already exists.");
    		return;
    	}
    	
    	
        if (isValidLatitude(latitude) && isValidLongitude(longitude)) {
			AirportData ad = new AirportData();
			ad.setIata(iataCode);
			ad.setLatitude(latitude);
			ad.setLatitude(longitude);
			AtmosphericInformation ai = new AtmosphericInformation();
			getAirportData().put(ad, ai);
		}else{
			throw new WeatherException();
		}
        
    }
	/**
	 * @param iata
	 * @return boolean
	 */
	public boolean deleteAirport(String iata) {

		// check if airport exists
		AirportData ad = findAirportData(iata);
		if(ad == null){
			return false;
		}
		return getAirportData().remove(ad) !=null ;
	}
	
    /**
     * A method to validate a latitude value
     *
     * @param latitude
     *            the latitude to check is valid
     *
     * @return true if, and only if, the latitude is within the MIN and MAX
     *         latitude
     */
    public boolean isValidLatitude(double latitude) {
        return (latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE);
    }

    /**
     * A method to validate a longitude value
     *
     * @param longitude
     *            the longitude to check is valid
     *
     * @return true if, and only if, the longitude is between the MIN and MAX
     *         longitude
     */
    public boolean isValidLongitude(double longitude) {
        return (longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE);
    }
}
