package com.opsmx.plugin.stage.custom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.platform.commons.util.CollectionUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonArray;
import com.netflix.spinnaker.kork.plugins.api.PluginComponent;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import com.opsmx.plugin.stage.custom.model.CustomConnector;

@Extension
@PluginComponent
public class VisibilityApprovalTask implements Task {

	private static final String REJECTED = "rejected";

	private static final String APPROVED = "approved";

	private static final String LOCATION = "location";

	private static final String HEADER_DATA = "headerData";

	private static final String DATA = "data";

	private static final String NAME = "name";

	private static final String CUSTOM = "CUSTOM";

	private static final String TOOL_CONNECTOR_PARAMETERS = "toolConnectorParameters";

	private static final String CANARY_ID = "canaryId";

	private static final String AUTOPILOT = "AUTOPILOT";

	private static final String IMAGE_ID = "imageId";

	private static final String AQUAWAVE = "AQUAWAVE";

	private static final String ID = "Id";

	private static final String APPSCAN = "APPSCAN";

	private static final String PROJECT_KEY = "projectKey";

	private static final String SONARQUBE = "SONARQUBE";

	private static final String ARTIFACT = "artifact";

	private static final String BUILD_ID = "buildId";

	private static final String JOB = "job";

	private static final String JENKINS = "JENKINS";

	private static final String REPO = "repo";

	private static final String COMMIT_ID = "commitId";

	private static final String GIT = "GIT";

	private static final String JIRA_TICKET_NO = "jira_ticket_no";

	private static final String VALUE = "value";

	private static final String KEY = "key";

	private static final String PARAMETERS = "parameters";

	private static final String CONNECTOR_TYPE = "connectorType";

	private static final String JIRA = "JIRA";

	private static final String EXCEPTION = "exception";

	private static final String ACTIVATED = "activated";

	public static final String STATUS = "status";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ObjectMapper objectMapper = new ObjectMapper();

	@NotNull
	@Override
	public TaskResult execute(@NotNull StageExecution stage) {

		Map<String, Object> contextMap = new HashMap<>();
		Map<String, Object> outputs = new HashMap<>();
		outputs.put(STATUS, "rejected");

		logger.info(" Visibility approval execution started");
		VisibilityApprovalContext context = stage.mapTo(VisibilityApprovalContext.class);
		if (context.getGateUrl() == null || context.getGateUrl().isEmpty()) {
			logger.info("Gate Url should not be empty");
			outputs.put(EXCEPTION, "Gate Url should not be empty");
			return TaskResult.builder(ExecutionStatus.TERMINAL)
					.context(contextMap)
					.outputs(outputs)
					.build();
		}

		logger.info("gateurl : {}, git : {}", context.getGit());

		logger.info("Application name : {}, pipeline name : {}", stage.getExecution().getApplication(), stage.getExecution().getName());

		try {

			HttpPost request = new HttpPost(context.getGateUrl());
			request.setEntity(new StringEntity(getPayloadString(context)));
			request.setHeader("Content-type", "application/json");

			CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(request);

			HttpEntity entity = response.getEntity();
			String registerResponse = "";
			if (entity != null) {
				registerResponse = EntityUtils.toString(entity);
			}

			if (response.getStatusLine().getStatusCode() != 202) {
				outputs.put(EXCEPTION, registerResponse);
				return TaskResult.builder(ExecutionStatus.TERMINAL)
						.context(contextMap)
						.outputs(outputs)
						.build();
			}

			String approvalUrl = response.getLastHeader(LOCATION).getValue();
			logger.info("Application : {}, Pipeline : {}, Visibility Approval url : {}", stage.getExecution().getApplication(), stage.getExecution().getName(), approvalUrl);
			return getVerificationStatus(approvalUrl);

		} catch (Exception e) {
			logger.error("Failed to execute verification gate", e);
			outputs.put(EXCEPTION, e.getMessage());
		}

		return TaskResult.builder(ExecutionStatus.TERMINAL)
				.context(contextMap)
				.outputs(outputs)
				.build();
	}

	private TaskResult getVerificationStatus(String canaryUrl) {
		HttpGet request = new HttpGet(canaryUrl);

		Map<String, Object> outputs = new HashMap<>();
		String analysisStatus = ACTIVATED;
		while (analysisStatus.equalsIgnoreCase(ACTIVATED)) {
			try {
				request.setHeader("Content-type", "application/json");
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					ObjectNode readValue = objectMapper.readValue(EntityUtils.toString(entity), ObjectNode.class);
					analysisStatus = readValue.get(STATUS).asText();
					if (!analysisStatus.equalsIgnoreCase(ACTIVATED)) {
						Thread.sleep(1000);
						continue;
					}

					logger.info("Visibility approval status : {}", analysisStatus);
					if (analysisStatus.equalsIgnoreCase(APPROVED)) {
						outputs.put(STATUS, analysisStatus);
						return TaskResult.builder(ExecutionStatus.SUCCEEDED)
								.outputs(outputs)
								.build();
					} else if (analysisStatus.equalsIgnoreCase(REJECTED)) {
						outputs.put(STATUS, analysisStatus);
						return TaskResult.builder(ExecutionStatus.TERMINAL)
								.outputs(outputs)
								.build();
					}						
				}

			} catch (Exception e) {
				logger.error("Error occured while getting approval result ", e);
				outputs.put(EXCEPTION, e.getMessage());
			}
		}

		return TaskResult.builder(ExecutionStatus.TERMINAL)
				.outputs(outputs)
				.build();
	}

	private String getPayloadString(VisibilityApprovalContext context) throws Exception {

		ObjectNode finalJson = objectMapper.createObjectNode();
		finalJson.put("approvalCallbackURL", "http://oes-platform:8095/callbackurl");
		finalJson.put("rejectionCallbackURL", "http://oes-platform:8095/rejectionbackurl");

		if (context.getImageIds() != null && !context.getImageIds().isEmpty()) {
			ArrayNode images = objectMapper.createArrayNode();
			Arrays.asList(context.getImageIds().split(",")).forEach(a -> {
				images.add(a.trim());
			});
			finalJson.set("imageIds", images);
		}

		ArrayNode toolConnectorPayloads = objectMapper.createArrayNode();
		if (context.getJiraId() != null && ! context.getJiraId().isEmpty()) {
			ObjectNode jiraObjectNode = objectMapper.createObjectNode();
			jiraObjectNode.put(CONNECTOR_TYPE, JIRA);
			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
			Arrays.asList(context.getJiraId().split(",")).forEach(a -> {
				parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, JIRA_TICKET_NO).put(VALUE, a.trim()));
			});

			jiraObjectNode.set(PARAMETERS, parameterArrayNode);
			toolConnectorPayloads.add(jiraObjectNode);

		} else {
			logger.info("JIRAID is not provided");
		}

		if (context.getGit() != null && ! context.getGit().isEmpty()) {
			ObjectNode gitObjectNode = objectMapper.createObjectNode();
			gitObjectNode.put(CONNECTOR_TYPE, GIT);
			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
			context.getGit().forEach(git -> {
				if (git != null && git.getGitCommitId() != null && !git.getGitCommitId().isEmpty()) {
					if (parameterArrayNode.size() == 0) {
						parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, REPO).put(VALUE, git.getGitRepo()));
					}
					Arrays.asList(git.getGitCommitId().split(",")).forEach(a -> {
						parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, COMMIT_ID).put(VALUE, a.trim()));
					});
				} 
			});
			if (parameterArrayNode != null && parameterArrayNode.size() >= 1) {
				gitObjectNode.set(PARAMETERS, parameterArrayNode);
				toolConnectorPayloads.add(gitObjectNode);
			}
		} else {
			logger.info("Repo is not provided");
		}

		if (context.getJenkins() != null && ! context.getJenkins().isEmpty()) {
			ObjectNode jenkinsObjectNode = objectMapper.createObjectNode();
			jenkinsObjectNode.put(CONNECTOR_TYPE, JENKINS);
			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
			context.getJenkins().forEach(jenkins -> {
				if (jenkins != null && jenkins.getBuildNo() != null && ! jenkins.getBuildNo().isEmpty()) {
					parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, JOB).put(VALUE, jenkins.getJobName()));
					Arrays.asList(jenkins.getBuildNo().split(",")).forEach(a -> {
						parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, BUILD_ID).put(VALUE, a.trim()));
					});

					Arrays.asList(jenkins.getArtifact().split(",")).forEach(a -> {
						parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, ARTIFACT).put(VALUE, a.trim()));
					});
				} 
			});

			if (parameterArrayNode != null && parameterArrayNode.size() >= 1) {
				jenkinsObjectNode.set(PARAMETERS, parameterArrayNode);
				toolConnectorPayloads.add(jenkinsObjectNode);
			}
		} else {
			logger.info("Jenkins job is not provided");
		}

		if (context.getSonarqubeProjectKey() != null && ! context.getSonarqubeProjectKey().isEmpty()) {
			ObjectNode sonarObjectNode = objectMapper.createObjectNode();
			sonarObjectNode.put(CONNECTOR_TYPE, SONARQUBE);
			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
			Arrays.asList(context.getSonarqubeProjectKey().split(",")).forEach(a -> {
				parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, PROJECT_KEY).put(VALUE, a.trim()));
			});

			sonarObjectNode.set(PARAMETERS, parameterArrayNode);
			toolConnectorPayloads.add(sonarObjectNode);

		} else {
			logger.info("Sonarqube projectid is not provided");
		} 

		if (context.getAppScanProjectId() != null && ! context.getAppScanProjectId().isEmpty()) {

			ObjectNode appscanNode = objectMapper.createObjectNode();
			appscanNode.put(CONNECTOR_TYPE, APPSCAN);
			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
			Arrays.asList(context.getAppScanProjectId().split(",")).forEach(a -> {
				parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, ID).put(VALUE, a.trim()));
			});

			appscanNode.set(PARAMETERS, parameterArrayNode);
			toolConnectorPayloads.add(appscanNode);
		} else {
			logger.info("APPSCAN projectid is not provided");
		} 

		if (context.getAquaWaveImageId() != null && ! context.getAquaWaveImageId().isEmpty()) {

			ObjectNode aquaNode = objectMapper.createObjectNode();
			aquaNode.put(CONNECTOR_TYPE, AQUAWAVE);
			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
			Arrays.asList(context.getAquaWaveImageId().split(",")).forEach(a -> {
				parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, IMAGE_ID).put(VALUE, a.trim()));
			});

			aquaNode.set(PARAMETERS, parameterArrayNode);
			toolConnectorPayloads.add(aquaNode);
		} else {
			logger.info("AQUAWAVE image is not provided");
		} 

		if (context.getAutopilotCanaryId() != null && ! context.getAutopilotCanaryId().isEmpty()) {

			ObjectNode aquaNode = objectMapper.createObjectNode();
			aquaNode.put(CONNECTOR_TYPE, AUTOPILOT);
			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
			Arrays.asList(context.getAutopilotCanaryId().split(",")).forEach(a -> {
				parameterArrayNode.add(objectMapper.createObjectNode().put(KEY, CANARY_ID).put(VALUE, a.trim()));
			});

			aquaNode.set(PARAMETERS, parameterArrayNode);
			toolConnectorPayloads.add(aquaNode);
		} else {
			logger.info("AQUAWAVE image is not provided");
		}		

		finalJson.set(TOOL_CONNECTOR_PARAMETERS, toolConnectorPayloads);


		ArrayNode customArrayNode = objectMapper.createArrayNode();
		if (context.getCustomConnector() != null && ! context.getCustomConnector().isEmpty()) {

			for (CustomConnector custom : context.getCustomConnector()) {
				if (custom != null && custom.getHeader() != null && !custom.getHeader().isEmpty()) {
					ObjectNode customNode = objectMapper.createObjectNode();
					customNode.put(CONNECTOR_TYPE, CUSTOM);
					customNode.put(NAME, custom.getName());

					ArrayNode headerNode = objectMapper.createArrayNode();
					Arrays.asList(custom.getHeader().split(",")).forEach(a -> {
						headerNode.add(a.trim());
					});

					customNode.set(HEADER_DATA, headerNode);
					try {
						customNode.set(DATA, objectMapper.readTree(custom.getData()));
					} catch (Exception e) {
						logger.error("please provide valid json data for custom connector", e);
						throw e;
					}
					customArrayNode.add(customNode);
				}
			}

		} else {
			logger.info("CustomConnector is not provided");
		}
		
		finalJson.set("customConnectorData", customArrayNode);

		logger.info("Payload string to visibility approval : {}", finalJson.toString());

		return finalJson.toString();
	}
}
