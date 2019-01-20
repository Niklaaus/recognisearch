package com.api.recognisearch.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.api.recognisearch.service.RecogniPredictionService;

@RestController
public class SearchController {

	@Autowired
	private RecogniPredictionService searchService;
	
	@CrossOrigin(origins = "https://recognizr.xyz")
	@PostMapping("/search")
	public ResponseEntity<?> compareWithPresentCollection(@RequestBody String imageBase64Encoded) {

		String directory = "";
		File uploadedImage = null;
		BufferedOutputStream uploadStream = null;
		HashMap<String, String> resp = new HashMap<>();
		String filename = "uploaded";
		if (!imageBase64Encoded.isEmpty()) {
			try {
				int startOfBase64Data = imageBase64Encoded.indexOf(",") + 1;
				imageBase64Encoded = imageBase64Encoded.substring(startOfBase64Data, imageBase64Encoded.length());
				byte[] imageBytes = Base64.getDecoder().decode(imageBase64Encoded);
				///////////////////////////////////////////////////////////////
				// Creating the directory to store file/data/image ////////////
				directory = System.getProperty("catalina.home");
				File fileSaveDir = new File(directory);
				// Creates the save directory if it does not exists
				if (!fileSaveDir.exists()) {
					fileSaveDir.mkdirs();
				}
				filename = filename + "_" + (new Date()).toString().replaceAll("[^a-zA-Z0-9]", "_") + ".png";
				uploadedImage = new File(fileSaveDir.getAbsolutePath() + File.separator + filename);
				uploadStream = new BufferedOutputStream(new FileOutputStream(uploadedImage));
				uploadStream.write(imageBytes);
				
				resp.putAll(searchService.getPredictionResults(uploadedImage.getAbsolutePath()));

				return new ResponseEntity(resp, HttpStatus.OK);
			} catch (Exception e) {
				resp.put("message", "Image was corrupt");
				return new ResponseEntity(resp, HttpStatus.BAD_REQUEST);
			} finally {
				if (uploadStream != null) {
					try {
						uploadStream.close();
					} catch (IOException e) {
						System.out.println("COULD NOT CLOSE BUFFERED STREAM");
						e.printStackTrace();
					}
				}
			}
		}

		else {
			resp.put("message", "Image was corrupt");
			return new ResponseEntity(resp, HttpStatus.BAD_REQUEST);
		}
	}
}
