package com.opsmx.plugin.stage.custom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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

@Extension
@PluginComponent
public class PolicyTask implements Task {

	private static final String USER2 = "user";

	private static final String TRIGGER = "trigger";

	private static final String NAME2 = "name";

	private static final String APPLICATION2 = "application";

	private static final String START_TIME = "startTime";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String EXCEPTION = "exception";

	@Autowired
	private ObjectMapper objectMapper = new ObjectMapper();

	public PolicyTask() {
	}


	@NotNull
	@Override
	public TaskResult execute(@NotNull StageExecution stage) {

		Map<String, Object> contextMap = new HashMap<>();
		Map<String, Object> outputs = new HashMap<>();
		logger.info("Policy gate execution start ");
		PolicyContext context = stage.mapTo("/parameters", PolicyContext.class);


		try {  
			if (context.getPolicyurl() == null || context.getPolicyurl().isEmpty()) {
				logger.info("Policyproxy Url should not be empty");
				outputs.put(EXCEPTION, "Policyproxy Url should not be empty");
				return TaskResult.builder(ExecutionStatus.TERMINAL)
						.context(contextMap)
						.outputs(outputs)
						.build();
			}     

			if (context.getPolicyurl().endsWith("/")) {
				context.setPolicyurl(context.getPolicyurl().replaceAll(".$", ""));
			}

			if (context.getPolicypath().startsWith("/")) {
				context.setPolicypath(context.getPolicypath().substring(1));
			}

			String url = context.getPolicyurl().concat("/").concat(context.getPolicypath());

			HttpPost request = new HttpPost(url);
			request.setEntity(new StringEntity(getPayloadString(context, stage.getExecution().getApplication(), stage.getExecution().getName(),
					stage.getExecution().getId(), stage.getExecution().getAuthentication().getUser(), "")));
			request.setHeader("Content-type", "application/json");
			request.setHeader("x-spinnaker-user", stage.getExecution().getAuthentication().getUser());

			CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(request);

			HttpEntity entity = response.getEntity();
			String registerResponse = "";
			if (entity != null) {
				registerResponse = EntityUtils.toString(entity);
			}

			logger.info("Policy trigger response : {}", registerResponse);

			if (response.getStatusLine().getStatusCode() != 200) {
				outputs.put(EXCEPTION, registerResponse);
				return TaskResult.builder(ExecutionStatus.TERMINAL)
						.context(contextMap)
						.outputs(outputs)
						.build();
			}

			List<String> deny = Arrays.asList();
			JsonNode rootNode = objectMapper.readTree(registerResponse);  
			Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.fields();
		       while (fieldsIterator.hasNext()) {

		           Map.Entry<String,JsonNode> field = fieldsIterator.next();
		           if (field.getKey().equalsIgnoreCase("deny")) {
		        	   ArrayNode denyJson = (ArrayNode) field.getValue();
		        	   denyJson.forEach(a -> {
		        		   deny.add(a.asText());
		        	   });
		           }
		       }
		       
		     if (deny == null || deny.isEmpty()) {
		    	 outputs.put("result", registerResponse);
		    	 TaskResult.builder(ExecutionStatus.SUCCEEDED)
					.context(contextMap)
					.outputs(outputs)
					.build();
		     } else {
		    	 outputs.put("result", registerResponse);
		    	 TaskResult.builder(ExecutionStatus.TERMINAL)
					.context(contextMap)
					.outputs(outputs)
					.build();
		     }
			

		} catch (Exception e) {
			logger.error("Error occured while getting policy result ", e);
			outputs.put(EXCEPTION, e.getMessage());
		}

		return TaskResult.builder(ExecutionStatus.TERMINAL)
				.context(contextMap)
				.outputs(outputs)
				.build();
	}


	private String getPayloadString(PolicyContext context, String application, String name, String executionId, String user, String payload) throws JsonMappingException, JsonProcessingException {
		ObjectNode finalJson = objectMapper.createObjectNode();
		if (payload != null && ! payload.isEmpty()) {
			finalJson = (ObjectNode) objectMapper.readTree(payload);
			finalJson.put(START_TIME, System.currentTimeMillis());
			finalJson.put(APPLICATION2, application);
			finalJson.put(NAME2, name);
			finalJson.set(TRIGGER, objectMapper.createObjectNode().put(USER2, user));
			
		} else {
			finalJson.put(START_TIME, System.currentTimeMillis());
			finalJson.put(APPLICATION2, application);
			finalJson.put(NAME2, name);
			finalJson.put("stage", context.getGate());
			finalJson.put("executionId", executionId);
			finalJson.set(TRIGGER, objectMapper.createObjectNode().put(USER2, user));
			if (context.getImageids() != null && !context.getImageids().isEmpty()) {
				ArrayNode images = objectMapper.createArrayNode();
				Arrays.asList(context.getImageids().split(",")).forEach(a -> {
					images.add(a.trim());
				});
				finalJson.set("imageIds", images);
			}
		}
		
		logger.info("Payload string to policy : {}", finalJson.toString());
		return finalJson.toString();
	}
}
