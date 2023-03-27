package com.argo.r2eln.repo.transform;

import com.unidocs.workflow.client.WFJob;
import com.unidocs.workflow.common.FileEx;
import com.unidocs.workflow.common.JobResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;

public class UniflowPdfTransformer
  extends AbstractContentTransformer2
{
  private String sourceDir = "/data/eln/pdf/source";
  private String convertDir = "/data/eln/pdf/convert";
  private boolean doDeletion = true;
  private boolean isAvaliable = false;
  private String serverPath = "R:\\";
  private String serverIP = "143.248.5.97";
  private int serverPort = 729;
  
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
  
  public void setServerPath(String path)
  {
    this.serverPath = path;
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
    return this.isAvaliable;
  }
  
  protected void transformInternal(ContentReader reader, ContentWriter writer, TransformationOptions options)
    throws Exception
  {
    String extension = getExtensionOrAny(reader.getMimetype());
    
    long currentTime = System.currentTimeMillis();
    
    String sourceFile = this.sourceDir + File.separator + currentTime + "." + extension;
    
    String convertFile = this.convertDir + File.separator + currentTime + ".pdf";
    
    reader.getContent(new File(sourceFile));
    
    boolean ret = generatePDF(sourceFile, convertFile);
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
  
  public boolean generatePDF(String targetPDF, String SavePDF)
  {
    File N = new File(targetPDF);

    if (N.exists())
    {
      N = new File(SavePDF);
      WFJob job = null;
      String outfile = null;

      try
      {
        job = new WFJob();

        FileEx srcFile = new FileEx(targetPDF);
        JobResult jr = job.generatePDF(srcFile, new File(targetPDF).getName().toString() + ".pdf", 0);

        if (jr.getStatus() == 1)
        {
          System.out.println("PDFConvert Success : " + targetPDF);
          FileEx[] out = jr.getOutFile();

          for (int j = 0; j < out.length; j++)
          {
            out[j].saveToByStream(new File(SavePDF), true);
          }
        }
        else
        {
          throw new RuntimeException("fail to convert pdf for " + targetPDF + "\n (" + jr.getErrMsg() + ")" + jr.getErrCode());
        }
        System.out.println("generatePDF>>6");
      }
      catch (Exception e)
      {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        throw new RuntimeException("fail to convert pdf due to \n " + sw.toString());
      }
    }
    else
    {
      throw new RuntimeException("fail to convert pdf for ");
    }
    return true;
  }
}
