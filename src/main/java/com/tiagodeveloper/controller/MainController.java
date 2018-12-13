package com.tiagodeveloper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Controller
@RequestMapping(value={"","/","/main"})
public class MainController {
	
	private static AWSCredentials credentials;
	
	static{
		credentials = new BasicAWSCredentials(
				  "<AWS accesskey>", 
				  "<AWS secretkey>"
				);
	}
	
	
	@GetMapping(value={"","/","/home"})
	public String home(){
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.SA_EAST_1)
				  .build();
		
		
		s3client.listBuckets();
		return "home";
	}
	@PostMapping("/salvar")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        return "redirect:/";
    }

}
