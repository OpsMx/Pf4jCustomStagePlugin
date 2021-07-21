package com.opsmx.plugin.stage.custom;

import java.util.List;

import com.opsmx.plugin.stage.custom.model.Bamboo;
import com.opsmx.plugin.stage.custom.model.CustomConnector;
import com.opsmx.plugin.stage.custom.model.GitDetails;
import com.opsmx.plugin.stage.custom.model.JenkinsDetails;

public class VisibilityApprovalContext {

	public VisibilityApprovalContext() {}

	private String gateUrl;
	private String appScanProjectId;
	private String aquaWaveImageId;
	private String autopilotCanaryId;
	private String gateName;
	private String imageIds;
	private String jiraId;
	private String refId;
	private String sonarqubeProjectKey;
	private List<GitDetails> git;
	private List<JenkinsDetails> jenkins;
	private List<CustomConnector> customConnector;
	private List<Bamboo> bamboo;
	private String jfrogWatchName;

	public VisibilityApprovalContext(String gateUrl, String appScanProjectId, String aquaWaveImageId,
			String autopilotCanaryId, String gateName, String imageIds, String jiraId, String refId,
			String sonarqubeProjectKey, List<GitDetails> git, List<JenkinsDetails> jenkins,
			List<CustomConnector> customConnector, List<Bamboo> bamboo, String jfrogWatchName) {
		super();
		this.gateUrl = gateUrl;
		this.appScanProjectId = appScanProjectId;
		this.aquaWaveImageId = aquaWaveImageId;
		this.autopilotCanaryId = autopilotCanaryId;
		this.gateName = gateName;
		this.imageIds = imageIds;
		this.jiraId = jiraId;
		this.refId = refId;
		this.sonarqubeProjectKey = sonarqubeProjectKey;
		this.git = git;
		this.jenkins = jenkins;
		this.customConnector = customConnector;
		this.bamboo = bamboo;
		this.jfrogWatchName = jfrogWatchName;
	}

	public String getGateUrl() {
		return gateUrl;
	}

	public void setGateUrl(String gateUrl) {
		this.gateUrl = gateUrl;
	}

	public String getAppScanProjectId() {
		return appScanProjectId;
	}

	public void setAppScanProjectId(String appScanProjectId) {
		this.appScanProjectId = appScanProjectId;
	}

	public String getAquaWaveImageId() {
		return aquaWaveImageId;
	}

	public void setAquaWaveImageId(String aquaWaveImageId) {
		this.aquaWaveImageId = aquaWaveImageId;
	}

	public String getAutopilotCanaryId() {
		return autopilotCanaryId;
	}

	public void setAutopilotCanaryId(String autopilotCanaryId) {
		this.autopilotCanaryId = autopilotCanaryId;
	}

	public String getGateName() {
		return gateName;
	}

	public void setGateName(String gateName) {
		this.gateName = gateName;
	}

	public String getImageIds() {
		return imageIds;
	}

	public void setImageIds(String imageIds) {
		this.imageIds = imageIds;
	}

	public String getJiraId() {
		return jiraId;
	}

	public void setJiraId(String jiraId) {
		this.jiraId = jiraId;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getSonarqubeProjectKey() {
		return sonarqubeProjectKey;
	}

	public void setSonarqubeProjectKey(String sonarqubeProjectKey) {
		this.sonarqubeProjectKey = sonarqubeProjectKey;
	}

	public List<GitDetails> getGit() {
		return git;
	}

	public void setGit(List<GitDetails> git) {
		this.git = git;
	}

	public List<JenkinsDetails> getJenkins() {
		return jenkins;
	}

	public void setJenkins(List<JenkinsDetails> jenkins) {
		this.jenkins = jenkins;
	}

	public List<CustomConnector> getCustomConnector() {
		return customConnector;
	}

	public void setCustomConnector(List<CustomConnector> customConnector) {
		this.customConnector = customConnector;
	}

	public List<Bamboo> getBamboo() {
		return bamboo;
	}

	public void setBamboo(List<Bamboo> bamboo) {
		this.bamboo = bamboo;
	}

	public String getJfrogWatchName() {
		return jfrogWatchName;
	}

	public void setJfrogWatchName(String jfrogWatchName) {
		this.jfrogWatchName = jfrogWatchName;
	}
}
