package com.opsmx.plugin.stage.custom;

import java.util.List;

public class VisibilityApprovalContext {
	
    public VisibilityApprovalContext(String gateUrl, String appScanProjectId, String aquaWaveImageId, String autopilotCanaryId,
			String gateName, String imageIds, String jiraId, String refId, String sonarqubeProjectKey,
			List<GitDetails> git, List<JenkinsDetails> jenkins, List<CustomConnector> customConnector) {
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
    
          
    public class GitDetails {
    	
    	public GitDetails(String gitRepo, String gitCommitId) {
			super();
			this.gitRepo = gitRepo;
			this.gitCommitId = gitCommitId;
		}

		private String gitRepo;
    	
    	private String gitCommitId;

		public String getGitRepo() {
			return gitRepo;
		}

		public void setGitRepo(String gitRepo) {
			this.gitRepo = gitRepo;
		}

		public String getGitCommitId() {
			return gitCommitId;
		}

		public void setGitCommitId(String gitCommitId) {
			this.gitCommitId = gitCommitId;
		}
    }
    
    public class JenkinsDetails {

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
    
    public class CustomConnector {

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
}
