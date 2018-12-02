package com.api.recognisearch.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Component
public class RecogniPredictionService {

	public Map<? extends String, ? extends String> getPredictionResults(String absolutePath) {

		HashMap<String, String> resp = new HashMap<>();
		try {
			System.out.println("image path >>>>." + absolutePath);
			Client client = Client.create();

			WebResource webResource = client.resource("http://127.0.0.1:5000/predict?filepath=" + absolutePath);

			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			if (response.getStatus() == 200) {
				resp = response.getEntity(resp.getClass());

				if (resp.get("confidence") != null && !"".equals(resp.get("confidence"))) {
					if (Float.parseFloat(resp.get("confidence")) < 0.5f) {

						resp.put("resp_code", "NR");// face not really recognised as confidence is less than 50%
					} else {
						
						resp.put("resp_code", "FR");
					}

				}
			} else {

				resp.put("resp_code", "NF");// either no face found or another exception at python API end
			}

		} catch (Exception e) {

			resp.put("resp_code", "EX");// some exception at java service

			e.printStackTrace();
		}

		return resp;
	}

}
