package com.ruisi.ext.engine.control;

import com.opensymphony.xwork2.ActionSupport;
import com.ruisi.ext.engine.control.sys.LoginSecurityAdapter;
import com.ruisi.ext.engine.control.sys.LoginSecurityAdapterImpl;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import com.ruisi.ext.engine.wrapper.ExtRequestImpl;
import com.ruisi.ext.engine.wrapper.ExtResponse;
import com.ruisi.ext.engine.wrapper.ExtResponseImpl;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;

public class ExtViewAction extends ActionSupport {
	private LoginSecurityAdapter loginSecurityAdapter;

	public ExtViewAction() {
		this.loginSecurityAdapter = new LoginSecurityAdapterImpl();
	}

	public String execute() throws Exception {
		ExtRequest request = new ExtRequestImpl(ServletActionContext.getRequest());
		HttpServletResponse resp = ServletActionContext.getResponse();
		ExtResponse response = new ExtResponseImpl(resp);
		ServletContext context = ServletActionContext.getServletContext();
		String mvid = request.getParameter("mvid");

		if (!(this.loginSecurityAdapter.loginChk(request, response, context, null)))
			return "nologin";
		try {
			ActionProcess ap = ActionProcess.getInstance();
			ap.processMV(mvid, request, response, context);
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

		return "success";
	}
}