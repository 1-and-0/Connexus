package us.connex.services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

// google fileservice has been depricated - alternative google cloud storage
@SuppressWarnings("deprecation")
public class ImageHelper 
{
	private static final Logger log		=	Logger.getLogger(ImageHelper.class.getName()) ;
	
	public String uploadToBlobByUrl(String urlString)
	{
		URL	url							=	null;
		byte[] data 					= 	null;
		String mimeType 				= 	null;
		FileService fileService 		= 	null;
		AppEngineFile file 				= 	null;
		FileWriteChannel writeChannel 	=	null;	
		BlobKey blobKey 				=	null;
		
		String  returnImageUrl			=	null;
		
		int temp 						=	0;
		ByteArrayOutputStream reader 	=	null;
		InputStream inputStream 		=	null;
		try
		{
			if(urlString == null)
				return null;
			
			url 					= 	new URL(urlString);
			HttpURLConnection con	=	(HttpURLConnection) url.openConnection();

			con.setConnectTimeout(70000);
			con.setReadTimeout(70000);
			
			inputStream 				= con.getInputStream();
			reader						= new ByteArrayOutputStream();
			
			while (( temp = inputStream.read()) != -1)
			{
				reader.write(temp);
			}
			
	        if(urlString.endsWith("jpg") || urlString.endsWith("jpeg"))
	        {
	        	mimeType 	= "image/jpeg";
	        }
	        else if(urlString.endsWith("png"))
			{
	        	mimeType	= "image/png";
			}
	        else
	        {
	        	log.info("unknown mime type :: "+urlString);
	        	mimeType 	= "image/jpeg";
	        }
			
	        data 		= reader.toByteArray();
			fileService = FileServiceFactory.getFileService();
			file = fileService.createNewBlobFile(mimeType); 
			writeChannel = fileService.openWriteChannel(file, true);
			writeChannel.write(java.nio.ByteBuffer.wrap(data));
			writeChannel.closeFinally();
			
			blobKey = fileService.getBlobKey(file);
			returnImageUrl = ImagesServiceFactory.getImagesService().getServingUrl(
				ServingUrlOptions.Builder.withBlobKey(blobKey).secureUrl(true));
			
			log.info("the url generated is :: "+returnImageUrl);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE," error in uploading file "+e.getMessage(),e);
			return null;
		}
		return returnImageUrl;
	}
	
	public String uploadImageDataToBlob(byte[] imageData,String type)
	{
		FileService fileService 		= 	null;
		AppEngineFile file 				= 	null;
		FileWriteChannel writeChannel 	=	null;	
		BlobKey blobKey 				=	null;
		
		String  returnImageUrl			=	null;
		String mimeType					=	null;
		
		try
		{
	        if(type.equals("jpg") || type.equals("jpeg"))
	        {
	        	mimeType 	= "image/jpeg";
	        }
	        else if(type.endsWith("png"))
			{
	        	mimeType	= "image/png";
			}
	        else
	        {
	        	mimeType 	= "image/jpeg";
	        }
	        
			fileService = FileServiceFactory.getFileService();
			file = fileService.createNewBlobFile(mimeType); 
			writeChannel = fileService.openWriteChannel(file, true);
			writeChannel.write(java.nio.ByteBuffer.wrap(imageData));
			writeChannel.closeFinally();
			
			blobKey = fileService.getBlobKey(file);
			returnImageUrl = ImagesServiceFactory.getImagesService().getServingUrl(
				ServingUrlOptions.Builder.withBlobKey(blobKey).secureUrl(true));
			
			log.info("the url generated is :: "+returnImageUrl);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE," error in uploading file "+e.getMessage(),e);
			return null;
		}
		return returnImageUrl;
	}
}
