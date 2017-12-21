package com.ruisi.ext.engine.service.impl;

import com.ruisi.ext.engine.control.ActionProcess;
import com.ruisi.ext.engine.control.InputOption;
import com.ruisi.ext.engine.control.InputOptionFactory;
import com.ruisi.ext.engine.init.ExtEnvirContext;
import com.ruisi.ext.engine.init.ExtEnvirContextImpl;
import com.ruisi.ext.engine.service.ServiceSupport;
import com.ruisi.ext.engine.view.builder.BuilderManager;
import com.ruisi.ext.engine.view.context.ExtContext;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.emitter.ContextEmitter;
import com.ruisi.ext.engine.view.emitter.html.HTMLEmitter;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import com.ruisi.ext.engine.wrapper.ExtResponse;
import com.ruisi.ext.engine.wrapper.ExtWriter;
import com.ruisi.ext.engine.wrapper.ExtWriterImpl;
import java.util.Map;

public class TabAjaxService extends ServiceSupport {
	public void execute(InputOption option) throws Exception {
		option.getResponse().setContentType("text/html;charset=UTF-8");
		ExtRequest request = option.getRequest();
		String mvid = request.getParameter("mvid");

		MVContext mv = ActionProcess.findMVContextById(mvid, false);

		Map inputParams = ExtContext.getInstance().getParams(mvid);
		if (inputParams != null) {
			InputOption option2 = InputOptionFactory.createInputOption(request, option.getResponse(), inputParams);
			option.getParams().putAll(option2.getParams());
		}

		request.setAttribute("ext.view.fromId", mvid);
		request.setAttribute("ext_rebuild", "y");

		ExtEnvirContext ctx = new ExtEnvirContextImpl(option);
		ExtWriter out = new ExtWriterImpl(option.getResponse().getWriter());
		ContextEmitter emitter = new HTMLEmitter();
		emitter.initialize(out, option.getRequest(), option.getResponse(), ctx, option, this.servletContext);
		BuilderManager.buildContext(mv, request, out, this.daoHelper, ctx, option.getResponse(), option,
				this.servletContext);

		super.setNoResult(request);
	}
}