package com.ruisi.ext.engine.wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import org.apache.commons.fileupload.FileItem;

public abstract interface ExtRequest {
	public abstract String getAuthType();

	public abstract String getContextPath();

	public abstract Cookie[] getCookies();

	public abstract long getDateHeader(String paramString);

	public abstract String getHeader(String paramString);

	public abstract Enumeration getHeaderNames();

	public abstract Enumeration getHeaders(String paramString);

	public abstract int getIntHeader(String paramString);

	public abstract String getMethod();

	public abstract String getPathInfo();

	public abstract String getPathTranslated();

	public abstract String getQueryString();

	public abstract String getRemoteUser();

	public abstract String getRequestURI();

	public abstract StringBuffer getRequestURL();

	public abstract String getRequestedSessionId();

	public abstract String getServletPath();

	public abstract ExtSession getSession();

	public abstract ExtSession getSession(boolean paramBoolean);

	public abstract Principal getUserPrincipal();

	public abstract boolean isRequestedSessionIdFromCookie();

	public abstract boolean isRequestedSessionIdFromURL();

	public abstract Object getAttribute(String paramString);

	public abstract Enumeration getAttributeNames();

	public abstract String getCharacterEncoding();

	public abstract int getContentLength();

	public abstract String getContentType();

	public abstract ServletInputStream getInputStream() throws IOException;

	public abstract FileItem getUploadFile(String paramString);

	public abstract String getParameter(String paramString);

	public abstract Map getParameterMap();

	public abstract Enumeration getParameterNames();

	public abstract String[] getParameterValues(String paramString);

	public abstract String getProtocol();

	public abstract BufferedReader getReader() throws IOException;

	public abstract String getRealPath(String paramString);

	public abstract String getRemoteAddr();

	public abstract String getRemoteHost();

	public abstract int getRemotePort();

	public abstract RequestDispatcher getRequestDispatcher(String paramString);

	public abstract String getScheme();

	public abstract String getServerName();

	public abstract int getServerPort();

	public abstract boolean isSecure();

	public abstract void removeAttribute(String paramString);

	public abstract void setAttribute(String paramString, Object paramObject);

	public abstract void setCharacterEncoding(String paramString) throws UnsupportedEncodingException;

	public abstract String getLocalAddr();

	public abstract String getLocalName();

	public abstract int getLocalPort();

	public abstract Locale getLocale();

	public abstract Enumeration getLocales();

	public abstract Object getProxy();
}