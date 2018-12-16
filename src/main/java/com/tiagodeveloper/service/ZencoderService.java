package com.tiagodeveloper.service;

import org.json.simple.JSONObject;

public interface ZencoderService {
	
	
	public JSONObject encodingJob(String videoUri); 
	public JSONObject progressJobOutput(Long id); 

}
