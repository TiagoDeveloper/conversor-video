package com.tiagodeveloper.controller;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource(value={"credentials.properties"},ignoreResourceNotFound=true)
public class MainController {
	
	private AWSCredentials credentials;
	
	@Value("${basic.aws.credentials.accesskey}")
	private String accessKey;

	@Value("${basic.aws.credentials.secretkey}")
	private String secretKey;

	@Value("${Zencoder.api.key}")
	private String zencoderApiKey;
	
	@PostConstruct
	public void init() {
		this.credentials = new BasicAWSCredentials(
								accessKey, 
								secretKey
							);
	}
	
	
	
	@GetMapping(value={"","/","/home"})
	public String home(Model model) throws IOException{

		model.addAttribute("movie", "/file");
		
		return "home";
	}
	@PostMapping("/salvar")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {

		AmazonS3 s3client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.SA_EAST_1)
				.build();
		
		File arq = new File(file.getOriginalFilename());
		
//		file.transferTo(arq);
		FileUtils.copyInputStreamToFile(file.getInputStream(), arq);
		
		s3client.putObject("aws-conversor-videos", file.getOriginalFilename(), arq);
		
		arq.delete();
		
		return "redirect:/";
    }
	
	
	
	@GetMapping(value="/file", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] teste() throws IOException{
		AmazonS3 s3client = AmazonS3ClientBuilder
				  .standard()
				  .withCredentials(new AWSStaticCredentialsProvider(credentials))
				  .withRegion(Regions.SA_EAST_1)
				  .build();
		InputStream in = s3client.getObject("aws-conversor-videos", "WhatsAppVideo.mp4").getObjectContent();
		return IOUtils.toByteArray(in);
		
	}
	
	@GetMapping("/zencoder")
	public String testeZen(){
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();

		  headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      headers.set("Zencoder-Api-Key", zencoderApiKey);
	      headers.setContentType(MediaType.APPLICATION_JSON);
	      
	      
	      
	    String input = "{\"input\": \"s3://s3-sa-east-1.amazonaws.com/aws-conversor-videos/video-teste.mp4\",\"outputs\": [{\"label\": \"mp4 high\",\"h264_profile\": \"high\"},{\"label\": \"webm\",\"format\": \"webm\"},{\"label\": \"ogg\",\"format\": \"ogg\"},{\"label\": \"mp4 low\",\"size\": \"640x480\"}]}";  

	    HttpEntity<String> entity = new HttpEntity<String>(input,headers);
		String teste = restTemplate.exchange("https://app.zencoder.com/api/v2/jobs", HttpMethod.POST, entity, String.class).getBody();
		System.out.println(teste);
		return "redirect:/";
	}

}
