package com.ruisi.ext.engine.view.context;

import com.ruisi.ext.engine.view.context.chart.ChartContext;
import com.ruisi.ext.engine.view.context.cross.CrossReportContext;
import com.ruisi.ext.engine.view.context.dc.cube.DataCenterContext;
import com.ruisi.ext.engine.view.context.dc.grid.GridDataCenterContext;
import com.ruisi.ext.engine.view.context.dsource.DataSourceContext;
import com.ruisi.ext.engine.view.context.grid.DataGridContext;
import com.ruisi.ext.engine.view.context.gridreport.GridReportContext;
import com.ruisi.ext.engine.view.context.html.SubmitCheckContext;
import java.util.Map;

public abstract interface MVContext extends Element {
	public abstract String getMvid();

	public abstract void setMvid(String paramString);

	public abstract String getFormId();

	public abstract void setFormId(String paramString);

	public abstract boolean isShowForm();

	public abstract void setShowForm(boolean paramBoolean);

	public abstract void setSubmitCheck(SubmitCheckContext paramSubmitCheckContext);

	public abstract SubmitCheckContext getSubmitCheck();

	public abstract boolean isUpload();

	public abstract void setUpload(boolean paramBoolean);

	public abstract Map<String, String> getSqls();

	public abstract void setSqls(Map<String, String> paramMap);

	public abstract Map<String, ChartContext> getCharts();

	public abstract void setCharts(Map<String, ChartContext> paramMap);

	public abstract Map<String, DataGridContext> getDataGrids();

	public abstract void setDataGrids(Map<String, DataGridContext> paramMap);

	public abstract Map<String, CrossReportContext> getCrossReports();

	public abstract void setCrossReports(Map<String, CrossReportContext> paramMap);

	public abstract Map<String, GridReportContext> getGridReports();

	public abstract void setGridReports(Map<String, GridReportContext> paramMap);

	public abstract boolean isFromRef();

	public abstract void setFromRef(boolean paramBoolean);

	public abstract String getScripts();

	public abstract void setScripts(String paramString);

	public abstract Map<String, DataCenterContext> getCubeDataCenters();

	public abstract void setCubeDataCenters(Map<String, DataCenterContext> paramMap);

	public abstract Map<String, GridDataCenterContext> getGridDataCenters();

	public abstract void setGridDataCenters(Map<String, GridDataCenterContext> paramMap);

	public abstract Map<String, DataSourceContext> getDsources();

	public abstract void setDsources(Map<String, DataSourceContext> paramMap);
}