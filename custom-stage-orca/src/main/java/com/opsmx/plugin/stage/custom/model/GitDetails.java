package com.opsmx.plugin.stage.custom.model;

public class GitDetails {

	public GitDetails(){}
	
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
