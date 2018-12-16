package com.tiagodeveloper.controller;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.amazonaws.services.s3.AmazonS3;
import com.tiagodeveloper.service.ZencoderService;

@Controller
@RequestMapping(value={"","/","/main"})
public class MainController {
	
	@Autowired
	private AmazonS3 s3client;
	
	@Autowired
	private ZencoderService zencoderService;
	
	
	@GetMapping(value={"","/","/home"})
	public String home(Model model) throws IOException{

		model.addAttribute("movie", "/file");
		
		return "home";
	}
	@PostMapping("/salvar")
    public @ResponseBody JSONObject handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
		String bucketName = "aws-conversor-videos";
		
		File arq = new File(file.getOriginalFilename());
		
		FileUtils.copyInputStreamToFile(file.getInputStream(), arq);
		
		this.s3client.putObject(bucketName , file.getOriginalFilename(), arq);

		URL url = this.s3client.getUrl(bucketName , file.getOriginalFilename());
		
		arq.delete();
		
		return this.zencoderService.encodingJob(url.toString());
    }
	
	
	
	@GetMapping(value="/file", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody byte[] teste() throws IOException{
		InputStream in = s3client.getObject("aws-conversor-videos", "WhatsAppVideo.mp4").getObjectContent();
		return IOUtils.toByteArray(in);
		
	}
	
	@GetMapping("/zencoder")
	public @ResponseBody JSONObject encodingJob(){
		return this.zencoderService.encodingJob("s3://s3-sa-east-1.amazonaws.com/aws-conversor-videos/video-teste.mp4");
	}
	
	@GetMapping("/verifyProgressOutput/{id}")
	public @ResponseBody JSONObject verifyProgressOutput(@PathVariable("id") Long id){
		return this.zencoderService.progressJobOutput(id);
	}
	

}
