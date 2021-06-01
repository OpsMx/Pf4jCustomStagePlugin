package com.opsmx.plugin.stage.custom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.CharStreams;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import com.netflix.spinnaker.kork.plugins.api.PluginComponent;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import com.netflix.spinnaker.orca.clouddriver.OortService;
import com.opsmx.plugin.stage.custom.services.internal.ClouddriverApi;
import com.opsmx.plugin.stage.custom.services.internal.RestOk3Client;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
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
import retrofit.client.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
@PluginComponent
public class CustomStageTask implements Task {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private CustomStageConfig config;

	private ClouddriverApi oort = new RestOk3Client().getClient();

	@Autowired
	private ObjectMapper objectMapper = new ObjectMapper();

	public CustomStageTask() {
	}

	public CustomStageTask(CustomStageConfig config) {
		this.config = config;
	}

	@NotNull
	@Override
	public TaskResult execute(@NotNull StageExecution stage) {

		String stageType = stage.getType();
		if (stageType.equalsIgnoreCase(StageType.VERIFICATION_GATE.getType())) {
			Map<String, Object> contextMap = new HashMap<>();
			Map<String, Object> outputs = new HashMap<>();
			log.info(" CustomStageTask execute start ");
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

			outputs.put("result", "Error while getting canary id");
			return TaskResult.builder(ExecutionStatus.TERMINAL)
					.context(contextMap)
					.outputs(outputs)
					.build();

		} else {
			log.info(" CustomStageTask execute start ");
			CustomStageContext context = stage.mapTo(CustomStageContext.class);
			String vmDetails = context.getVmDetails() != null ?
					context.getVmDetails() : config.getDefaultVmDetails();
					String payload = context.getPayload() != null ?
							context.getPayload() : config.getDefaultGitAccount();
							log.info(" vmDetails : " + vmDetails);
							log.info(" payload : " + payload);
							String result = "";
							String command = "";

							Map<String, Object> githubArtifact = null;
							Map<String, Object> VmDetailsPayload = null;
							try {
								githubArtifact =
										objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
										});
								VmDetailsPayload = objectMapper.readValue(vmDetails, new TypeReference<Map<String, Object>>() {
								});
								Artifact artifact = prepareArtifact(githubArtifact);
								String response = fetchGithubArtifact(artifact);
								log.info(" response : " + response);
								command = parseArtifactForCommand(response);
								log.info(" command : " + command);
								result = connectSshServerAndSendResponse((String) VmDetailsPayload.get("username"),
										(String) VmDetailsPayload.get("password"),
										(String) VmDetailsPayload.get("server"),
										(Integer) VmDetailsPayload.get("port"), command);
							} catch (IOException e) {
								log.warn("Failure parsing githubArtifact from {}", githubArtifact, e);
								throw new IllegalStateException(e); // forces a retry
							} catch (Exception e) {
								e.printStackTrace();
							}
							Map<String, Object> outputs = new HashMap<>();
							outputs.put("command", command);
							outputs.put("vmServer", VmDetailsPayload.get("server"));
							outputs.put("result", result);
							Map<String, Object> contextMap = new HashMap<>();
							log.info(" CustomStageTask execute end ");
							return TaskResult.builder(ExecutionStatus.SUCCEEDED)
									.context(contextMap)
									.outputs(outputs)
									.build();
		}
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

	private String parseArtifactForCommand(String response) {

		log.info(" parseArtifactForCommand start ");
		String command = "";
		String[] lines = response.split(System.getProperty("line.separator"));
		for (String line : lines) {
			if (line.trim() != null && !(line.trim().startsWith("#")) && !(line.trim().isEmpty())) {
				command = line;
			}
		}
		log.info(" parseArtifactForCommand end ");
		return command;
	}

	private Artifact prepareArtifact(Map<String, Object> artifact) {

		log.info(" prepareArtifact start ");
		Artifact githubArtifact = Artifact.builder().name("testCustomStage").
				type((String) artifact.get("type")).
				artifactAccount((String) artifact.get("artifactAccount")).
				version((String) artifact.get("version")).
				reference((String) artifact.get("reference")).build();
		log.info(" prepareArtifact end ");
		return githubArtifact;
	}

	private String fetchGithubArtifact(Artifact artifact) {

		log.info(" fetchGithubArtifact ");
		Response response = oort.fetchArtifact(artifact);
		InputStream artifactInputStream;
		try {
			artifactInputStream = response.getBody().in();
		} catch (IOException e) {
			log.warn("Failure fetching script.sh from {}", artifact, e);
			throw new IllegalStateException(e); // forces a retry
		}
		try (InputStreamReader rd = new InputStreamReader(artifactInputStream)) {
			return CharStreams.toString(rd);
		} catch (IOException e) {
			log.warn("Failure fetching script.sh from {}", artifact, e);
			throw new IllegalStateException(e); // forces a retry
		}
	}

	private String connectSshServerAndSendResponse(String username, String password,
			String host, int port, String command) throws Exception {

		log.info(" connectSshServerAndSendResponse start ");
		Session session = null;
		ChannelExec channel = null;
		String responseString = "";

		try {
			session = new JSch().getSession(username, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
			channel.setOutputStream(responseStream);
			channel.connect();

			while (channel.isConnected()) {
				Thread.sleep(100);
			}

			responseString = new String(responseStream.toByteArray());
			log.info("command output : " + responseString);
		} finally {
			if (session != null) {
				session.disconnect();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
		log.info(" connectSshServerAndSendResponse end ");
		return responseString;
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
