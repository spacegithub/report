package com.ruisi.ext.engine.view.context;

import com.ruisi.ext.engine.view.builder.AbstractBuilder;
import com.ruisi.ext.engine.view.builder.MVBuilder;
import com.ruisi.ext.engine.view.context.chart.ChartContext;
import com.ruisi.ext.engine.view.context.cross.CrossReportContext;
import com.ruisi.ext.engine.view.context.dc.cube.DataCenterContext;
import com.ruisi.ext.engine.view.context.dc.grid.GridDataCenterContext;
import com.ruisi.ext.engine.view.context.dsource.DataSourceContext;
import com.ruisi.ext.engine.view.context.grid.DataGridContext;
import com.ruisi.ext.engine.view.context.gridreport.GridReportContext;
import com.ruisi.ext.engine.view.context.html.SubmitCheckContext;
import java.util.Map;

public class MVContextImpl extends AbstractContext implements MVContext {
	private String mvid;
	private String formId;
	private boolean showForm = false;

	private boolean upload = false;

	private boolean fromRef = false;
	private Map<String, String> sqls;
	private Map<String, ChartContext> charts;
	private Map<String, GridReportContext> grids;
	private Map<String, DataGridContext> dataGrids;
	private Map<String, GridDataCenterContext> gridDataCenters;
	private SubmitCheckContext submitCheck;
	private Map<String, CrossReportContext> crossReports;
	private String scripts;
	private Map<String, DataSourceContext> dsources;
	private Map<String, DataCenterContext> dataCenters;

	public Map<String, DataCenterContext> getCubeDataCenters() {
		return this.dataCenters;
	}

	public void setCubeDataCenters(Map<String, DataCenterContext> dataCenters) {
		this.dataCenters = dataCenters;
	}

	public String getMvid() {
		return this.mvid;
	}

	public void setMvid(String mvid) {
		this.mvid = mvid;
	}

	public AbstractBuilder createBuilder() {
		return new MVBuilder(this);
	}

	public String getFormId() {
		return this.formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public boolean isShowForm() {
		return this.showForm;
	}

	public void setShowForm(boolean showForm) {
		this.showForm = showForm;
	}

	public boolean isUpload() {
		return this.upload;
	}

	public void setUpload(boolean upload) {
		this.upload = upload;
	}

	public Map<String, String> getSqls() {
		return this.sqls;
	}

	public void setSqls(Map<String, String> sqls) {
		this.sqls = sqls;
	}

	public Map<String, ChartContext> getCharts() {
		return this.charts;
	}

	public void setCharts(Map<String, ChartContext> charts) {
		this.charts = charts;
	}

	public SubmitCheckContext getSubmitCheck() {
		return this.submitCheck;
	}

	public void setSubmitCheck(SubmitCheckContext submitCheck) {
		this.submitCheck = submitCheck;
	}

	public Map<String, DataGridContext> getDataGrids() {
		return this.dataGrids;
	}

	public void setDataGrids(Map<String, DataGridContext> dataGrids) {
		this.dataGrids = dataGrids;
	}

	public boolean isFromRef() {
		return this.fromRef;
	}

	public void setFromRef(boolean fromRef) {
		this.fromRef = fromRef;
	}

	public String getScripts() {
		return this.scripts;
	}

	public void setScripts(String scripts) {
		this.scripts = scripts;
	}

	public Map<String, CrossReportContext> getCrossReports() {
		return this.crossReports;
	}

	public void setCrossReports(Map<String, CrossReportContext> crossReports) {
		this.crossReports = crossReports;
	}

	public Map<String, GridReportContext> getGridReports() {
		return this.grids;
	}

	public void setGridReports(Map<String, GridReportContext> grids) {
		this.grids = grids;
	}

	public Map<String, GridDataCenterContext> getGridDataCenters() {
		return this.gridDataCenters;
	}

	public Map<String, DataSourceContext> getDsources() {
		return this.dsources;
	}

	public void setDsources(Map<String, DataSourceContext> dsources) {
		this.dsources = dsources;
	}

	public void setGridDataCenters(Map<String, GridDataCenterContext> gridDataCenters) {
		this.gridDataCenters = gridDataCenters;
	}
}