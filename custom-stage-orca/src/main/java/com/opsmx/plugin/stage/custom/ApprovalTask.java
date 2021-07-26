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
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.netflix.spinnaker.kork.plugins.api.PluginComponent;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import com.opsmx.plugin.stage.custom.model.CustomConnector;

@Extension
@PluginComponent
public class ApprovalTask implements Task {

	private static final String GATE_URL = "gateUrl";

	private static final String VALUES = "values";

	private static final String BUILD_NUMBER = "buildNumber";

	private static final String PLAN_NAME = "planName";

	private static final String PROJECT_NAME = "projectName";

	private static final String WATCH_NAME = "watch_name";

	private static final String JFROG = "JFROG";

	private static final String BAMBOO = "BAMBOO";

	private static final String REPORT_ID = "reportId";

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
		outputs.put(STATUS, REJECTED);

		logger.info(" Visibility approval execution started");

		try {
			
			Map<String, Object> jsonContext = (Map<String, Object>) stage.getContext().get("parameters");

			if (jsonContext.get(GATE_URL) == null || ((String) jsonContext.get(GATE_URL)).isEmpty()) {
				logger.info("Gate Url should not be empty");
				outputs.put(EXCEPTION, "Gate Url should not be empty");
				return TaskResult.builder(ExecutionStatus.TERMINAL)
						.context(contextMap)
						.outputs(outputs)
						.build();
			}
			
			String gateUrl = (String) jsonContext.get(GATE_URL);
			logger.info("Application name : {}, pipeline name : {}", stage.getExecution().getApplication(), stage.getExecution().getName());

			HttpPost request = new HttpPost(gateUrl);
			request.setEntity(new StringEntity(preparePayload(jsonContext, stage.getExecution().getId())));
			request.setHeader("Content-type", "application/json");
			request.setHeader("x-spinnaker-user", stage.getExecution().getAuthentication().getUser());

			CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(request);

			HttpEntity entity = response.getEntity();
			String registerResponse = "";
			if (entity != null) {
				registerResponse = EntityUtils.toString(entity);
			}

			logger.info("visibility approval trigger response : {}", registerResponse);

			if (response.getStatusLine().getStatusCode() != 202) {
				outputs.put(EXCEPTION, registerResponse);
				return TaskResult.builder(ExecutionStatus.TERMINAL)
						.context(contextMap)
						.outputs(outputs)
						.build();
			}

			String approvalUrl = response.getLastHeader(LOCATION).getValue();
			logger.info("Application : {}, Pipeline : {}, Visibility Approval url : {}", stage.getExecution().getApplication(),
					stage.getExecution().getName(), approvalUrl);
			return getVerificationStatus(approvalUrl, stage.getExecution().getAuthentication().getUser());

		} catch (Exception e) {
			logger.error("Failed to execute verification gate", e);
			outputs.put(EXCEPTION, e);
		} 
		return TaskResult.builder(ExecutionStatus.TERMINAL)
				.context(contextMap)
				.outputs(outputs)
				.build();
	}

	private TaskResult getVerificationStatus(String canaryUrl, String user) {
		HttpGet request = new HttpGet(canaryUrl);

		Map<String, Object> outputs = new HashMap<>();
		String analysisStatus = ACTIVATED;
		while (analysisStatus.equalsIgnoreCase(ACTIVATED)) {
			try {
				request.setHeader("Content-type", "application/json");
				request.setHeader("x-spinnaker-user", user);
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					ObjectNode readValue = objectMapper.readValue(EntityUtils.toString(entity), ObjectNode.class);
					analysisStatus = readValue.get(STATUS).asText();

					logger.info("Visibility approval status : {}", analysisStatus);
					if (analysisStatus.equalsIgnoreCase(APPROVED)) {
						outputs.put(STATUS, analysisStatus);
						return TaskResult.builder(ExecutionStatus.SUCCEEDED)
								.outputs(outputs)
								.build();
					} else if (analysisStatus.equalsIgnoreCase(REJECTED)) {
						outputs.put(STATUS, analysisStatus);
						outputs.put(EXCEPTION, "Rejected by approver");
						return TaskResult.builder(ExecutionStatus.TERMINAL)
								.outputs(outputs)
								.build();
					}		

					Thread.sleep(1000);
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

	private String preparePayload(Map<String, Object> parameterContext, String executionId) throws JsonMappingException, JsonProcessingException {

		ObjectNode finalJson = objectMapper.createObjectNode();

		finalJson.put("approvalCallbackURL", "http://oes-platform:8095/callbackurl");
		finalJson.put("rejectionCallbackURL", "http://oes-platform:8095/rejectionbackurl");
		finalJson.put("executionId", executionId);

		String connectorJson = (String) parameterContext.get("connectors");
		ArrayNode connectorNode = (ArrayNode) objectMapper.readTree(connectorJson);
		ArrayNode toolConnectorPayloads = objectMapper.createArrayNode();
		connectorNode.forEach(connector -> {
			setParameters(toolConnectorPayloads, connector);
		});
		
		finalJson.set(TOOL_CONNECTOR_PARAMETERS, toolConnectorPayloads);
		logger.info("Payload string to trigger approval : {}", finalJson.toString());

		return finalJson.toString();
	}

	private void setParameters(ArrayNode toolConnectorPayloads, JsonNode connector) {
		String connectorType = connector.get(CONNECTOR_TYPE).asText();
		if (connectorType.equals(JIRA)) {
			singlePayload(toolConnectorPayloads, connector, JIRA, JIRA_TICKET_NO);
		} else if (connectorType.equals(AUTOPILOT)) {
			singlePayload(toolConnectorPayloads, connector, AUTOPILOT, CANARY_ID);
		} else if (connectorType.equals(AQUAWAVE)) {
			singlePayload(toolConnectorPayloads, connector, AQUAWAVE, IMAGE_ID);
		} else if (connectorType.equals(APPSCAN)) {
			singlePayload(toolConnectorPayloads, connector, APPSCAN, REPORT_ID);
		} else if (connectorType.equals(SONARQUBE)) {
			singlePayload(toolConnectorPayloads, connector, SONARQUBE, PROJECT_KEY);
		} else if (connectorType.equals(JFROG)) {
			singlePayload(toolConnectorPayloads, connector, JFROG, WATCH_NAME);
		} else if (connectorType.equals(GIT)) {
			gitPayload(toolConnectorPayloads, connector);
		} else if (connectorType.equals(JENKINS)) {
			jenkinsPayload(toolConnectorPayloads, connector);
		} else if (connectorType.equals(BAMBOO)) {
			bambooPayload(toolConnectorPayloads, connector);
		}
	}

	private void bambooPayload(ArrayNode toolConnectorPayloads, JsonNode connector) {
		ObjectNode bambooObjectNode = objectMapper.createObjectNode();
		bambooObjectNode.put(CONNECTOR_TYPE, BAMBOO);
		ArrayNode parameterArrayNode = objectMapper.createArrayNode();
		ArrayNode valuesNode = (ArrayNode) connector.get(VALUES);
		valuesNode.forEach(bambooNode -> {
			if (bambooNode != null && bambooNode.get(PROJECT_NAME) != null && !bambooNode.get(PROJECT_NAME).asText().isEmpty() 
					&& bambooNode.get(PLAN_NAME) != null && !bambooNode.get(PLAN_NAME).isEmpty()
					&& bambooNode.get(BUILD_NUMBER) != null && !bambooNode.get(BUILD_NUMBER).isEmpty()) {
					parameterArrayNode.add(objectMapper.createObjectNode()
							.put(PROJECT_NAME, bambooNode.get(PROJECT_NAME).asText().trim())
							.put(PLAN_NAME, bambooNode.get(PLAN_NAME).asText().trim())
							.put(BUILD_NUMBER, bambooNode.get(BUILD_NUMBER) != null ?  bambooNode.get(BUILD_NUMBER).asText().trim() : ""));
			}
		});
		
		if (parameterArrayNode != null && parameterArrayNode.size() >= 1) {
			bambooObjectNode.set(PARAMETERS, parameterArrayNode);
			toolConnectorPayloads.add(bambooObjectNode);
		}
	}

	private void jenkinsPayload(ArrayNode toolConnectorPayloads, JsonNode connector) {
		ObjectNode jenkinsObjectNode = objectMapper.createObjectNode();
		jenkinsObjectNode.put(CONNECTOR_TYPE, JENKINS);
		ArrayNode parameterArrayNode = objectMapper.createArrayNode();
		ArrayNode valuesNode = (ArrayNode) connector.get(VALUES);
		valuesNode.forEach(jenkinsNode -> {
			if (jenkinsNode != null && jenkinsNode.get(JOB) != null && !jenkinsNode.get(JOB).asText().isEmpty() 
					&& jenkinsNode.get(BUILD_ID) != null && !jenkinsNode.get(BUILD_ID).isEmpty()) {
					parameterArrayNode.add(objectMapper.createObjectNode()
							.put(JOB, jenkinsNode.get(JOB).asText().trim())
							.put(BUILD_ID, jenkinsNode.get(BUILD_ID).asText().trim())
							.put(ARTIFACT, jenkinsNode.get(ARTIFACT) != null ?  jenkinsNode.get(ARTIFACT).asText().trim() : ""));
			}
		});
		
		if (parameterArrayNode != null && parameterArrayNode.size() >= 1) {
			jenkinsObjectNode.set(PARAMETERS, parameterArrayNode);
			toolConnectorPayloads.add(jenkinsObjectNode);
		}
	}

	private void gitPayload(ArrayNode toolConnectorPayloads, JsonNode connector) {
		ObjectNode gitObjectNode = objectMapper.createObjectNode();
		gitObjectNode.put(CONNECTOR_TYPE, GIT);
		ArrayNode parameterArrayNode = objectMapper.createArrayNode();
		ArrayNode valuesNode = (ArrayNode) connector.get(VALUES);
		valuesNode.forEach(gitNode -> {
			if (gitNode != null && gitNode.get(REPO) != null && !gitNode.get(REPO).asText().isEmpty() && gitNode.get(COMMIT_ID) != null && !gitNode.get(COMMIT_ID).isEmpty()) {
					ArrayNode commitIds = objectMapper.createArrayNode();
					Arrays.asList(gitNode.get(COMMIT_ID).asText().split(",")).forEach(a -> {
						commitIds.add(a.trim());
					});

					parameterArrayNode.add(objectMapper.createObjectNode().put(REPO, gitNode.get(REPO).asText()).set(COMMIT_ID, commitIds));
			}
		});
		
		if (parameterArrayNode != null && parameterArrayNode.size() >= 1) {
			gitObjectNode.set(PARAMETERS, parameterArrayNode);
			toolConnectorPayloads.add(gitObjectNode);
		}
	}

	private void singlePayload(ArrayNode toolConnectorPayloads, JsonNode connector, String type, String param) {
		ObjectNode singleObjectNode = objectMapper.createObjectNode();
		singleObjectNode.put(CONNECTOR_TYPE, type);
		ArrayNode paramsNode = objectMapper.createArrayNode();
		ArrayNode valuesNode = (ArrayNode) connector.get(VALUES);
		valuesNode.forEach(a -> {
			if (a != null) {
				Arrays.asList(a.get(param).asText().split(",")).forEach(tic -> {
					paramsNode.add(tic.trim());
				});
			}
		});
		singleObjectNode.set(PARAMETERS, objectMapper.createArrayNode().add(objectMapper.createObjectNode().set(param, paramsNode)));
		toolConnectorPayloads.add(singleObjectNode);
	}

//	private String getPayloadString(ApprovalContext context, String executionId) throws Exception {
//
//		ObjectNode finalJson = objectMapper.createObjectNode();
//		finalJson.put("approvalCallbackURL", "http://oes-platform:8095/callbackurl");
//		finalJson.put("rejectionCallbackURL", "http://oes-platform:8095/rejectionbackurl");
//		finalJson.put("executionId", executionId);
//
//		if (context.getImageIds() != null && !context.getImageIds().isEmpty()) {
//			ArrayNode images = objectMapper.createArrayNode();
//			Arrays.asList(context.getImageIds().split(",")).forEach(a -> {
//				images.add(a.trim());
//			});
//			finalJson.set("imageIds", images);
//		}
//
//		ArrayNode toolConnectorPayloads = objectMapper.createArrayNode();
//		if (context.getJiraId() != null && ! context.getJiraId().isEmpty()) {
//			ObjectNode jiraObjectNode = objectMapper.createObjectNode();
//			jiraObjectNode.put(CONNECTOR_TYPE, JIRA);
//			ArrayNode jiraTickets = objectMapper.createArrayNode();
//
//			Arrays.asList(context.getJiraId().split(",")).forEach(a -> {
//				jiraTickets.add(a.trim());
//			});
//
//			jiraObjectNode.set(PARAMETERS, objectMapper.createArrayNode().add(objectMapper.createObjectNode().set(JIRA_TICKET_NO, jiraTickets)));
//			toolConnectorPayloads.add(jiraObjectNode);
//
//		} else {
//			logger.info("JIRAID is not provided");
//		}
//
//		if (context.getGit() != null && ! context.getGit().isEmpty()) {
//			ObjectNode gitObjectNode = objectMapper.createObjectNode();
//			gitObjectNode.put(CONNECTOR_TYPE, GIT);
//			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
//			context.getGit().forEach(git -> {
//				if (git != null && git.getGitCommitId() != null && !git.getGitCommitId().isEmpty()) {
//					ArrayNode commitIds = objectMapper.createArrayNode();
//					Arrays.asList(git.getGitCommitId().split(",")).forEach(a -> {
//						commitIds.add(a.trim());
//					});
//					parameterArrayNode.add(objectMapper.createObjectNode().put(REPO, git.getGitRepo()).set(COMMIT_ID, commitIds));
//				} 
//			});
//
//			if (parameterArrayNode != null && parameterArrayNode.size() >= 1) {
//				gitObjectNode.set(PARAMETERS, parameterArrayNode);
//				toolConnectorPayloads.add(gitObjectNode);
//			}
//		} else {
//			logger.info("Repo is not provided");
//		}
//
//		if (context.getJenkins() != null && ! context.getJenkins().isEmpty()) {
//
//			ObjectNode jenkinsObjectNode = objectMapper.createObjectNode();
//			jenkinsObjectNode.put(CONNECTOR_TYPE, JENKINS);
//			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
//			context.getJenkins().forEach(jenkins -> {
//				if (jenkins != null && jenkins.getBuildNo() != null && ! jenkins.getBuildNo().isEmpty()) {
//
//					parameterArrayNode.add(objectMapper.createObjectNode()
//							.put(JOB, jenkins.getJobName())
//							.put(BUILD_ID, jenkins.getBuildNo().trim())
//							.put(ARTIFACT, jenkins.getArtifact().trim()));
//				} 
//			});
//
//			if (parameterArrayNode != null && parameterArrayNode.size() >= 1) {
//				jenkinsObjectNode.set(PARAMETERS, parameterArrayNode);
//				toolConnectorPayloads.add(jenkinsObjectNode);
//			}
//		} else {
//			logger.info("Jenkins job is not provided");
//		}
//
//		if (context.getBamboo() != null && ! context.getBamboo().isEmpty()) {
//
//			ObjectNode bambooNode = objectMapper.createObjectNode();
//			bambooNode.put(CONNECTOR_TYPE, BAMBOO);
//			ArrayNode parameterArrayNode = objectMapper.createArrayNode();
//			context.getBamboo().forEach(bamboo -> {
//				if (bamboo != null && bamboo.getProjectName() != null && ! bamboo.getProjectName().isEmpty()
//						&& bamboo.getBuildNumber() != null && bamboo.getPlanName() != null) {
//
//					parameterArrayNode.add(objectMapper.createObjectNode()
//							.put(PROJECT_NAME, bamboo.getProjectName().trim())
//							.put(PLAN_NAME, bamboo.getPlanName().trim())
//							.put(BUILD_NUMBER, bamboo.getBuildNumber().trim()));
//				} 
//			});
//
//			if (parameterArrayNode != null && parameterArrayNode.size() >= 1) {
//				bambooNode.set(PARAMETERS, parameterArrayNode);
//				toolConnectorPayloads.add(bambooNode);
//			}
//		} else {
//			logger.info("Bamboo details is not provided");
//		}
//
//		if (context.getAutopilotCanaryId() != null && ! context.getAutopilotCanaryId().isEmpty()) {
//			ArrayNode commitIds = objectMapper.createArrayNode();
//			Arrays.asList(context.getAutopilotCanaryId().split(",")).forEach(a -> {
//				commitIds.add(a.trim());
//			});
//
//			toolConnectorPayloads.add(
//					objectMapper.createObjectNode().put(CONNECTOR_TYPE, AUTOPILOT)
//					.set(PARAMETERS, objectMapper.createArrayNode().add(objectMapper.createObjectNode().set(CANARY_ID, commitIds))));
//		} else {
//			logger.info("Autopilot id is not provided");
//		}
//
//		if (context.getJfrogWatchName() != null && ! context.getJfrogWatchName().isEmpty()) {
//			ArrayNode commitIds = objectMapper.createArrayNode();
//			Arrays.asList(context.getJfrogWatchName().split(",")).forEach(a -> {
//				commitIds.add(a.trim());
//			});
//
//			toolConnectorPayloads.add(
//					objectMapper.createObjectNode().put(CONNECTOR_TYPE, JFROG)
//					.set(PARAMETERS, objectMapper.createArrayNode().add(objectMapper.createObjectNode().set(WATCH_NAME, commitIds))));
//		} else {
//			logger.info("Jrog is not provided");
//		}
//
//		if (context.getSonarqubeProjectKey() != null && ! context.getSonarqubeProjectKey().isEmpty()) {
//			ArrayNode projIds = objectMapper.createArrayNode();
//			Arrays.asList(context.getSonarqubeProjectKey().split(",")).forEach(a -> {
//				projIds.add(a.trim());
//			});
//
//			toolConnectorPayloads.add(
//					objectMapper.createObjectNode().put(CONNECTOR_TYPE, SONARQUBE)
//					.set(PARAMETERS, objectMapper.createArrayNode().add(objectMapper.createObjectNode().set(PROJECT_KEY, projIds))));
//		} else {
//			logger.info("Sonarqube projectid is not provided");
//		} 
//
//		if (context.getAppScanProjectId() != null && ! context.getAppScanProjectId().isEmpty()) {
//			ArrayNode projIds = objectMapper.createArrayNode();
//			Arrays.asList(context.getAppScanProjectId().split(",")).forEach(a -> {
//				projIds.add(a.trim());
//			});
//
//			toolConnectorPayloads.add(
//					objectMapper.createObjectNode().put(CONNECTOR_TYPE, APPSCAN)
//					.set(PARAMETERS, objectMapper.createArrayNode().add(objectMapper.createObjectNode().set(REPORT_ID, projIds))));
//		} else {
//			logger.info("APPSCAN projectid is not provided");
//		} 
//
//		if (context.getAquaWaveImageId() != null && ! context.getAquaWaveImageId().isEmpty()) {
//			ArrayNode projIds = objectMapper.createArrayNode();
//			Arrays.asList(context.getAquaWaveImageId().split(",")).forEach(a -> {
//				projIds.add(a.trim());
//			});
//
//			toolConnectorPayloads.add(
//					objectMapper.createObjectNode().put(CONNECTOR_TYPE, AQUAWAVE)
//					.set(PARAMETERS, objectMapper.createArrayNode().add(objectMapper.createObjectNode().set(IMAGE_ID, projIds))));
//		} else {
//			logger.info("AQUAWAVE image is not provided");
//		} 	
//
//		finalJson.set(TOOL_CONNECTOR_PARAMETERS, toolConnectorPayloads);
//
//
//		ArrayNode customArrayNode = objectMapper.createArrayNode();
//		if (context.getCustomConnector() != null && ! context.getCustomConnector().isEmpty()) {
//
//			for (CustomConnector custom : context.getCustomConnector()) {
//				if (custom != null && custom.getHeader() != null && !custom.getHeader().isEmpty()) {
//					ObjectNode customNode = objectMapper.createObjectNode();
//					customNode.put(CONNECTOR_TYPE, CUSTOM);
//					customNode.put(NAME, custom.getName());
//
//					ArrayNode headerNode = objectMapper.createArrayNode();
//					Arrays.asList(custom.getHeader().split(",")).forEach(a -> {
//						headerNode.add(a.trim());
//					});
//
//					customNode.set(HEADER_DATA, headerNode);
//					try {
//						customNode.set(DATA, objectMapper.readTree(custom.getData()));
//					} catch (Exception e) {
//						logger.error("please provide valid json data for custom connector", e);
//						throw e;
//					}
//					customArrayNode.add(customNode);
//				}
//			}
//
//		} else {
//			logger.info("CustomConnector is not provided");
//		}
//
//		finalJson.set("customConnectorData", customArrayNode);
//
//		return "";
//	}
}
