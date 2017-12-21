package com.ruisi.ext.engine.wrapper;

import com.ruisi.ext.engine.view.exception.ExtRuntimeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class ExtRequestImpl implements ExtRequest {
	private HttpServletRequest request;
	private List<FileItem> files;

	public ExtRequestImpl(HttpServletRequest request) {
		this.request = request;
		if (ServletFileUpload.isMultipartContent(request)) {
			FileItemFactory factory = new DiskFileItemFactory();
			try {
				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setHeaderEncoding("UTF-8");
				this.files = upload.parseRequest(request);
			} catch (Exception ex) {
				throw new ExtRuntimeException(ex);
			}
		}
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ExtRuntimeException(e);
		}
	}

	public HttpServletRequest getRequest() {
		return this.request;
	}

	public String getAuthType() {
		return this.request.getAuthType();
	}

	public String getContextPath() {
		return this.request.getContextPath();
	}

	public Cookie[] getCookies() {
		return this.request.getCookies();
	}

	public long getDateHeader(String arg0) {
		return this.request.getDateHeader(arg0);
	}

	public String getHeader(String arg0) {
		return this.request.getHeader(arg0);
	}

	public Enumeration getHeaderNames() {
		return this.request.getHeaderNames();
	}

	public Enumeration getHeaders(String arg0) {
		return this.request.getHeaders(arg0);
	}

	public int getIntHeader(String arg0) {
		return this.request.getIntHeader(arg0);
	}

	public String getMethod() {
		return this.request.getMethod();
	}

	public String getPathInfo() {
		return this.request.getPathInfo();
	}

	public String getPathTranslated() {
		return this.request.getPathTranslated();
	}

	public String getQueryString() {
		return this.request.getQueryString();
	}

	public String getRemoteUser() {
		return this.request.getRemoteHost();
	}

	public String getRequestURI() {
		return this.request.getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return this.request.getRequestURL();
	}

	public String getRequestedSessionId() {
		return this.request.getRequestedSessionId();
	}

	public String getServletPath() {
		return this.request.getServletPath();
	}

	public ExtSession getSession() {
		return new ExtSessionImpl(this.request.getSession());
	}

	public ExtSession getSession(boolean arg0) {
		return new ExtSessionImpl(this.request.getSession(arg0));
	}

	public Principal getUserPrincipal() {
		return this.request.getUserPrincipal();
	}

	public boolean isRequestedSessionIdFromCookie() {
		return this.request.isRequestedSessionIdFromCookie();
	}

	public boolean isRequestedSessionIdFromURL() {
		return this.request.isRequestedSessionIdFromURL();
	}

	/** @deprecated */
	public boolean isRequestedSessionIdFromUrl() {
		return this.request.isRequestedSessionIdFromUrl();
	}

	public boolean isRequestedSessionIdValid() {
		return this.request.isRequestedSessionIdValid();
	}

	public boolean isUserInRole(String arg0) {
		return this.request.isUserInRole(arg0);
	}

	public Object getAttribute(String arg0) {
		return this.request.getAttribute(arg0);
	}

	public Enumeration getAttributeNames() {
		return this.request.getAttributeNames();
	}

	public String getCharacterEncoding() {
		return this.request.getCharacterEncoding();
	}

	public int getContentLength() {
		return this.request.getContentLength();
	}

	public String getContentType() {
		return this.request.getContentType();
	}

	public ServletInputStream getInputStream() throws IOException {
		return this.request.getInputStream();
	}

	public String getLocalAddr() {
		return this.request.getLocalAddr();
	}

	public String getLocalName() {
		return this.request.getLocalName();
	}

	public int getLocalPort() {
		return this.request.getLocalPort();
	}

	public Locale getLocale() {
		return this.request.getLocale();
	}

	public Object getProxy() {
		return this.request;
	}

	public Enumeration getLocales() {
		return this.request.getLocales();
	}

	public FileItem getUploadFile(String name) {
		for (FileItem file : this.files) {
			if (file.getFieldName().equalsIgnoreCase(name)) {
				return file;
			}
		}
		return null;
	}

	public String getParameter(String arg0) {
		if (arg0 == null) {
			return null;
		}
		if (ServletFileUpload.isMultipartContent(this.request)) {
			Iterator localIterator = this.files.iterator();
			while (localIterator.hasNext()) {
				FileItem file = (FileItem) localIterator.next();
				if (file.getFieldName().equalsIgnoreCase(arg0))
					try {
						return new String(file.get(), "UTF-8");
					} catch (Exception ex) {
						throw new ExtRuntimeException(ex);
					}
			}
		}
		return this.request.getParameter(arg0);
	}

	public Map getParameterMap() {
		return this.request.getParameterMap();
	}

	public Enumeration getParameterNames() {
		return this.request.getParameterNames();
	}

	public String[] getParameterValues(String arg0) {
		if (ServletFileUpload.isMultipartContent(this.request)) {
			List<String> ls = new ArrayList<String>();
			for (FileItem file : this.files) {
				if (!(file.getFieldName().equalsIgnoreCase(arg0)))
					continue;
				try {
					String ret = new String(file.get(), "UTF-8");
					ls.add(ret);
				} catch (Exception ex) {
					throw new ExtRuntimeException(ex);
				}
			}

			String[] rets = new String[ls.size()];
			return ls.toArray(rets);
		}
		return this.request.getParameterValues(arg0);
	}

	public String getProtocol() {
		return this.request.getProtocol();
	}

	public BufferedReader getReader() throws IOException {
		return this.request.getReader();
	}

	public String getRealPath(String arg0) {
		return this.request.getRealPath(arg0);
	}

	public String getRemoteAddr() {
		return this.request.getRemoteAddr();
	}

	public String getRemoteHost() {
		return this.request.getRemoteHost();
	}

	public int getRemotePort() {
		return this.request.getRemotePort();
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		return this.request.getRequestDispatcher(arg0);
	}

	public String getScheme() {
		return this.request.getScheme();
	}

	public String getServerName() {
		return this.request.getServerName();
	}

	public int getServerPort() {
		return this.request.getServerPort();
	}

	public boolean isSecure() {
		return this.request.isSecure();
	}

	public void removeAttribute(String arg0) {
		this.request.removeAttribute(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
		this.request.setAttribute(arg0, arg1);
	}

	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		this.request.setCharacterEncoding(arg0);
	}
}