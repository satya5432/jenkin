package com.crossover.trial.weather.rest;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.crossover.trial.weather.domain.AirportData;
import com.crossover.trial.weather.domain.DataPoint;
import com.crossover.trial.weather.exception.WeatherException;
import com.crossover.trial.weather.service.AirportWeatherService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {
    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    /** shared gson json to object factory */
    public final static Gson gson = new Gson();
    private AirportWeatherService airportWeatherService= AirportWeatherService.getInstance();

    @Override
    @GET
    @Path("/ping")
    public Response ping() {
        return Response.status(Response.Status.OK).entity("1").build();
    }

    @Override
    @POST
    @Path("/weather/{iata}/{pointType}")
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
        try {
        	airportWeatherService.addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
        } catch (JsonSyntaxException | WeatherException e) {
        	LOGGER.log(Level.WARNING, e.getMessage(), e);
        	return Response.status(Status.REQUESTED_RANGE_NOT_SATISFIABLE).build();
        } catch (Exception e) {
        	LOGGER.log(Level.SEVERE, e.getMessage(), e);
        	return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }


    @Override
    @GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirports() {
        Set<String> retval = new HashSet<>();
        for (AirportData ad : airportWeatherService.getAirports()) {
            retval.add(ad.getIata());
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }


    @Override
    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirport(@PathParam("iata") String iata) {
        AirportData ad = airportWeatherService.findAirportData(iata);
        return Response.status(ad != null ? Response.Status.OK : Response.Status.NOT_FOUND).entity(ad).build();
    }


    @Override
    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
        try {
			airportWeatherService.addAirport(iata, Double.valueOf(latString), Double.valueOf(longString));
			return Response.status(Response.Status.CREATED).build();
		} catch (NumberFormatException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return Response.status(Status.BAD_REQUEST).build();
		} catch (WeatherException e) {
        	LOGGER.log(Level.WARNING, e.getMessage(), e);
        	return Response.status(Status.REQUESTED_RANGE_NOT_SATISFIABLE).build();
        }
    }


    @Override
    @DELETE
    @Path("/airport/{iata}")
    public Response deleteAirport(@PathParam("iata") String iata) {
    	boolean deleted = airportWeatherService.deleteAirport(iata);
		return Response.status(deleted ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND).build();
    }

}
