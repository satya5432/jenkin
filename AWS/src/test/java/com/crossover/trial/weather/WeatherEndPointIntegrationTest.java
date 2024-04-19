/**
 * 
 */
package com.crossover.trial.weather;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.crossover.trial.helper.AirportLoader;
import com.crossover.trial.weather.domain.DataPoint;

/**
 * @author Ravi Yadav
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WeatherEndPointIntegrationTest {
    private static final String BASE_URI = "http://localhost:9090";

    /** end point for read queries */
    private WebTarget query;

    /** end point to supply updates */
    private WebTarget collect;
    private static Thread server;
    
    @BeforeClass
    public static void config(){
    	server=new Thread(new Runnable() {
			
			@Override
			public void run() {
				WeatherServer.main(new String[0]);
				
			}
		});
        server.start();
    }
    @Before
    public void setUp() {
    	Client client = ClientBuilder.newClient();
        query = client.target(BASE_URI + "/query");
        collect = client.target(BASE_URI + "/collect");
	}
    
    @Test
    public void shouldDeleteAllExistingAirportsTest() {
    	String[] res;
		clearAirports();

        res = collect.path("airports").request().accept(MediaType.APPLICATION_JSON).get(String[].class);
        assertEquals(0, res.length);
	}

	
    @Test
    public void shoulsLoadDataFromFile() throws IOException {
    	clearAirports();
    	loadFronFile();
		

        String[] res = collect.path("airports").request().accept(MediaType.APPLICATION_JSON)
                .get(String[].class);
        assertEquals(10, res.length);
	}
    
    @Test
	public void shouldAddDataPoint() throws Exception {
    	clearAirports();
    	loadFronFile();
		WebTarget path = collect.path("/weather/BOS/" + "wind");
		DataPoint dp = new DataPoint.Builder().withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
		Response response = path.request().post(Entity.entity(dp, "application/json"));
		assertEquals(204, response.getStatus());
	}
    
    @Test
    public void shouldGetExistingAirport() throws Exception{
    	loadFronFile();
        Response response = collect.path("airport").path("EWR").request().get();
        assertEquals(200, response.getStatus());
        clearAirports();
    
    }
    
    @Test
    public void shouldNotGetNonExistingAirport() throws Exception{
        loadFronFile();
    	Response response = collect.path("airport").path("Tst").request().get();
        assertEquals(404, response.getStatus());
        clearAirports();
    }
    

    public static void tearDown() {
    	System.exit(0);;
	}
    
    /**
	 * 
	 */
	private void clearAirports() {
		String[] res = collect.path("airports").request().accept(MediaType.APPLICATION_JSON)
                .get(String[].class);
        for (String iata : res) {
            collect.path("airport").path(iata).request().delete();
        }
	}
    /**
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadFronFile() throws IOException, FileNotFoundException {
		URL url = AirportLoader.class.getClassLoader().getResource("airports.dat");
		File airportDataFile = new File(url.getPath());
		if (!airportDataFile.exists() || airportDataFile.length() == 0) {
			System.err.println(airportDataFile + " is not a valid input");
			System.exit(1);
		}

		AirportLoader al = new AirportLoader();
		al.upload(new FileInputStream(airportDataFile));
	}
}
