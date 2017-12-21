package com.ruisi.ext.engine.view.builder.grid;

import com.ruisi.ext.engine.dao.DaoHelper;
import com.ruisi.ext.engine.init.TemplateManager;
import com.ruisi.ext.engine.util.DaoUtils;
import com.ruisi.ext.engine.util.RuleUtils;
import com.ruisi.ext.engine.view.builder.AbstractBuilder;
import com.ruisi.ext.engine.view.builder.dc.GridDataCenterBuilder;
import com.ruisi.ext.engine.view.builder.dsource.DataSourceBuilder;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.context.dc.grid.GridDataCenterContext;
import com.ruisi.ext.engine.view.context.dsource.DataSourceContext;
import com.ruisi.ext.engine.view.context.grid.PageInfo;
import com.ruisi.ext.engine.view.context.gridreport.GridReportContext;
import com.ruisi.ext.engine.view.emitter.ContextEmitter;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import java.util.List;
import java.util.Map;

public class GridReportBuilder extends AbstractBuilder {
	private GridReportContext grid;

	public GridReportBuilder(GridReportContext grid) {
		this.grid = grid;
	}

	protected void processEnd() {
	}

	protected void processStart() throws Exception {
		GridReportContext copy = (GridReportContext) this.grid.clone();
		String dc = this.grid.getRefDataCenter();
		if ((dc != null) && (dc.length() > 0)) {
			List datas = getDataSetFromDataCenter(copy);
			copy.setOptions(datas);
			this.emitter.startGridReport(copy);
			return;
		}

		String exporting = (String) this.request.getAttribute("ext.isExport");
		if (exporting == null) {
			exporting = "";
		}

		List datas = null;
		String sql = TemplateManager.buildTempldate(this.grid.getTemplateName(), this.request, this.veloContext);
		PageInfo page = copy.getPageInfo();
		if ((page != null) && (!("true".equals(exporting)))) {
			PageInfo newPage = new PageInfo();
			newPage.setPagesize(page.getPagesize());
			String cpage = this.request.getParameter("currPage");
			if ((cpage == null) || (cpage.length() == 0))
				newPage.setCurtpage(0L);
			else {
				newPage.setCurtpage(Long.parseLong(cpage));
			}
			if ((this.grid.getRefDsource() == null) || (this.grid.getRefDsource().length() == 0)) {
				datas = DaoUtils.calPage(this.request, sql, newPage, this.daoHelper);
			} else {
				DataSourceBuilder dsb = new DataSourceBuilder();
				MVContext mv = RuleUtils.findCurMV(this.veloContext);
				DataSourceContext ds = dsb.findDataSource(this.grid.getRefDsource(), mv);
				datas = new DataSourceBuilder().queryForList(sql, ds, newPage, this.request);
			}
			copy.setPageInfo(newPage);
		} else if ((this.grid.getRefDsource() == null) || (this.grid.getRefDsource().length() == 0)) {
			datas = this.daoHelper.queryForList(sql);
		} else {
			DataSourceBuilder dsb = new DataSourceBuilder();
			MVContext mv = RuleUtils.findCurMV(this.veloContext);
			DataSourceContext ds = dsb.findDataSource(this.grid.getRefDsource(), mv);
			datas = new DataSourceBuilder().queryForList(sql, ds);
		}

		copy.setOptions(datas);
		this.emitter.startGridReport(copy);
	}

	private List getDataSetFromDataCenter(GridReportContext copy) throws Exception {
		MVContext mv = RuleUtils.findCurMV(this.veloContext);
		GridDataCenterContext dcctx = (GridDataCenterContext) mv.getGridDataCenters().get(this.grid.getRefDataCenter());
		GridDataCenterBuilder dc = new GridDataCenterBuilder(dcctx, this.request, this.veloContext, this.daoHelper);
		if (this.grid.getPageInfo() != null) {
			PageInfo newPage = new PageInfo();
			newPage.setPagesize(this.grid.getPageInfo().getPagesize());
			String cpage = this.request.getParameter("currPage");
			if ((cpage == null) || (cpage.length() == 0))
				newPage.setCurtpage(0L);
			else {
				newPage.setCurtpage(Long.parseLong(cpage));
			}
			List datas = (List) dc.buildByXML(newPage);
			copy.setPageInfo(newPage);
			Map extDatas = dc.getExtDatas();
			copy.setExtDatas(extDatas);
			return datas;
		}
		List datas = (List) dc.buildByXML(null);
		Map extDatas = dc.getExtDatas();
		copy.setExtDatas(extDatas);
		return datas;
	}
}