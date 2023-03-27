package com.argo.r2eln.repo.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import org.springframework.beans.factory.annotation.Value;
import com.argo.r2eln.repo.dao.DgistR2ElnPDFDAO;

/*
 * smb를 통하지 않고, 리눅스 서버의 알프레스코와 윈도우 서버의 eln2pdf의 포트만 개방하여 통신하는 방식
 * 
 * 2020.10.21
 * hklee
 */
public class Argonet2PdfTransformer extends AbstractContentTransformer2 {
	
	@Autowired
	DgistR2ElnPDFDAO dao;
	
   private String sourceDir = "/data/convdata";                 // was 서버 경로
   private String convertDir = "/data/convdata";
   private boolean doDeletion = false;
   private boolean isAvaliable = true;

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
        
	    String jobid = sNodeRef;                        // nodeRef
	    String sourceFile = null;                       // PDF서버측에서 직접 다운로드 받게 하여 필요없어짐
	    String convertFile = null;                      // 다운로드 URL로 변경하여 필요없어짐
	    String filename = uuid + "_" + currentTime;     // 파일명_시간

	    
	    // 파일을 옮겨줄 필요가 없어서 주석처리함. 2020.10.21 by hklee 
//	    reader.getContent(new File(sourceFile));

	    System.out.println("extension : "+extension);
	    System.out.println("jobid : "+jobid);
	    boolean ret = MimetypeMap.EXTENSION_BINARY.equals(extension) ? false : generatePDF(jobid, sourceFile, convertFile, filename, extension);
    	System.out.println("ret : "+ret);
    	if(!ret)
    		throw new RuntimeException("cannot convert to PDF by "+this.getClass().getName());
    	
    	if (ret)
    	{
    	    try {
              dao.initConnection();
              Map<String,Object> params = new HashMap<String, Object>();
              params.put("jobid", jobid);
              convertFile = dao.selectDestFileJobConv(params);
              
    	    } catch(Exception e) {
              e.printStackTrace();
    	    } finally {
    	      dao.closeConnection();
    	    }
    	    
    	    if(convertFile != null && !"".equals(convertFile)) {
              InputStream in = new URL(convertFile).openStream();
              writer.putContent(in);
              in.close();    	      
    	    }

    	}
    	
    	/* 임시 파일을 생성하지 않으므로 주석처리 
    	if ((this.doDeletion) && (new File(sourceFile).exists())) {
    		new File(sourceFile).delete();
    	}
    	if ((this.doDeletion) && (new File(convertFile).exists())) {
    		new File(convertFile).delete();
    	}*/
	    
	}

	
	/**
	 * pdf 생성
	 * 
	 * @param targetPDF
	 * @param SavePDF
	 * @return
	 */
    public boolean generatePDF(String jobid, String targetPDF, String SavePDF, String filename, String ext) {
		long currentTime = System.currentTimeMillis();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("jobid", jobid);
		params.put("filename", filename + "." + ext);
		
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
				        
				        String isSuccess = "W";
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
	                    
				    } else if("E".equals(duplictateCheck)) {
				        flag = false;
				    } else if("S".equals(duplictateCheck)) {
                        flag = true;
                    }
				} else {
				  
			        dao.insertJobConv(params);
			        dao.commit();
	                
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
