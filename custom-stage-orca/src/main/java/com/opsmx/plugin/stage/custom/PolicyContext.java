package com.opsmx.plugin.stage.custom;

public class PolicyContext {
	
	private String policyProxy;
    
    private String policyPath;
    
    private String gateName;
    
    private String imageIDs;

    private String payload;

    public PolicyContext(){}
	
	public PolicyContext(String policyProxy, String policyPath, String gateName, String imageIDs, String payload) {
		super();
		this.policyProxy = policyProxy;
		this.policyPath = policyPath;
		this.gateName = gateName;
		this.imageIDs = imageIDs;
		this.payload = payload;
	}

    public String getPolicyProxy() {
		return policyProxy;
	}

	public void setPolicyProxy(String policyProxy) {
		this.policyProxy = policyProxy;
	}

	public String getPolicyPath() {
		return policyPath;
	}

	public void setPolicyPath(String policyPath) {
		this.policyPath = policyPath;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}

	public String getImageIDs() {
		return imageIDs;
	}

	public void setImageIDs(String imageIDs) {
		this.imageIDs = imageIDs;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
