package com.ruisi.ext.engine.view.builder;

import com.ruisi.ext.engine.ConstantsEngine;
import com.ruisi.ext.engine.init.ExtEnvirContext;
import com.ruisi.ext.engine.view.context.Element;
import com.ruisi.ext.engine.view.context.ExtContext;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.emitter.ContextEmitter;
import com.ruisi.ext.engine.view.exception.ExtConfigException;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import java.io.IOException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class PageBuilder extends AbstractBuilder {
	private Element ele;
	private Context ct;
	private BuilderInterceptor interceptor;

	public PageBuilder(Element ele) throws Exception {
		this.ele = ele;
		String buildInter = ExtContext.getInstance().getConstant("buildInter");
		if ((buildInter == null) || (buildInter.length() <= 0))
			return;
		try {
			this.interceptor = ((BuilderInterceptor) Class.forName(buildInter).newInstance());
		} catch (InstantiationException e) {
			throw e;
		} catch (IllegalAccessException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			String err = ConstantsEngine.replace("builder 拦截器类 $0 不存在.", buildInter);
			throw new ExtConfigException(err, e);
		}
	}

	protected void processEnd() throws IOException {
		this.emitter.end(this.ele);
		if (this.interceptor != null)
			this.interceptor.end(this.ele, this.request, this.daoHelper);
	}

	protected void processStart() throws Exception {
		if (this.interceptor != null) {
			this.interceptor.start(this.ele, this.request, this.daoHelper);
		}
		this.emitter.start(this.ele);

		if (!(this.ele instanceof MVContext))
			return;
		this.veloContext.put("mvInfo", this.ele);
		this.request.setAttribute("mvInfo", this.ele);

		MVContext mvo = (MVContext) this.ele;
		String script = mvo.getScripts();
		if ((script != null) && (script.length() > 0)) {
			this.ct = Context.enter();
			Scriptable scope = this.ct.initStandardObjects();

			Object out = Context.javaToJS(this.emitter.getWriter(), scope);
			ScriptableObject.putProperty(scope, "out", out);
			Object request = Context.javaToJS(this.request, scope);
			ScriptableObject.putProperty(scope, "request", request);
			Object extContext = Context.javaToJS(this.veloContext, scope);
			ScriptableObject.putProperty(scope, "extContext", extContext);
			this.ct.evaluateString(scope, script, null, 1, null);

			Object obj = new JSObject(this.ct, scope);
			this.request.setAttribute("ext.script.engine", obj);
		}
	}

	public Context getCt() {
		return this.ct;
	}

	public static class JSObject {
		private Context ct;
		private Scriptable scope;

		public JSObject(Context ct, Scriptable scope) {
			this.ct = ct;
			this.scope = scope;
		}

		public Context getCt() {
			return this.ct;
		}

		public Scriptable getScope() {
			return this.scope;
		}
	}
}