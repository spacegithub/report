package com.ruisi.ext.engine.init;

import com.ruisi.ext.engine.view.context.Element;
import com.ruisi.ext.engine.view.context.face.Template;
import com.ruisi.ext.engine.view.exception.ExtRuntimeException;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import javax.servlet.ServletContext;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class TemplateManager {
	private ServletContext ctx;
	private static TemplateManager manager;
	private static int number = 0;

	private synchronized int next() {
		if (number > 1000)
			number = 0;
		else {
			number += 1;
		}
		return number;
	}

	private TemplateManager(ServletContext ctx) {
		this.ctx = ctx;
	}

	public static void initTemplate(ServletContext ctx) {
		manager = new TemplateManager(ctx);
	}

	public static TemplateManager getInstance() {
		if (manager == null) {
			throw new ExtRuntimeException("未初始化模板对象.");
		}
		return manager;
	}

	public static String buildTempldate(String templateName, ExtRequest request, ExtEnvirContext ctx)
			throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		StringWriter sw = new StringWriter();
		Velocity.mergeTemplate(templateName, "UTF-8", (Context) ctx, sw);
		return sw.toString();
	}

	public String createTemplate(String input) throws IOException {
		String param = this.ctx.getInitParameter("velocityResPath");
		String path = "";
		if ((param != null) && ("tomcatTempDir".equals(param))) {
			path = System.getProperty("java.io.tmpdir");
			path = path + "/" + this.ctx.getServletContextName() + "/" + "WEB-INF/ext-temp/";
		} else {
			path = this.ctx.getRealPath("/") + "WEB-INF/ext-temp/";
		}

		File f = new File(path);
		if (!(f.exists())) {
			f.mkdirs();
		}

		String fileName = "";
		fileName = next() + ".mv";
		path = path + fileName;

		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
		out.write(input);
		out.close();

		return fileName;
	}

	public void deleteTmp(Element ele) {
		String path = this.ctx.getRealPath("/") + "WEB-INF/ext-temp/";
		synchronized (this) {
			delete(ele, path);
		}
	}

	public String getTemplate(String templateName) throws IOException {
		String path = this.ctx.getRealPath("/") + "WEB-INF/ext-temp/";
		String file = path + templateName;
		return FileUtils.readFileToString(new File(file), "UTF-8");
	}

	private void delete(Element ele, String path) {
		String file;
		if (ele instanceof Template) {
			String name = ((Template) ele).getTemplateName();
			file = path + name;
			new File(file).delete();
		}
		if (ele.getChildren() == null) {
			return;
		}
		for (Element tmp : ele.getChildren())
			delete(tmp, path);
	}
}