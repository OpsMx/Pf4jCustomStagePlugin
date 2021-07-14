package com.opsmx.plugin.stage.custom;

public class TestVerificationContext {

    private String gateUrl;
    private String lifeTimeHours;
    private Integer minimumCanaryResult;
    private Integer canaryResultScore;
    private String gateName;
    private String imageIds;
    private Boolean logAnalysis;
    private Boolean metricAnalysis;
    private Long baselineStartTime;
    private Long canaryStartTime;
    private String testRunKey;
    private String baselineTestRunId;
    private String newTestRunId;
    private String testRunInfo;
    
    
    public TestVerificationContext(){}


	public TestVerificationContext(String gateUrl, String lifeTimeHours, Integer minimumCanaryResult,
			Integer canaryResultScore, String gateName, String imageIds, Boolean logAnalysis, Boolean metricAnalysis,
			Long baselineStartTime, Long canaryStartTime, String testRunKey, String baselineTestRunId,
			String newTestRunId, String testRunInfo) {
		super();
		this.gateUrl = gateUrl;
		this.lifeTimeHours = lifeTimeHours;
		this.minimumCanaryResult = minimumCanaryResult;
		this.canaryResultScore = canaryResultScore;
		this.gateName = gateName;
		this.imageIds = imageIds;
		this.logAnalysis = logAnalysis;
		this.metricAnalysis = metricAnalysis;
		this.baselineStartTime = baselineStartTime;
		this.canaryStartTime = canaryStartTime;
		this.testRunKey = testRunKey;
		this.baselineTestRunId = baselineTestRunId;
		this.newTestRunId = newTestRunId;
		this.testRunInfo = testRunInfo;
	}


	public String getGateUrl() {
		return gateUrl;
	}


	public void setGateUrl(String gateUrl) {
		this.gateUrl = gateUrl;
	}


	public String getLifeTimeHours() {
		return lifeTimeHours;
	}


	public void setLifeTimeHours(String lifeTimeHours) {
		this.lifeTimeHours = lifeTimeHours;
	}


	public Integer getMinimumCanaryResult() {
		return minimumCanaryResult;
	}


	public void setMinimumCanaryResult(Integer minimumCanaryResult) {
		this.minimumCanaryResult = minimumCanaryResult;
	}


	public Integer getCanaryResultScore() {
		return canaryResultScore;
	}


	public void setCanaryResultScore(Integer canaryResultScore) {
		this.canaryResultScore = canaryResultScore;
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


	public Boolean getLogAnalysis() {
		return logAnalysis;
	}


	public void setLogAnalysis(Boolean logAnalysis) {
		this.logAnalysis = logAnalysis;
	}


	public Boolean getMetricAnalysis() {
		return metricAnalysis;
	}


	public void setMetricAnalysis(Boolean metricAnalysis) {
		this.metricAnalysis = metricAnalysis;
	}


	public Long getBaselineStartTime() {
		return baselineStartTime;
	}


	public void setBaselineStartTime(Long baselineStartTime) {
		this.baselineStartTime = baselineStartTime;
	}


	public Long getCanaryStartTime() {
		return canaryStartTime;
	}


	public void setCanaryStartTime(Long canaryStartTime) {
		this.canaryStartTime = canaryStartTime;
	}


	public String getTestRunKey() {
		return testRunKey;
	}


	public void setTestRunKey(String testRunKey) {
		this.testRunKey = testRunKey;
	}


	public String getBaselineTestRunId() {
		return baselineTestRunId;
	}


	public void setBaselineTestRunId(String baselineTestRunId) {
		this.baselineTestRunId = baselineTestRunId;
	}


	public String getNewTestRunId() {
		return newTestRunId;
	}


	public void setNewTestRunId(String newTestRunId) {
		this.newTestRunId = newTestRunId;
	}


	public String getTestRunInfo() {
		return testRunInfo;
	}


	public void setTestRunInfo(String testRunInfo) {
		this.testRunInfo = testRunInfo;
	}

    
}
