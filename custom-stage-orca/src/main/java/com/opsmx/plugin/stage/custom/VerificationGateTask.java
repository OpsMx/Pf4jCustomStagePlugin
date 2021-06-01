package com.opsmx.plugin.stage.custom;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.google.common.io.CharStreams;
import com.netflix.spinnaker.kork.plugins.api.PluginComponent;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;

@Extension
@PluginComponent
public class VerificationGateTask implements Task {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ObjectMapper objectMapper = new ObjectMapper();

	@NotNull
	@Override
	public TaskResult execute(@NotNull StageExecution stage) {

			Map<String, Object> contextMap = new HashMap<>();
			Map<String, Object> outputs = new HashMap<>();
			log.info(" VerificationGateStage execute start ");
			VerificationGateContext context = stage.mapTo(VerificationGateContext.class);
			
			if (context.getGateUrl().isEmpty()) {
				log.info("Gate Url should not be empty");
				outputs.put("result", "Gate Url should not be empty");
				return TaskResult.builder(ExecutionStatus.TERMINAL)
						.context(contextMap)
						.outputs(outputs)
						.build();
			}

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost post = new HttpPost(context.getGateUrl());

			try {
				post.setEntity(new StringEntity(getPayloadString(stage.getExecution().getApplication(), stage.getExecution().getName(), context)));
				post.setHeader("Content-type", "application/json");
				CloseableHttpResponse response = httpClient.execute(post);
				if (response.getStatusLine().getStatusCode() != 202) {
					outputs.put("result", response.getStatusLine());
					return TaskResult.builder(ExecutionStatus.TERMINAL)
							.context(contextMap)
							.outputs(outputs)
							.build();
				}

				String canaryId = "";
				HttpEntity registerEntity = response.getEntity();
				InputStream responseStream = registerEntity.getContent();
				try (InputStreamReader rd = new InputStreamReader(responseStream)) {
					ObjectNode readValue = objectMapper.readValue(CharStreams.toString(rd), ObjectNode.class);
					canaryId = readValue.get("canaryId").asText();
				} catch (IOException e) {
					outputs.put("result", e.getMessage());
				}

				if (canaryId == null || canaryId.isEmpty()) {
					return TaskResult.builder(ExecutionStatus.TERMINAL)
							.context(contextMap)
							.outputs(outputs)
							.build();
				}

				String canaryUrl = response.getLastHeader("location").getValue();
				
				return getVerificationStatus(canaryUrl);

			} catch (Exception e) {
				e.printStackTrace();
				outputs.put("result", e.getMessage());
			}

			return TaskResult.builder(ExecutionStatus.TERMINAL)
					.context(contextMap)
					.outputs(outputs)
					.build();
	}

	private TaskResult getVerificationStatus(String canaryUrl) {
		HttpGet request = new HttpGet(canaryUrl);

		Map<String, Object> outputs = new HashMap<>();
		TaskResult taskResult = TaskResult.builder(ExecutionStatus.TERMINAL)
				.outputs(outputs)
				.build();
		String analysisStatus = "RUNNING";
		while (analysisStatus.equalsIgnoreCase("RUNNING")) {
			try {
				request.setHeader("Content-type", "application/json");
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(request);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					ObjectNode readValue = objectMapper.readValue(EntityUtils.toString(entity), ObjectNode.class);
					analysisStatus = readValue.get("status").get("status").asText();
					if (analysisStatus.equalsIgnoreCase("COMPLETED")) {
						
						outputs.put("overallResult", readValue.get("canaryResult").get("overallResult").asText());
						outputs.put("overallScore", readValue.get("canaryResult").get("overallScore").asText());					
						TaskResult.builder(ExecutionStatus.SUCCEEDED)
						.outputs(outputs)
						.build();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				outputs.put("result", e.getMessage());
			}

		}
		return taskResult;
	}

	private String getPayloadString(String applicationName, String pipelineName, VerificationGateContext context) {

		ObjectNode finalJson = objectMapper.createObjectNode();
		finalJson.put("application", applicationName);
		finalJson.put("isJsonResponse", true);

		ObjectNode canaryConfig = objectMapper.createObjectNode();
		canaryConfig.put("lifetimeHours", context.getLifeTimeHours());
		canaryConfig.set("canaryHealthCheckHandler", objectMapper.createObjectNode().put("minimumCanaryResultScore", context.getMinimumCanaryResult()));
		canaryConfig.set("canarySuccessCriteria", objectMapper.createObjectNode().put("canaryResultScore", context.getCanaryResultScore()));

		ObjectNode baselinePayload = objectMapper.createObjectNode();
		ObjectNode canaryPayload = objectMapper.createObjectNode();
		if (context.getLogAnalysis().equals(Boolean.TRUE)) {
			baselinePayload.set("log", 
					objectMapper.createObjectNode().set(pipelineName, 
							objectMapper.createObjectNode()
							.put("pipelineName", pipelineName)
							.put("serviceGate", context.getGateName())));
			canaryPayload.set("log", 
					objectMapper.createObjectNode().set(pipelineName, 
							objectMapper.createObjectNode()
							.put("pipelineName", pipelineName)
							.put("serviceGate", context.getGateName())));
		}

		if (context.getMetricAnalysis().equals(Boolean.TRUE)) {
			baselinePayload.set("metric", 
					objectMapper.createObjectNode().set(pipelineName, 
							objectMapper.createObjectNode()
							.put("pipelineName", pipelineName)
							.put("serviceGate", context.getGateName())));
			canaryPayload.set("metric", 
					objectMapper.createObjectNode().set(pipelineName, 
							objectMapper.createObjectNode()
							.put("pipelineName", pipelineName)
							.put("serviceGate", context.getGateName())));
		}

		ObjectNode triggerPayload = objectMapper.createObjectNode();
		triggerPayload.set("baseline", baselinePayload);
		triggerPayload.set("canary", canaryPayload);

		ArrayNode payloadTriggerNode = objectMapper.createArrayNode();
		payloadTriggerNode.add(triggerPayload);
		triggerPayload.put("baselineStartTimeMs", context.getBaselineStartTime());
		triggerPayload.put("canaryStartTimeMs", context.getCanaryStartTime());

		finalJson.set("canaryConfig", canaryConfig);
		finalJson.set("canaryDeployments", payloadTriggerNode);
		String finalPayloadString = finalJson.toString();
		log.info("Payload string to trigger analysis : {}", finalPayloadString);
		
		return finalPayloadString;
	}

	private Long getEpoch(String timestamp) {

		if(timestamp == null) return null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			Date dt = sdf.parse(timestamp);
			return dt.getTime();
		} catch(Exception e) {
			return null;
		}
	}
}
