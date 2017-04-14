package com.infamous;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.api.services.drive.Drive;
import com.infamous.GDservice.GoogleDriveService;
import com.infamous.GDservice.InformationFile;


@Controller
public class MainController {

	private final GoogleDriveService serviceGoogle;

	@Autowired
	public MainController(GoogleDriveService serviceGoogle) {
		this.serviceGoogle = serviceGoogle;
	}
	//Home
	@GetMapping("/")
	public String Home(Model model) {
		return "index";
	}

	/**
	 * View
	 */
	@GetMapping("upload")
	public String Upload(Model model) {
		return "upload";
	}
	/**
	 * Submit
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@PostMapping("upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        boolean flag=serviceGoogle.uploadFile(file.getOriginalFilename(),file.getName(), file.getContentType());
        System.out.println(file.getName()+"     "+file.getOriginalFilename()+"     "+file.getContentType());
        
        if(flag==true){
        	 redirectAttributes.addFlashAttribute("message",
                     "Upload Success");
        }else{
        	redirectAttributes.addFlashAttribute("message",
                    "Upload fail");
        }
        
        return "redirect:/upload";
		
		//UpLoadMultipart 
//		 URL url;
//		try {
//			url = new URL("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart");
//			
//			 HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
//			  httpCon.setDoOutput(true);
//			  httpCon.setRequestMethod("POST");
//			  httpCon.setRequestProperty("Content-Type", file.getContentType());
//			  httpCon.setRequestProperty("Content-Length",file.getSize()+"");
//			
//			  OutputStreamWriter out = new OutputStreamWriter(
//			      httpCon.getOutputStream());
//			  System.out.println(httpCon.getResponseCode());
//			  System.out.println(httpCon.getResponseMessage());
//			  out.close();
//			  
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
		 

    }
	
	/**
	 * Information file in google drive
	 * @return
	 */
	@GetMapping("all-file")
	@ModelAttribute("list")
	public List<InformationFile> Download() {
		return serviceGoogle.getAllFile();
	}

	/**
	 * Download File
	 * @param fileid
	 * @param response
	 */
	@GetMapping("/download/{fileid}")
	@ResponseBody
	public void Download(@PathVariable String fileid,HttpServletResponse response) {
		ByteArrayOutputStream out= serviceGoogle.downloadFile(fileid);
		InformationFile info=serviceGoogle.printInformationFile(fileid);
		System.out.println(info.toString());
		response.setHeader("Content-Type", info.getType());
	       
        response.setHeader("Content-Length", String.valueOf(out.size()));
            
        response.setHeader("Content-Disposition", "inline; filename=\"" + info.getTitle() + "\"");
        
        try {
			response.getOutputStream().write(out.toByteArray(), 0, out.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}