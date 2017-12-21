package com.ruisi.ext.engine.view.builder.grid;

import com.ruisi.ext.engine.ConstantsEngine;
import com.ruisi.ext.engine.dao.DaoHelper;
import com.ruisi.ext.engine.init.ExtEnvirContext;
import com.ruisi.ext.engine.init.TemplateManager;
import com.ruisi.ext.engine.util.DaoUtils;
import com.ruisi.ext.engine.util.RuleUtils;
import com.ruisi.ext.engine.view.builder.AbstractBuilder;
import com.ruisi.ext.engine.view.builder.Rebuilder;
import com.ruisi.ext.engine.view.context.ExtContext;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.context.grid.ColConfigContext;
import com.ruisi.ext.engine.view.context.grid.ColsContext;
import com.ruisi.ext.engine.view.context.grid.DataGridContext;
import com.ruisi.ext.engine.view.context.grid.PageInfo;
import com.ruisi.ext.engine.view.emitter.ContextEmitter;
import com.ruisi.ext.engine.view.exception.ExtConfigException;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import java.util.List;
import java.util.Map;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class DataGridBuilder extends AbstractBuilder implements Rebuilder {
	private DataGridContext dataGrid;

	public void rebuild() throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
	}

	public void processEnd() {
	}

	public void processStart() throws Exception {
		DataGridContext copy = (DataGridContext) this.dataGrid.clone();

		if ((this.dataGrid.getDataRef() != null) && (this.dataGrid.getDataRef().length() > 0)) {
			this.emitter.startDataGrid(copy);
			return;
		}

		String exporting = (String) this.request.getAttribute("ext.isExport");
		String fromAjax = (String) this.request.getAttribute("ext_fromAjax");
		if ((!("y".equals(exporting))) && (this.dataGrid.isAjax())
				&& (((fromAjax == null) || ("false".equals(fromAjax))))) {
			this.emitter.startDataGrid(copy);
			return;
		}

		List datas = null;
		String dataId = this.dataGrid.getDataId();
		if ((dataId == null) || (dataId.length() == 0)) {
			String ref = this.dataGrid.getRef();
			String sql;
			if ((ref != null) && (ref.length() > 0)) {
				String mvId = RuleUtils.findCurMV(this.veloContext).getMvid();
				MVContext mv = ExtContext.getInstance().getMVContext(mvId);

				if ((mv.getSqls() == null) || (mv.getSqls().get(ref) == null)) {
					String err = ConstantsEngine.replace("ref 为 $0 的sql不存在, mvId = $1 .", ref, mvId);
					throw new ExtConfigException(err);
				}
				sql = TemplateManager.buildTempldate((String) mv.getSqls().get(ref), this.request, this.veloContext);
			} else {
				sql = TemplateManager.buildTempldate(this.dataGrid.getTemplateName(), this.request, this.veloContext);
			}

			String order = this.request.getParameter("ext_col_order");
			String orderState = this.request.getParameter("ext_order_state");
			if ((order != null) && (order.length() > 0)) {
				sql = "select * from (" + sql + ") order by " + order;
				if ("d".equals(orderState)) {
					sql = sql + " desc";
				}
			}

			if (!("y".equals(exporting))) {
				PageInfo page = this.dataGrid.getPageInfo();
				if (page != null) {
					PageInfo newPage = new PageInfo();
					newPage.setPagesize(page.getPagesize());
					String currPageId = "currpage_";
					if ((this.dataGrid.getPageInputName() != null) && (this.dataGrid.getPageInputName().length() > 0)) {
						currPageId = this.dataGrid.getPageInputName();
					}
					if ("true".equals(fromAjax)) {
						currPageId = "currPage";
					}
					String cpage = this.request.getParameter(currPageId);
					if ((cpage == null) || (cpage.length() == 0))
						newPage.setCurtpage(0L);
					else {
						newPage.setCurtpage(Long.parseLong(cpage));
					}
					datas = DaoUtils.calPage(this.request, sql, newPage, this.daoHelper);
					copy.setPageInfo(newPage);
				} else {
					datas = this.daoHelper.queryForList(sql);
				}
			} else {
				datas = this.daoHelper.queryForList(sql);
			}
		} else {
			datas = (List) this.request.getAttribute(dataId);
			if (datas == null) {
				String err = ConstantsEngine.replace("未在request对象中找到id为 $0 的数据集.", dataId);
				throw new ExtConfigException(err);
			}

			PageInfo page = (PageInfo) this.request.getAttribute("ext.view.pageInfo");
			copy.setPageInfo(page);
		}

		ColConfigContext colConfig = this.dataGrid.getColConfigContext();
		if (colConfig.getColsContext() != null) {
			ColsContext tcols = colConfig.getColsContext();
			List refDatas = (List) this.veloContext.get(tcols.getDataRef());
			if (refDatas != null) {
				String[] aliasArray = new String[refDatas.size()];
				String[] descArray = new String[refDatas.size()];
				for (int i = 0; i < refDatas.size(); ++i) {
					Map m = (Map) refDatas.get(i);
					aliasArray[i] = ((String) m.get(tcols.getAllAlias()));
					descArray[i] = ((String) m.get(tcols.getAllDescs()));
				}
				tcols.setAliasArray(aliasArray);
				tcols.setDescArray(descArray);
			}
		}

		copy.setOptions(datas);

		this.emitter.startDataGrid(copy);
	}

	public DataGridBuilder(DataGridContext dataGrid) {
		this.dataGrid = dataGrid;
	}
}