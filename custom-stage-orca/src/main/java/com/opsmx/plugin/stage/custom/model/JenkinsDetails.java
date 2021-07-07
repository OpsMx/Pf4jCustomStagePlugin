package com.opsmx.plugin.stage.custom.model;

public class JenkinsDetails {

	public JenkinsDetails(){}

	public JenkinsDetails(String jobName, String buildNo, String artifact) {
		super();
		this.jobName = jobName;
		this.buildNo = buildNo;
		this.artifact = artifact;
	}

	private String jobName;

	private String buildNo;

	private String artifact;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getBuildNo() {
		return buildNo;
	}

	public void setBuildNo(String buildNo) {
		this.buildNo = buildNo;
	}

	public String getArtifact() {
		return artifact;
	}

	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}

}
