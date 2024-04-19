package com.crossover.trial.weather.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.AtmosphericInformation;
import com.crossover.trial.weather.domain.DataPointType;
import com.crossover.trial.weather.service.AirportWeatherService;
import com.google.gson.Gson;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

    /** shared gson json to object factory */
    public static final Gson gson = new Gson();
    private AirportWeatherService airportWeatherService= AirportWeatherService.getInstance();


    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @Override
    public String ping() {
        Map<String, Object> retval = new HashMap<>();

        int datasize = 0;
        for (AtmosphericInformation ai : airportWeatherService.getAirportData().values()) {
            // we only count recent readings
            if (ai.getDataPoint(DataPointType.CLOUDCOVER) != null
                || ai.getDataPoint(DataPointType.HUMIDTY) != null
                || ai.getDataPoint(DataPointType.PRESSURE) != null
                || ai.getDataPoint(DataPointType.PRECIPITATION) != null
                || ai.getDataPoint(DataPointType.TEMPERATURE) != null
                || ai.getDataPoint(DataPointType.WIND) != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    datasize++;
                }
            }
        }
        retval.put("datasize", datasize);

        Map<String, Double> freq = new HashMap<>();
        // fraction of queries
        for (AirportData data : airportWeatherService.getAirports()) {
            double frac = (double)airportWeatherService.getRequestFrequency().getOrDefault(data, 0) / airportWeatherService.getRequestFrequency().size();
            freq.put(data.getIata(), frac);
        }
        retval.put("iata_freq", freq);

        int m = airportWeatherService.getRadiusFreq().keySet().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];
        for (Map.Entry<Double, Integer> e : airportWeatherService.getRadiusFreq().entrySet()) {
            int i = e.getKey().intValue() % 10;
            hist[i] += e.getValue();
        }
        retval.put("radius_freq", hist);

        return gson.toJson(retval);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iata the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @Override
    public Response weather(String iata, String radiusString) {
    	
    	AirportData airport = airportWeatherService.findAirportData(iata);
    	
    	if (airport == null) {
            return Response.status(404).build();
        }
        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
        airportWeatherService.updateRequestFrequency(iata, radius);

        List<AtmosphericInformation> retval = new ArrayList<>();
        if (radius == 0) {
            AtmosphericInformation atmosphericInformation = airportWeatherService.getAirportData().get(airport);
			retval.add(atmosphericInformation == null? new AtmosphericInformation() : atmosphericInformation);
        } else {
            for (AirportData airportData :  airportWeatherService.getAirports()){
                if (airportWeatherService.calculateDistance(airport, airportData) <= radius){
                    AtmosphericInformation ai = airportWeatherService.getAirportData().get(airportData);
                    if (ai.getDataPoint(DataPointType.CLOUDCOVER) != null || ai.getDataPoint(DataPointType.HUMIDTY) != null || ai.getDataPoint(DataPointType.PRECIPITATION) != null
                       || ai.getDataPoint(DataPointType.PRESSURE) != null || ai.getDataPoint(DataPointType.TEMPERATURE) != null || ai.getDataPoint(DataPointType.WIND) != null){
                        retval.add(ai);
                    }
                }
            }
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }

}
