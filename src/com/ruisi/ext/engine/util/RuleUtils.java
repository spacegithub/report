package com.ruisi.ext.engine.util;

import com.ruisi.ext.engine.init.ExtEnvirContext;
import com.ruisi.ext.engine.view.context.Element;
import com.ruisi.ext.engine.view.context.MVContext;
import com.ruisi.ext.engine.view.context.grid.DataGridContext;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import java.util.Iterator;
import java.util.List;

public class RuleUtils {
	public static MVContext findMVContext(Element input) {
		if (input == null) {
			return null;
		}
		if (input instanceof MVContext) {
			return ((MVContext) input);
		}
		return findMVContext(input.getParent());
	}

	public static boolean isEmpty(List ls) {
		if (ls.isEmpty()) {
			return true;
		}
		boolean ret = true;
		for (Iterator localIterator = ls.iterator(); localIterator.hasNext();) {
			Object obj = localIterator.next();
			if (obj != null) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	public static int theDataGridPos(Element mv) {
		int ret = 0;
		if (mv.getChildren() == null) {
			return ret;
		}
		for (Element ele : mv.getChildren()) {
			if (ele instanceof DataGridContext) {
				++ret;
			}
			ret += theDataGridPos(ele);
		}
		return ret;
	}

	public static MVContext findCurMV(ExtEnvirContext ctx) {
		MVContext mv = (MVContext) ctx.get("mvInfo");
		return mv;
	}

	public static String getResPath(ExtRequest request) {
		String path = (String) request.getAttribute("ext.respath");
		if ((path == null) || (path.length() == 0)) {
			return "../";
		}
		return path;
	}
}