package com.argo.r2eln.repo.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.argo.r2eln.repo.dao.DgistR2ElnPDFDAO;

/*
 * 리눅스에서 samba를 통해 공유폴더를 생성한 후, 윈도우에 마운트해둔 방식으로 사용가능함.
 *  
 */
public class ArgonetPdfTransformer extends AbstractContentTransformer2 {
	
	@Autowired
	DgistR2ElnPDFDAO dao;
	
   private String sourceDir = "/data/convdata";                 // was 서버 경로
   private String convertDir = "/data/convdata";
   private String rSourceDir = "\\\\10.2.2.151\\alfresco\\";    // pdf 서버에서 접근할 수 있는 경로
   private String rConvertDir = "\\\\10.2.2.151\\alfresco\\";
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
		System.out.println("====ArgonetPdfTransformer : "+System.currentTimeMillis());
	    String extension = getExtensionOrAny(reader.getMimetype());
	    long currentTime = System.currentTimeMillis();


	    String sNodeRef = options.getSourceNodeRef().toString();
	    System.out.println("sNodeRef : "+ sNodeRef);

        String uuid = options.getSourceNodeRef().getId();
        
	    String jobid = sNodeRef;                                                                                // nodeRef
	    String sourceFile = this.sourceDir + File.separator + uuid + "_" + currentTime + "." + extension;     // 절대경로 - 파일명_시간.ext
	    String convertFile = this.convertDir + File.separator + uuid + "_" + currentTime + ".pdf";            // 절대경로 - 파일명_시간.pdf
        String rSourceFile = this.rSourceDir + File.separator + uuid + "_" + currentTime + "." + extension;   // pdf 서버에서 접근 가능한 경로
        String rConvertFile = this.rConvertDir + File.separator + uuid + "_" + currentTime + ".pdf";
        
	    reader.getContent(new File(sourceFile));

	    System.out.println("extension : "+extension);
	    System.out.println("jobid : "+jobid);
	    boolean ret = MimetypeMap.EXTENSION_BINARY.equals(extension) ? false : generatePDF(jobid, rSourceFile, rConvertFile, extension);
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

	
	/**
	 * pdf 생성
	 * 
	 * @param targetPDF
	 * @param SavePDF
	 * @return
	 */
    public boolean generatePDF(String jobid, String targetPDF, String SavePDF, String ext) {
		long currentTime = System.currentTimeMillis();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("jobid", jobid);
		params.put("srcfile", targetPDF);      // 절대경로 변경
		params.put("destfile", SavePDF);
		boolean flag = false;
	
			try {
				dao.initConnection();
				
				// 이미 변환 시도된 적이 있는지 체크
				String duplictateCheck = dao.selectJobConv(params);
				System.out.println("duplictateCheck : "+ duplictateCheck);
				if(duplictateCheck != null && !"".equals(duplictateCheck) && !"N".equals(duplictateCheck)) {
				    // 변환 시도된 적이 있음.
				    if("W".equals(duplictateCheck)) {
				        flag = false;
				    } else if("E".equals(duplictateCheck)) {
				        flag = false;
				    }
				    
				} else {
				    if(duplictateCheck == null || (duplictateCheck != null && "".equals(duplictateCheck))) {
				        dao.insertJobConv(params);
				        dao.commit();
				    }
	                
	                String isSuccess = "W";             // W : Working
	                int round = 0;
	                while(true) {
	                    Thread.currentThread().sleep(5000);
	                    isSuccess = dao.selectJobConv(params);
	                    System.out.println(currentTime+" jobid : "+params.get("jobid")+" jobstatus : "+isSuccess+" round : "+round);
	                    if("S".equals(isSuccess)) {    // S : Success
	                        flag = true;
	                        break;
	                    }
	                    if("E".equals(isSuccess)) {    // E : Error
	                        flag = false;
	                        break;
	                    }
	                    if(round > 24) {
	                        flag = false;
	                        break;
	                    }
	                    round++;
	                }				  
				}

			} catch (ClassNotFoundException | SQLException | InterruptedException | IOException e1) {
				e1.printStackTrace();
			} finally {
				try {dao.closeConnection();} catch (SQLException e) {e.printStackTrace();}
			}
		
		return flag;
	}
}
