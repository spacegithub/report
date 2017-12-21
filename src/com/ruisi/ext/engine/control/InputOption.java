package com.ruisi.ext.engine.control;

import com.ruisi.ext.engine.wrapper.ExtRequest;
import com.ruisi.ext.engine.wrapper.ExtResponse;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;

public abstract interface InputOption
{
  public abstract ExtRequest getRequest();

  public abstract ExtResponse getResponse();

  public abstract Map<String, Object> getParams();

  public abstract String getParamValue(String paramString);

  public abstract String[] getParamValues(String paramString);

  public abstract void setParamValue(String paramString1, String paramString2);

  public abstract void setParamValues(String paramString, String[] paramArrayOfString);

  public abstract FileItem getUploadFile(String paramString);
}