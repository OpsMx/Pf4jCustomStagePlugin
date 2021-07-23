package com.opsmx.plugin.stage.custom.model;

public class Bamboo {

	private String projectName;

	private String planName;

	private String buildNumber;

	public Bamboo() {
		super();
	}

	public Bamboo(String projectName, String planName, String buildNumber) {
		super();
		this.projectName = projectName;
		this.planName = planName;
		this.buildNumber = buildNumber;
	}
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
}
