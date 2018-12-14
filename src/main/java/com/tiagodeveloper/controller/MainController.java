package com.tiagodeveloper.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
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
				 "<key-amazon>", 
				  "<value-amazon>"
				);
	}
	
	
	@GetMapping(value={"","/","/home"})
	public String home(Model model) throws IOException{

		model.addAttribute("movie", "/file");
		
		return "home";
	}
	@PostMapping("/salvar")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        return "redirect:/";
    }
	@GetMapping(value="/file", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] teste() throws IOException{
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.SA_EAST_1)
				  .build();
		InputStream in = s3client.getObject("aws-conversor-videos", "WhatsApp Video 2018-12-13 at 21.41.48.mp4").getObjectContent();
		return IOUtils.toByteArray(in);
		
	}
	
	@GetMapping("/zencoder")
	public String testeZen(){
		RestTemplate restTemplate = new RestTemplate();
		
				HttpHeaders headers = new HttpHeaders();
	      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      headers.set("Zencoder-Api-Key", "<key-zencoder>");
	      headers.setContentType(MediaType.APPLICATION_JSON);
	      HttpEntity<String> entity = new HttpEntity<String>("{test: true, input :\"https://s3-sa-east-1.amazonaws.com/aws-conversor-videos/WhatsApp+Video+2018-12-13+at+21.41.48.mp4\"}",headers);
		
		String teste = restTemplate.exchange("https://app.zencoder.com/api/v2/jobs", HttpMethod.POST, entity, String.class).getBody();
		System.out.println(teste);
		return "redirect:/";
	}

}
