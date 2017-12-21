package com.ruisi.ext.engine.view.builder;

import com.ruisi.ext.engine.control.InputOption;
import com.ruisi.ext.engine.control.InputOptionFactory;
import com.ruisi.ext.engine.dao.DaoHelper;
import com.ruisi.ext.engine.init.ExtEnvirContext;
import com.ruisi.ext.engine.init.ExtEnvirContextImpl;
import com.ruisi.ext.engine.util.DaoUtils;
import com.ruisi.ext.engine.view.context.Element;
import com.ruisi.ext.engine.view.context.ExtContext;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.context.face.RefChecker;
import com.ruisi.ext.engine.view.emitter.ContextEmitter;
import com.ruisi.ext.engine.view.emitter.html.HTMLEmitter;
import com.ruisi.ext.engine.view.exception.AuthException;
import com.ruisi.ext.engine.view.exception.ExtRuntimeException;
import com.ruisi.ext.engine.wrapper.ByteArrayWriterImpl;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import com.ruisi.ext.engine.wrapper.ExtResponse;
import com.ruisi.ext.engine.wrapper.ExtSession;
import com.ruisi.ext.engine.wrapper.ExtWriter;
import com.ruisi.ext.engine.wrapper.OutputStreamWriterImpl;
import com.ruisi.ext.engine.wrapper.TestRequestImpl;
import com.ruisi.ext.engine.wrapper.TestResponseImpl;
import com.ruisi.ext.engine.wrapper.TestSessionImpl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;

public class BuilderManager {
	private static Log log = LogFactory.getLog(BuilderManager.class);

	public static void buildContextByEmitter(Element ele, ExtRequest request, ExtEnvirContext context,
			ExtResponse response, DaoHelper daoHelper, ContextEmitter emitter, InputOption option, ServletContext sctx)
			throws AuthException, Exception {
		PageBuilder pb = null;
		try {
			pb = new PageBuilder(ele);
			pb.request = request;
			pb.veloContext = context;
			pb.response = response;
			pb.daoHelper = daoHelper;
			pb.emitter = emitter;
			pb.option = option;
			pb.servletContext = sctx;
			pb.processStart();
			process(ele, request, emitter, daoHelper, context, response, option, sctx);
			pb.processEnd();
		} catch (AuthException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("构建界面出错.", ex);
			throw ex;
		} finally {
			if ((pb != null) && (pb.getCt() != null))
				Context.exit();
		}
	}

	public static void buildContext(Element ele, ExtRequest request, ExtWriter writer, DaoHelper daoHelper,
			ExtEnvirContext context, ExtResponse response, InputOption option, ServletContext sctx)
			throws AuthException, Exception {
		PageBuilder pb = null;
		try {
			ContextEmitter emitter = new HTMLEmitter();
			emitter.initialize(writer, request, response, context, option, sctx);
			pb = new PageBuilder(ele);
			pb.request = request;
			pb.veloContext = context;
			pb.response = response;
			pb.daoHelper = daoHelper;
			pb.emitter = emitter;
			pb.option = option;
			pb.servletContext = sctx;
			pb.processStart();
			process(ele, request, emitter, daoHelper, context, response, option, sctx);
			pb.processEnd();
		} catch (AuthException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("构建界面出错.", ex);
			throw ex;
		} finally {
			if ((pb != null) && (pb.getCt() != null))
				Context.exit();
		}
	}

	public static void buildContext(String mvId, ExtRequest request, ExtWriter writer, DaoHelper daoHelper,
			ExtEnvirContext context, ExtResponse response, InputOption option, ServletContext sctx)
			throws AuthException, Exception {
		try {
			MVContext mv = ExtContext.getInstance().getMVContext(mvId);
			buildContext(mv, request, writer, daoHelper, context, response, option, sctx);
		} catch (AuthException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("构建界面出错.", ex);
			throw ex;
		}
	}

	/** @deprecated */
	public static void rebuild(Element element, ExtRequest request, ContextEmitter emitter, DaoHelper daoHelper,
			ExtEnvirContext ctx, ExtResponse response, InputOption option, ServletContext context) throws Exception {
		AbstractBuilder builder = element.createBuilder();
		if (!(builder instanceof Rebuilder)) {
			throw new ExtRuntimeException("对象不具有rebuild功能.");
		}
		builder.emitter = emitter;
		builder.request = request;
		builder.daoHelper = daoHelper;
		builder.veloContext = ctx;
		builder.response = response;
		builder.option = option;
		builder.servletContext = context;
		Rebuilder rb = (Rebuilder) builder;
		rb.rebuild();
	}

	private static void process(Element element, ExtRequest request, ContextEmitter emitter, DaoHelper daoHelper,
			ExtEnvirContext ctx, ExtResponse response, InputOption option, ServletContext context) throws Exception {
		AbstractBuilder builder = element.createBuilder();
		builder.emitter = emitter;
		builder.request = request;
		builder.daoHelper = daoHelper;
		builder.veloContext = ctx;
		builder.response = response;
		builder.option = option;
		builder.servletContext = context;
		builder.processStart();
		if (element.getChildren() != null) {
			if (element.isGoOn()) {
				for (Element temp : element.getChildren()) {
					process(temp, request, emitter, daoHelper, ctx, response, option, context);
				}
			} else {
				element.setIsGoOn(Boolean.valueOf(true));
			}
		}
		builder.processEnd();
	}

	public static void processAllowBuilder(Element element, Element target, ExtRequest request, ContextEmitter emitter,
			DaoHelper daoHelper, ExtEnvirContext ctx, ExtResponse response, InputOption option, ServletContext sctx)
			throws AuthException, Exception {
		PageBuilder pb = null;
		try {
			pb = new PageBuilder(element);
			pb.request = request;
			pb.veloContext = ctx;
			pb.response = response;
			pb.daoHelper = daoHelper;
			pb.emitter = emitter;
			pb.option = option;
			pb.servletContext = sctx;
			pb.processStart();
			procAllow(element, target, request, emitter, daoHelper, ctx, response, option, sctx);
			pb.processEnd();
		} catch (AuthException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("构建界面出错.", ex);
			throw ex;
		} finally {
			if ((pb != null) && (pb.getCt() != null))
				Context.exit();
		}
	}

	private static void procAllow(Element element, Element target, ExtRequest request, ContextEmitter emitter,
			DaoHelper daoHelper, ExtEnvirContext ctx, ExtResponse response, InputOption option, ServletContext sctx)
			throws Exception {
		AbstractBuilder builder = element.createBuilder();
		builder.emitter = emitter;
		builder.request = request;
		builder.daoHelper = daoHelper;
		builder.veloContext = ctx;
		builder.response = response;
		builder.option = option;
		builder.servletContext = sctx;
		if ((builder instanceof DataBuilder) || (element == target)) {
			builder.processStart();
		}
		if (element.getChildren() != null) {
			for (Element temp : element.getChildren()) {
				procAllow(temp, target, request, emitter, daoHelper, ctx, response, option, sctx);
			}
		}
		if ((builder instanceof DataBuilder) || (element == target))
			builder.processEnd();
	}

	public static void refCheckProcess(Element ele) throws Exception {
		if (ele instanceof RefChecker) {
			RefChecker check = (RefChecker) ele;
			check.check();
		}
		if (ele.getChildren() != null)
			for (Element tmp : ele.getChildren())
				refCheckProcess(tmp);
	}

	public static String buildContext2String(Element mv, ExtRequest req, ExtEnvirContext envirCtx, ExtResponse response,
			DaoHelper dao, ContextEmitter emitter, InputOption option, ServletContext sctx) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ExtWriter writer = new ByteArrayWriterImpl(bout);
		boolean err = false;
		String errInfo = "";
		try {
			emitter.initialize(writer, req, response, envirCtx, option, sctx);
			buildContextByEmitter(mv, req, envirCtx, response, dao, emitter, option, sctx);
		} catch (AuthException ex) {
			err = true;
			errInfo = ex.getMessage();
		} catch (ExtRuntimeException e) {
			err = true;
			errInfo = e.getMessage();
			e.printStackTrace();
		} catch (Exception e) {
			err = true;
			errInfo = e.getMessage();
			e.printStackTrace();
		}
		if (!(err)) {
			String str = new String(bout.toByteArray());
			try {
				bout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return str;
		}
		return " <ul><h4>出错了！ 错误信息 </h4><li>" + errInfo + "</li></ul>";
	}

	public static String buildContextNoHttp(String mvId, Map extParams, ContextEmitter emitter, ServletContext ctx) {
		try {
			MVContext mv = ExtContext.getInstance().getMVContext(mvId, true);

			ExtSession s = new TestSessionImpl();
			ExtRequest req = new TestRequestImpl(extParams, s);
			ExtResponse resp = new TestResponseImpl();

			DaoHelper dao = DaoUtils.getDaoHelper(ctx);

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ExtWriter out = new OutputStreamWriterImpl(bout);

			Map params = ExtContext.getInstance().getParams(mvId);

			InputOption option = InputOptionFactory.createInputOption(req, resp, params);

			ExtEnvirContext ec = new ExtEnvirContextImpl(option);

			req.setAttribute("ext.view.mvid", mvId);

			emitter.initialize(out, req, resp, ec, option, ctx);

			buildContextByEmitter(mv, req, ec, resp, dao, emitter, option, ctx);
			String str = new String(bout.toByteArray());
			return str;
		} catch (AuthException ex) {
			log.error("License 不存在或已失效或超出最大访问限制。");
			return null;
		} catch (Exception ex) {
			log.error("执行NoHttp请求出错.", ex);
			throw new ExtRuntimeException(ex);
		}
	}
}