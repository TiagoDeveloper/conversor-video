package com.tiagodeveloper.service.impl;

import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tiagodeveloper.service.ZencoderService;

@Service("zencoderService")
@PropertySource(value = { "zencoder.properties", "credentials.properties" }, ignoreResourceNotFound = true)
public class ZencoderServiceImpl implements ZencoderService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HttpHeaders headers;

	@Value("${Zencoder.api.key}")
	private String zencoderApiKey;

	@Value("${Zencoder.api.jobs.body.create.json}")
	private String zencoderApiJsonBodyCreate;

	@Value("${Zencoder.api.jobs.uri}")
	private String zencoderApiJobsUri;

	@Value("${Zencoder.api.outputs.uri}")
	private String zencoderApiOutputUri;

	@Override
	@SuppressWarnings("unchecked")
	public JSONObject encodingJob(String videoUri) {

		JSONParser parse = new JSONParser();
		JSONObject object = null;
		try {

			object = (JSONObject) parse.parse(zencoderApiJsonBodyCreate);
			
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		object.replace("input", videoUri);

		HttpEntity<String> entity = new HttpEntity<String>(object.toJSONString(), getHeaders());
		String response = this.restTemplate.exchange(zencoderApiJobsUri, HttpMethod.POST, entity, String.class).getBody();

		try {
			object = (JSONObject) parse.parse(response);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return object;
	}

	@Override
	public JSONObject progressJobOutput(Long id) {
		
		HttpEntity<String> entity = new HttpEntity<String>(getHeaders());
		
		String response = this.restTemplate.exchange(zencoderApiOutputUri+id, HttpMethod.GET, entity, String.class).getBody();
		JSONParser parse = new JSONParser();
		JSONObject object = null;
		
		try {
			object = (JSONObject) parse.parse(response);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	
	
	private HttpHeaders getHeaders(){
		
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("Zencoder-Api-Key", zencoderApiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		return headers;
	}

}
