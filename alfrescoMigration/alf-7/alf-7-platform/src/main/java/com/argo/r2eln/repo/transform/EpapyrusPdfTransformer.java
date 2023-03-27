package com.argo.r2eln.repo.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.argo.r2eln.repo.dao.R2ElnPDFDAO;

public class EpapyrusPdfTransformer extends AbstractContentTransformer2 {
	
	@Autowired
	R2ElnPDFDAO dao;
	
   private String sourceDir = "/data/home/eln/timestamp/pdf/source";
   private String convertDir = "/data/home/eln/timestamp/pdf/convert";
//   private String sourceDir = "/Users/momo31/alf/r2eln/tmp";
//   private String convertDir = "/Users/momo31/alf/r2eln/tmp";
   private boolean doDeletion = false;
   private boolean isAvaliable = true;
   private String convpdf = "http://203.250.192.226:7080/client/convpdf";

   public void setSourceDir(String dir)
   {
     this.sourceDir = dir;
   }
   
   public void setConvertDir(String dir)
   {
     this.convertDir = dir;
   }
   
   public void setAvaliable(String is)
   {
     this.isAvaliable = "true".equals(is);
   }
   
   public void setDoDeletion(String is)
   {
     this.doDeletion = "true".equals(is);
   }
   
   public void setConvpdf(String url) {
	   this.convpdf = url;
   }
   
   @Override
   public boolean isTransformableMimetype(String sourceMimetype, String targetMimetype, TransformationOptions options)
   {
	   if(!this.isAvaliable)
		   return false;
	   
       if (MimetypeMap.MIMETYPE_PDF.equals(sourceMimetype) || MimetypeMap.MIMETYPE_BINARY.equals(sourceMimetype))
       {
           // only support HTML -> TEXT
           return false;
       }
       else
       {
           return true;
       }
   }
   
	@Override
	protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options)
			throws Exception {
		System.out.println("===="+System.currentTimeMillis());
	    String extension = getExtensionOrAny(reader.getMimetype());
	    long currentTime = System.currentTimeMillis();


	    String jobid = "alf_"+currentTime;
	    String sourceFile = this.sourceDir + File.separator + jobid + "." + extension;
	    String convertFile = this.convertDir + File.separator + jobid + ".pdf";
	    
	    reader.getContent(new File(sourceFile));
	    String contentUrl = reader.getContentUrl();

	    System.out.println("extension : "+extension);
	    System.out.println("jobid : "+jobid);
	    boolean ret = MimetypeMap.EXTENSION_BINARY.equals(extension) ? false : generatePDF(jobid, sourceFile, convertFile, contentUrl, extension);
    	System.out.println("ret : "+ret);
    	if(!ret)
    		throw new RuntimeException("cannot convert to PDF by "+this.getClass().getName());
    	
    	if (ret)
    	{
    		FileInputStream fis = new FileInputStream(convertFile);
    		writer.putContent(new FileInputStream(convertFile));
    		fis.close();
    	}
    	if ((this.doDeletion) && (new File(sourceFile).exists())) {
    		new File(sourceFile).delete();
    	}
    	if ((this.doDeletion) && (new File(convertFile).exists())) {
    		new File(convertFile).delete();
    	}
	    
	}

	
	/**
	 * pdf 생성
	 * 
	 * @param targetPDF
	 * @param SavePDF
	 * @return
	 */
    public boolean generatePDF(String jobid, String targetPDF, String SavePDF, String contentUrl, String ext) {
		long currentTime = System.currentTimeMillis();
		//store://2017/11/9/16/57/ed8a7ae1-84b8-4dac-9614-102dc9464757.bin
		int start = StringUtils.lastIndexOf(contentUrl, "/")+1;
		int end = StringUtils.lastIndexOf(contentUrl, ".");
		String uuid = StringUtils.substring(contentUrl, start, end);
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("jobid", jobid);
		params.put("srcfile", convpdf+"/download?jobid="+jobid+"&ext="+ext);
		params.put("destfile", convpdf+"/upload?filename="+SavePDF);
		boolean flag = false;
	
			try {
				dao.initConnection();
				dao.insertJobConv(params);
				String isSuccess = "W";
				int round = 0;
				while(true) {
					Thread.currentThread().sleep(5000);
					isSuccess = dao.selectJobConv(params);
					System.out.println(currentTime+" jobid : "+params.get("jobid")+" jobstatus : "+isSuccess+" round : "+round);
					if("S".equals(isSuccess)) {
						flag = true;
						break;
					}
					if("F".equals(isSuccess)) {
						flag = false;
						break;
					}
					if(round > 24) {
						flag = false;
						break;
					}
					round++;
				}
			} catch (ClassNotFoundException | SQLException | InterruptedException | IOException e1) {
				e1.printStackTrace();
			} finally {
				try {dao.closeConnection();} catch (SQLException e) {e.printStackTrace();}
			}
		
		return flag;
	}
}
