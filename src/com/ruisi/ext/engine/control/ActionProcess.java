package com.ruisi.ext.engine.control;

import com.ruisi.ext.engine.ConstantsEngine;
import com.ruisi.ext.engine.dao.DaoHelper;
import com.ruisi.ext.engine.init.XmlParser;
import com.ruisi.ext.engine.init.config.InterceptorConfig;
import com.ruisi.ext.engine.init.config.ServiceConfig;
import com.ruisi.ext.engine.service.Service;
import com.ruisi.ext.engine.service.ServiceSupport;
import com.ruisi.ext.engine.util.MethodUtils;
import com.ruisi.ext.engine.view.context.ExtContext;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.exception.ExtRuntimeException;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import com.ruisi.ext.engine.wrapper.ExtResponse;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ActionProcess {
	private static Log log = LogFactory.getLog(ActionProcess.class);

	private static ActionProcess instance = new ActionProcess();

	private static Object lockObj = new Object();

	public static MVContext findMVContextById(String mvid) throws Exception {
		MVContext ret = null;
		synchronized (lockObj) {
			String devModel = ExtContext.getInstance().getConstant("devMode");
			if ("true".equalsIgnoreCase(devModel)) {
				ExtContext.getInstance().removeMV(mvid);
				ret = XmlParser.getInstance().parserPageAndChkRef(mvid);
			} else if (!(ExtContext.getInstance().chkMvExist(mvid))) {
				ret = XmlParser.getInstance().parserPageAndChkRef(mvid);
			} else {
				ret = ExtContext.getInstance().getMVContext(mvid);
			}
		}

		return ret;
	}

	public static MVContext findMVContextById(String mvid, boolean rebuild) throws Exception {
		if (rebuild) {
			return findMVContextById(mvid);
		}
		MVContext ret = null;
		synchronized (lockObj) {
			if (!(ExtContext.getInstance().chkMvExist(mvid)))
				ret = XmlParser.getInstance().parserPageAndChkRef(mvid);
			else {
				ret = ExtContext.getInstance().getMVContext(mvid);
			}
		}
		return ret;
	}

	public static ActionProcess getInstance() {
		if (instance == null) {
			throw new ExtRuntimeException("未初始化 actionProcess 对象.");
		}
		return instance;
	}

	public void processMV(String mvId, ExtRequest request, ExtResponse response, ServletContext sctx) throws Exception {
		String mvid = mvId;
		findMVContextById(mvid);
		Map params = ExtContext.getInstance().getParams(mvid);
		InputOption option = InputOptionFactory.createInputOption(request, response, params);
		request.setAttribute("ext.view.mvid", mvid);
		request.setAttribute("ext.view.option", option);
	}

	public void process(ExtRequest request, ExtResponse response, ServletContext context, DaoHelper helper)
			throws Exception {
		String sid = request.getParameter("serviceid");
		Service ser = ExtContext.getInstance().getService(sid);
		ServiceConfig sconfig = ExtContext.getInstance().getServiceConfig(sid);

		String method = request.getParameter("methodId");
		String mvid = sconfig.getMVID(method);

		findMVContextById(mvid);

		String fromId = request.getParameter("t_from_id");
		if ((fromId != null) && (fromId.length() > 0)) {
			findMVContextById(fromId, false);
		}

		if (log.isDebugEnabled()) {
			log.debug(request.getMethod() + " serviceId" + " = " + sid + "(" + ser.getClass().getName()
					+ "), methodId = " + method);
			log.debug("fromMV = " + fromId + ", returnMv = " + mvid);
		}

		request.setAttribute("ext.view.serviceId", sid);
		request.setAttribute("ext.view.methodId", method);
		request.setAttribute("ext.view.mvid", mvid);
		request.setAttribute("ext.view.fromId", fromId);

		if (ser instanceof ServiceSupport) {
			ServiceSupport sp = (ServiceSupport) ser;
			sp.initService(context, helper);
		}

		Map params = null;
		if (request.getMethod().equalsIgnoreCase("POST")) {
			if ((fromId == null) || (fromId.length() == 0)) {
				String err = ConstantsEngine.replace("请求方法为POST,但 $0 却为null", "t_from_id");
				throw new ExtRuntimeException(err);
			}
			params = ExtContext.getInstance().getParams(fromId);
		} else {
			params = ExtContext.getInstance().getParams(mvid);
		}

		InputOption option = InputOptionFactory.createInputOption(request, response, params);

		request.setAttribute("ext.view.option", option);

		if (log.isDebugEnabled()) {
			log.debug("params = " + option.getParams());
		}

		if ((method == null) || (method.length() == 0)) {
			ser.execute(option);
		} else {
			Class[] types = { InputOption.class };
			MethodUtils.invokeMethod(ser, method, new Object[] { option }, types);
		}

		String[] interRefs = sconfig.getInterRefs(method);
		if ((interRefs != null) && (interRefs.length > 0))
			for (String interRef : interRefs) {
				String clazz = ExtContext.getInstance().getInterceptor(interRef).getClazz();
				if (log.isDebugEnabled()) {
					log.debug("invoke拦截器: " + clazz + ", method = " + method);
				}
				MethodUtils.invokeInterceptor(clazz, method, option, ser, context, helper);
			}
	}
}