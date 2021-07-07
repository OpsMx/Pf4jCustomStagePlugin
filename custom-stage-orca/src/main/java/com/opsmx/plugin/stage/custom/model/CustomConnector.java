package com.opsmx.plugin.stage.custom.model;

public class CustomConnector {

	public CustomConnector(){}

	public CustomConnector(String name, String header, String data) {
		super();
		this.name = name;
		this.header = header;
		this.data = data;
	}

	private String name;

	private String header;

	private String data;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
