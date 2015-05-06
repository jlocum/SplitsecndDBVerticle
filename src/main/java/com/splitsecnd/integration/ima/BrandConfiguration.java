package com.splitsecnd.integration.ima;

import java.io.Serializable;

import org.vertx.java.core.json.JsonObject;

public class BrandConfiguration implements Serializable {
	private boolean isConfigured;
	private String host;
	private boolean ssl;
	private String port;
	private String requestUri;
	
	public void populate(JsonObject config) {
		setConfigured(config.getBoolean("emergencyServiceConfigured").booleanValue());
		setHost(config.getString("host"));
		setSsl(config.getBoolean("ssl").booleanValue());
		setPort(config.getString("port"));
		setRequestUri(config.getString("requestUri"));
	}
	
	public boolean isConfigured() {
		return isConfigured;
	}
	public void setConfigured(boolean isConfigured) {
		this.isConfigured = isConfigured;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public boolean isSsl() {
		return ssl;
	}
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getRequestUri() {
		return requestUri;
	}
	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}
	
}