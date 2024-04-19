package com.crossover.trial.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.crossover.trial.constants.Constants;

/**
 * A simple airport loader which reads a file from disk and sends entries to the webservice
 *
 * 
 * @author code test administrator
 */
public class AirportLoader {
	
    /** end point for read queries */

    /** end point to supply updates */
    private WebTarget collect;

    public AirportLoader() {
        Client client = ClientBuilder.newClient();
        collect = client.target(Constants.BASE_URI+Constants.FILE_SEPARATOR+Constants.COLLECT);
    }

    public void upload(InputStream airportDataStream) throws IOException{
    	BufferedReader reader =null;
        try {
			reader = new BufferedReader(new InputStreamReader(airportDataStream));
			String l = null;
			while ((l = reader.readLine()) != null) {
				String[] parts = l.split(",");
				if(parts.length !=11){
					continue;// current line is not in correct format
				}
				
				String path = Constants.FILE_SEPARATOR+Constants.AIRPORT+Constants.FILE_SEPARATOR + parts[4].replaceAll("\"", "")+ "/" + parts[6] + "/" + parts[7];
	            collect.path(path).request().post(Entity.entity("", MediaType.TEXT_HTML_TYPE));
			}
		} finally {
			if(reader != null){
				reader.close();
			}
		}
    }

	public static void main(String args[]) throws IOException {

		URL url = AirportLoader.class.getClassLoader().getResource("airports.dat");
		File airportDataFile = new File(url.getPath());
		if (!airportDataFile.exists() || airportDataFile.length() == 0) {
			System.err.println(airportDataFile + " is not a valid input");
			System.exit(1);
		}

		AirportLoader al = new AirportLoader();
		al.upload(new FileInputStream(airportDataFile));
		System.exit(0);
	}
}
