package com.ruisi.ext.engine.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.ServletContext;

import com.ruisi.ext.engine.control.InputOption;
import com.ruisi.ext.engine.dao.DaoHelper;
import com.ruisi.ext.engine.wrapper.ExtRequest;

public abstract class ServiceSupport implements Service {
	protected ServletContext servletContext;
	protected DaoHelper daoHelper;

	public void initService(ServletContext servletContext, DaoHelper daoHelper) throws Exception {
		this.servletContext = servletContext;
		this.daoHelper = daoHelper;
	}

	protected void setNoResult(ExtRequest req) {
		req.setAttribute("ext.control.noResult", "y");
	}

	protected void sendRedirect(InputOption option, String sid, String method, boolean isParam) throws IOException {
		Map<String, Object> params = option.getParams();
		StringBuffer sb = new StringBuffer("extControl");
		sb.append("?serviceid=");
		sb.append(sid);
		sb.append("&methodId=");
		sb.append(method);
		if (isParam) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if (entry.getValue() instanceof String) {
					sb.append("&");
					sb.append((String) entry.getKey());
					sb.append("=");

					sb.append(URLEncoder.encode((String) entry.getValue(), "UTF-8"));
				}
			}
		}

		String flag = (String) option.getRequest().getAttribute("ext.control.noResult");
		if (!("y".equalsIgnoreCase(flag))) {
			setNoResult(option.getRequest());
		}
		option.getResponse().sendRedirect(sb.toString());
	}
}