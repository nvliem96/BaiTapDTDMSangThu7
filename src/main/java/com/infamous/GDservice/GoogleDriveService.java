package com.infamous.GDservice;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;

import com.google.api.services.drive.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.client.http.FileContent;

import com.infamous.GDservice.InfomationService;

@Service
public class GoogleDriveService {

	InfomationService infor = new InfomationService();

	private Drive service;

	public GoogleDriveService() {
		this.service = getService();
	}

	private Drive getService() {
		Drive service = null;
		try {
			Credential credential = infor.authorize();
			service = new Drive.Builder(infor.HTTP_TRANSPORT, infor.JSON_FACTORY, credential)
					.setApplicationName(infor.APPLICATION_NAME).build();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return service;
	}

	public boolean uploadFile(String fileNameAfterUpload, String Path, String typeFile) {
		try {
			File fileMetadata = new File();
			fileMetadata.setName(fileNameAfterUpload);
			java.io.File filePath = new java.io.File(Path);

			FileContent mediaContent = new FileContent(typeFile, filePath);
			File file = service.files().create(fileMetadata, mediaContent).setFields("id").execute();

			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public InformationFile printInformationFile(String fileId) {
		InformationFile inforFile = null;
		try {
			File file = service.files().get(fileId).execute();
			inforFile = new InformationFile();
			inforFile.setId(file.getId());
			inforFile.setDescription(file.getDescription());
			inforFile.setTitle(file.getName());
			inforFile.setExtension(file.getFileExtension());
			inforFile.setType(file.getMimeType());
			
		} catch (IOException e) {
			System.out.println("An error occured: " + e);
		}
		return inforFile;

	}

	public ByteArrayOutputStream downloadFile(String fileId) {
		ByteArrayOutputStream outputStream=null;
		try {
			outputStream = new ByteArrayOutputStream();
				
			
			 service.files().get(fileId)
              .executeMediaAndDownloadTo(outputStream);
			
			

		} catch (Exception e) {
		
		}
		return outputStream;
	}

	public List<InformationFile> getAllFile() {
		List<InformationFile> list = null;
		try {

			FileList result = service.files().list().setFields("nextPageToken, files(id, name)").execute();

			List<File> files = result.getFiles();
			
			if (files == null || files.size() == 0) {
				return null;
			} else {
				list = new ArrayList<>();
				for (File file : files) {
				
					InformationFile infor = new InformationFile();

					infor.setTitle(file.getName());
					infor.setId(file.getId());
					infor.setType(file.getKind());

					list.add(infor);
				}
			}
		} catch (Exception e) {

		}
		return list;
	}
	public static void main(String... args){
		GoogleDriveService ss=new GoogleDriveService();
		
	}
}
