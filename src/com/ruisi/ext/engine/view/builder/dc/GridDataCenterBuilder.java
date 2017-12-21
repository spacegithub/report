package com.ruisi.ext.engine.view.builder.dc;

import com.ruisi.ext.engine.dao.DaoHelper;
import com.ruisi.ext.engine.init.ExtEnvirContext;
import com.ruisi.ext.engine.init.TemplateManager;
import com.ruisi.ext.engine.util.DaoUtils;
import com.ruisi.ext.engine.util.RuleUtils;
import com.ruisi.ext.engine.view.builder.PageBuilder;
import com.ruisi.ext.engine.view.builder.PageBuilder.JSObject;
import com.ruisi.ext.engine.view.builder.dsource.DataSourceBuilder;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.context.dc.grid.GridDataCenterContext;
import com.ruisi.ext.engine.view.context.dc.grid.GridSetConfContext;
import com.ruisi.ext.engine.view.context.dsource.DataSourceContext;
import com.ruisi.ext.engine.view.context.grid.PageInfo;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import com.ruisi.ext.engine.wrapper.ExtWriterPrintStreamImpl;
import com.ruisi.ispire.dc.cube.engine.ScriptEnginerException;
import com.ruisi.ispire.dc.cube.engine.SystemFuncLoader;
import com.ruisi.ispire.dc.cube.operation.ScriptInvoke;
import com.ruisi.ispire.dc.grid.ColumnInfo;
import com.ruisi.ispire.dc.grid.GridBaseProcessor;
import com.ruisi.ispire.dc.grid.GridDataMetaData;
import com.ruisi.ispire.dc.grid.GridProcContext;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class GridDataCenterBuilder {
	private GridDataCenterContext gridDataCenter;
	private ExtRequest request;
	private ExtEnvirContext veloContext;
	private DaoHelper dao;
	private SystemFuncLoader sfunc;
	private Map<String, Object> extDatas = new HashMap();

	private static Logger log = Logger.getLogger(GridDataCenterBuilder.class);

	public Map<String, Object> getExtDatas() {
		return this.extDatas;
	}

	public GridDataCenterBuilder(GridDataCenterContext gridDataCenter, ExtRequest request, ExtEnvirContext veloContext,
			DaoHelper dao) {
		this.gridDataCenter = gridDataCenter;
		this.request = request;
		this.veloContext = veloContext;
		this.dao = dao;
		this.sfunc = new SystemFuncLoader();
	}

	public Object buildByXML(PageInfo page) throws Exception {
		List datas = null;
		String dataKey = this.gridDataCenter.getConf().getDataKey();
		if ((dataKey != null) && (dataKey.length() > 0)) {
			datas = (List) this.request.getAttribute(dataKey);
			if (datas == null) {
				log.error("配置的  dataKey=" + dataKey + " 未获取到数据。");
				return new ArrayList();
			}
		} else {
			String name = this.gridDataCenter.getConf().getTemplateName();
			String refDsource = this.gridDataCenter.getConf().getRefDsource();
			String sql = TemplateManager.buildTempldate(name, this.request, this.veloContext);
			DataSourceBuilder dsb;
			MVContext mv;
			DataSourceContext ds;
			if (page != null) {
				if ((refDsource == null) || (refDsource.length() == 0)) {
					datas = DaoUtils.calPage(this.request, sql, page, this.dao);
				} else {
					dsb = new DataSourceBuilder();
					mv = RuleUtils.findCurMV(this.veloContext);
					ds = dsb.findDataSource(refDsource, mv);
					datas = new DataSourceBuilder().queryForList(sql, ds, page, this.request);
				}
			} else if ((refDsource == null) || (refDsource.length() == 0)) {
				datas = this.dao.queryForList(sql);
			} else {
				dsb = new DataSourceBuilder();
				mv = RuleUtils.findCurMV(this.veloContext);
				ds = dsb.findDataSource(refDsource, mv);
				datas = new DataSourceBuilder().queryForList(sql, ds);
			}
		}

		if (datas.size() == 0) {
			return datas;
		}

		GridDataMetaData md = new GridDataMetaData();
		Map data = (Map) datas.get(0);
		Iterator it = data.keySet().iterator();
		while (it.hasNext()) {
			String key = ((String) it.next()).toUpperCase();
			Object val = data.get(key);

			if (val == null) {
				val = data.get(key.toLowerCase());
			}
			ColumnInfo col = new ColumnInfo();
			if (val == null)
				col.setType("String");
			else if (val instanceof String)
				col.setType("String");
			else if ((val instanceof BigDecimal) || (val instanceof Integer) || (val instanceof Double)
					|| (val instanceof Long))
				col.setType("Number");
			else if (val instanceof Timestamp)
				col.setType("Date");
			else {
				col.setType("None");
			}
			col.setName(key);
			md.getQueryColumns().put(key, col);
		}

		List<GridProcContext> procssCtx = this.gridDataCenter.getProcess();
		List<GridBaseProcessor> procss = new ArrayList<GridBaseProcessor>();
		for (GridProcContext procCtx : procssCtx) {
			GridBaseProcessor proc = procCtx.getProccess();
			proc.init(datas, md, this);
			procss.add(proc);
		}
		if (procss.size() == 0) {
			return datas;
		}

		StringBuffer scripts = new StringBuffer();
		for (GridBaseProcessor proc : procss) {
			if (proc instanceof ScriptInvoke) {
				ScriptInvoke target = (ScriptInvoke) proc;
				scripts.append(target.createJSFunc());
			}
		}
		String script = scripts.toString();
		try {
			GridBaseProcessor proc;
			if ((script != null) && (script.length() > 0)) {
				script = script + this.sfunc.load();
				PageBuilder.JSObject invo = createJSEngine(script);
				for (Iterator localIterator2 = procss.iterator(); localIterator2.hasNext();) {
					proc = (GridBaseProcessor) localIterator2.next();
					if (proc instanceof ScriptInvoke) {
						ScriptInvoke target = (ScriptInvoke) proc;
						target.setInvocable(invo);
					}
				}
			}
			for (int i = 0; i < procss.size(); ++i) {
				proc = (GridBaseProcessor) procss.get(i);
				datas = proc.process();

				if (i + 1 < procss.size())
					((GridBaseProcessor) procss.get(i + 1)).initDatas(datas);
			}
		} finally {
			if ((script != null) && (script.length() > 0)) {
				Context.exit();
			}
		}
		return datas;
	}

	public ExtRequest getRequest() {
		return this.request;
	}

	public ExtEnvirContext getVeloContext() {
		return this.veloContext;
	}

	private PageBuilder.JSObject createJSEngine(String scripts) throws ScriptEnginerException {
		try {
			Context ct = Context.enter();
			Scriptable scope = ct.initStandardObjects();
			Object out = Context.javaToJS(new ExtWriterPrintStreamImpl(System.out), scope);
			ScriptableObject.putProperty(scope, "out", out);
			ct.evaluateString(scope, scripts, null, 1, null);
			return new PageBuilder.JSObject(ct, scope);
		} catch (Exception ex) {
			String err = "JS函数构建错误. \n " + scripts;
			log.error(err, ex);
			throw new ScriptEnginerException(err, ex);
		}
	}
}