package com.ruisi.ext.engine.view.context.dsource;

import java.util.HashMap;
import java.util.Map;

public class DataSourceContext {
	public static final String[] linkTypes = { "mysql", "oracle", "sqlserver" };

	private Map<String, String> properties = new HashMap<String, String>();

	public String getId() {
		return ((String) this.properties.get("id"));
	}

	public String getLinktype() {
		return ((String) this.properties.get("linktype"));
	}

	public String getLinkname() {
		return ((String) this.properties.get("linkname"));
	}

	public String getLinkpwd() {
		return ((String) this.properties.get("linkpwd"));
	}

	public String getLinkurl() {
		return ((String) this.properties.get("linkurl"));
	}

	public String getJndiname() {
		return ((String) this.properties.get("jndiname"));
	}

	public String getUse() {
		return ((String) this.properties.get("use"));
	}

	public void putProperty(String key, String value) {
		this.properties.put(key, value);
	}
}