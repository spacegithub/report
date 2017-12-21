package com.ruisi.ext.engine.view.rules.grid;

import com.ruisi.ext.engine.init.TemplateManager;
import com.ruisi.ext.engine.view.context.Element;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.exception.ExtConfigException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public class SqlRule extends Rule {
	private String currID;

	public void begin(String namespace, String name, Attributes attributes) throws Exception {
		String id = attributes.getValue("id");
		Element parent = (Element) this.digester.peek();

		if (!(parent instanceof MVContext)) {
			throw new ExtConfigException("标签 sql 必须配置在xml文件最上级.");
		}

		MVContext ctx = (MVContext) parent;
		Map sqls = ctx.getSqls();
		if (sqls == null) {
			sqls = new HashMap();
			ctx.setSqls(sqls);
		}
		this.currID = id;
	}

	public void body(String namespace, String name, String text) throws Exception {
		if ((text == null) || (text.length() == 0)) {
			return;
		}

		String templateName = TemplateManager.getInstance().createTemplate(text);

		Element parent = (Element) this.digester.peek();
		MVContext ctx = (MVContext) parent;
		ctx.getSqls().put(this.currID, templateName);
	}

	public void end(String namespace, String name) throws Exception {
	}
}