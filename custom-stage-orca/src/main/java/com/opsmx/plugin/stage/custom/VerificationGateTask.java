package com.opsmx.plugin.stage.custom;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.netflix.spinnaker.kork.plugins.api.PluginComponent;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;

@Extension
@PluginComponent
public class VerificationGateTask implements Task {

	private static final String METRIC = "metric";

	private static final String LOG = "log";

	private static final String SERVICE_GATE = "serviceGate";

	private static final String PIPELINE_NAME = "pipelineName";

	private static final String MINIMUM_CANARY_RESULT_SCORE = "minimumCanaryResultScore";

	private static final String CANARY_CONFIG = "canaryConfig";

	private static final String CANARY_RESULT = "canaryResult";

	private static final String LOCATION = "location";

	private static final String CANARY_ID = "canaryId";

	private static final String RESULT = "exception";

	private static final String COMPLETED = "COMPLETED";

	private static final String RUNNING = "RUNNING";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ObjectMapper objectMapper = new ObjectMapper();

	@NotNull
	@Override
	public TaskResult execute(@NotNull StageExecution stage) {

		Map<String, Object> contextMap = new HashMap<>();
		Map<String, Object> outputs = new HashMap<>();
		outputs.put(OesConstants.OVERALL_SCORE, 0.0);
		outputs.put(OesConstants.OVERALL_RESULT, "Fail");

		logger.info(" VerificationGateStage execute start ");
		VerificationGateContext context = stage.mapTo(VerificationGateContext.class);

		if (context.getGateUrl() == null || context.getGateUrl().isEmpty()) {
			logger.info("Gate Url should not be empty");
			outputs.put(RESULT, "Gate Url should not be empty");
			return TaskResult.builder(ExecutionStatus.TERMINAL)
					.context(contextMap)
					.outputs(outputs)
					.build();
		}

		logger.info("Application name : {}, Service name : {}", stage.getExecution().getApplication(), stage.getExecution().getName());

		try {

			HttpPost request = new HttpPost(context.getGateUrl());
			request.setEntity(new StringEntity(getPayloadString(stage.getExecution().getApplication(), stage.getExecution().getName(), context)));
			request.setHeader("Content-type", "application/json");

			CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(request);

			HttpEntity entity = response.getEntity();
			String registerResponse = "";
			if (entity != null) {
				registerResponse = EntityUtils.toString(entity);
			}

			if (response.getStatusLine().getStatusCode() != 202) {
				outputs.put(RESULT, registerResponse);
				return TaskResult.builder(ExecutionStatus.TERMINAL)
						.context(contextMap)
						.outputs(outputs)
						.build();
			}

			ObjectNode readValue = objectMapper.readValue(registerResponse, ObjectNode.class);
			String canaryId = readValue.get(CANARY_ID).asText();

			if (canaryId == null || canaryId.isEmpty()) {
				outputs.put(RESULT, "Something goes wrong while triggering registry analysis");
				return TaskResult.builder(ExecutionStatus.TERMINAL)
						.context(contextMap)
						.outputs(outputs)
						.build();
			}

			String canaryUrl = response.getLastHeader(LOCATION).getValue();
			logger.info("Analysis autopilot link : {}", canaryUrl);
			
			return getVerificationStatus(canaryUrl);

		} catch (Exception e) {
			logger.error("Failed to execute verification gate", e);
			outputs.put(RESULT, e.getMessage());
		}

		return TaskResult.builder(ExecutionStatus.TERMINAL)
				.context(contextMap)
				.outputs(outputs)
				.build();
	}

	private TaskResult getVerificationStatus(String canaryUrl) {
		HttpGet request = new HttpGet(canaryUrl);

		Map<String, Object> outputs = new HashMap<>();
		String analysisStatus = RUNNING;
		while (analysisStatus.equalsIgnoreCase(RUNNING)) {
			try {
				request.setHeader("Content-type", "application/json");
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					ObjectNode readValue = objectMapper.readValue(EntityUtils.toString(entity), ObjectNode.class);
					analysisStatus = readValue.get(OesConstants.STATUS).get(OesConstants.STATUS).asText();
					if (analysisStatus.equalsIgnoreCase(COMPLETED)) {

						Float overAllScore = readValue.get(CANARY_RESULT).get(OesConstants.OVERALL_SCORE).floatValue();
						Float successScore = readValue.get(CANARY_CONFIG).get(MINIMUM_CANARY_RESULT_SCORE).floatValue(); 

						outputs.put(OesConstants.OVERALL_RESULT, readValue.get(CANARY_RESULT).get(OesConstants.OVERALL_RESULT).asText());
						outputs.put(OesConstants.CANARY_REPORTURL, readValue.get(CANARY_RESULT).get(OesConstants.CANARY_REPORTURL).asText());
						outputs.put(OesConstants.OVERALL_SCORE, overAllScore);

						if (Float.compare(successScore, overAllScore) == 0 || Float.compare(successScore, overAllScore) < 0) {
							return TaskResult.builder(ExecutionStatus.SUCCEEDED)
									.outputs(outputs)
									.build();
						} else {
							return TaskResult.builder(ExecutionStatus.TERMINAL)
									.outputs(outputs)
									.build();
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				outputs.put(RESULT, e.getMessage());
			}

		}
		
		return TaskResult.builder(ExecutionStatus.TERMINAL)
				.outputs(outputs)
				.build();
	}

	private String getPayloadString(String applicationName, String pipelineName, VerificationGateContext context) {

		ObjectNode finalJson = objectMapper.createObjectNode();
		finalJson.put("application", applicationName);
		finalJson.put("isJsonResponse", true);

		ObjectNode canaryConfig = objectMapper.createObjectNode();
		canaryConfig.put("lifetimeHours", context.getLifeTimeHours());
		canaryConfig.set("canaryHealthCheckHandler", objectMapper.createObjectNode().put(MINIMUM_CANARY_RESULT_SCORE, context.getMinimumCanaryResult()));
		canaryConfig.set("canarySuccessCriteria", objectMapper.createObjectNode().put("canaryResultScore", context.getCanaryResultScore()));

		ObjectNode baselinePayload = objectMapper.createObjectNode();
		ObjectNode canaryPayload = objectMapper.createObjectNode();
		if (context.getLogAnalysis().equals(Boolean.TRUE)) {
			baselinePayload.set(LOG, 
					objectMapper.createObjectNode().set(pipelineName, 
							objectMapper.createObjectNode()
							.put(PIPELINE_NAME, pipelineName)
							.put(SERVICE_GATE, context.getGateName())));
			canaryPayload.set(LOG, 
					objectMapper.createObjectNode().set(pipelineName, 
							objectMapper.createObjectNode()
							.put(PIPELINE_NAME, pipelineName)
							.put(SERVICE_GATE, context.getGateName())));
		}

		if (context.getMetricAnalysis().equals(Boolean.TRUE)) {
			baselinePayload.set(METRIC, 
					objectMapper.createObjectNode().set(pipelineName, 
							objectMapper.createObjectNode()
							.put(PIPELINE_NAME, pipelineName)
							.put(SERVICE_GATE, context.getGateName())));
			canaryPayload.set(METRIC, 
					objectMapper.createObjectNode().set(pipelineName, 
							objectMapper.createObjectNode()
							.put(PIPELINE_NAME, pipelineName)
							.put(SERVICE_GATE, context.getGateName())));
		}

		ObjectNode triggerPayload = objectMapper.createObjectNode();
		triggerPayload.set("baseline", baselinePayload);
		triggerPayload.set("canary", canaryPayload);

		ArrayNode payloadTriggerNode = objectMapper.createArrayNode();
		payloadTriggerNode.add(triggerPayload);
		triggerPayload.put("baselineStartTimeMs", context.getBaselineStartTime());
		triggerPayload.put("canaryStartTimeMs", context.getCanaryStartTime());

		finalJson.set(CANARY_CONFIG, canaryConfig);
		finalJson.set("canaryDeployments", payloadTriggerNode);
		String finalPayloadString = finalJson.toString();
		logger.info("Payload string to trigger analysis : {}", finalPayloadString);

		return finalPayloadString;
	}
}
