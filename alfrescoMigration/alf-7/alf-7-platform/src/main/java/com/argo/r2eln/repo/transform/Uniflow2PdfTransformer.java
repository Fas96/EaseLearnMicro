package com.argo.r2eln.repo.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;

import com.unidocs.workflow.client.WFJob;
import com.unidocs.workflow.common.FileEx;
import com.unidocs.workflow.common.JobResult;

public class Uniflow2PdfTransformer extends AbstractContentTransformer2 {

	  private String sourceDir = "R:\\";
	  private String convertDir = "R:\\";
	  private boolean doDeletion = true;
	  private boolean isAvaliable = false;
	  private String remoteDir = "C:\\PDFDATA\\ALFDATA";
	  private String serverIP = "210.219.43.172";
	  private int serverPort = 715;
	  
	  private UniFlow.UniFlowGenPDF genPDF = new UniFlow.UniFlowGenPDF();
	
	  public void init() {
		  genPDF.SetServerAddress(serverIP, serverPort);
	  }
	  
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
	  
	  public void setRemoteDir(String path)
	  {
	    this.remoteDir = path;
	  }
	  
	  public void setServerIP(String ip)
	  {
	    this.serverIP = ip;
	  }
	  
	  public void setServerPort(String port)
	  {
	    this.serverPort = Integer.parseInt(port);
	  }
	  
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
	  
	  protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options)
	    throws Exception
	  {
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
		    boolean ret = MimetypeMap.EXTENSION_BINARY.equals(extension) ? false : generatePDF(new File(sourceFile), new File(convertFile));
	    	System.out.println("ret : "+ret);
	    	if(!ret)
	    		throw new RuntimeException("cannot convert to PDF by "+this.getClass().getName());
	    	
	    	if (ret)
	    	{
	    		FileInputStream fis = new FileInputStream(convertFile);
	    		writer.putContent(fis);
	    		fis.close();
	    	}
	    	if ((this.doDeletion) && (new File(sourceFile).exists())) {
	    		new File(sourceFile).delete();
	    	}
	    	if ((this.doDeletion) && (new File(convertFile).exists())) {
	    		new File(convertFile).delete();
	    	}
	  }
	  
	  public boolean generatePDF(File srcFile, File pdfFile)
	  {



		if(srcFile.exists())
		{
			String filename = srcFile.getName();
			try
			{
				String remoteSrcPath = remoteDir+File.separator+filename;
				String remotePdfPath = remoteDir+File.separator+pdfFile.getName();
				
				System.out.println(srcFile.getAbsolutePath()+" is converting to pdf");
				System.out.println(remotePdfPath+" is the server path");
				
				int ret = genPDF.GeneratePDF(remoteSrcPath, remotePdfPath, 0);

				System.out.println("ret code - "+ret);
				
				if(ret<=0) {
					System.out.println("ret-"+ret);
					throw new RuntimeException("fail to convert pdf");
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("fail to convert pdf");
			}				

		}
		else
		{
			throw new RuntimeException("fail to convert pdf for ");
		}
	    return true;
	  }

}
