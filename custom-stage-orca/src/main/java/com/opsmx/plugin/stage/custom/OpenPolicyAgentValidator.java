package com.opsmx.plugin.stage.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.Errors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.netflix.spinnaker.front50.model.pipeline.Pipeline;
import com.netflix.spinnaker.front50.model.pipeline.PipelineDAO;
import com.netflix.spinnaker.front50.validator.PipelineValidator;
import com.netflix.spinnaker.kork.plugins.api.internal.SpinnakerExtensionPoint;
import com.netflix.spinnaker.kork.web.exceptions.ValidationException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


@Extension
public class OpenPolicyAgentValidator implements PipelineValidator, SpinnakerExtensionPoint {

	private final Logger logger = LoggerFactory.getLogger(OpenPolicyAgentValidator.class);

	private final PipelineDAO pipelineDAO;
	/* define configurable variables:
	    opaUrl: OPA or OPA-Proxy base url
	    opaResultKey: Not needed for Proxy. The key to watch in the return from OPA.
	    policyLocation: Where in OPA is the policy located, generally this is v0/location/to/policy/path
	                    And for Proxy it is /v1/staticPolicy/eval
	    isOpaEnabled: Policy evaluation is skipped if this is false
	    isOpaProxy : true if Proxy is present instead of OPA server.
	 */
	@Value("${policy.opa.url:http://oes-server-svc.oes:8085}")
	private String opaUrl;

	@Value("${policy.opa.resultKey:deny}")
	private String opaResultKey;

	@Value("${policy.opa.policyLocation:/v1/staticPolicy/eval}")
	private String opaPolicyLocation;

	@Value("${policy.opa.enabled:false}")
	private boolean isOpaEnabled;

	@Value("${policy.opa.proxy:true}")
	private boolean isOpaProxy;

	@Value("${policy.opa.deltaVerification:false}")
	private boolean deltaVerification;

	private final Gson gson = new Gson();

	/* OPA spits JSON */
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private final OkHttpClient opaClient = new OkHttpClient();

	public OpenPolicyAgentValidator(PipelineDAO pipelineDAO) {
		this.pipelineDAO = pipelineDAO;
	}

	@Override
	public void validate(Pipeline pipeline, Errors errors) {
		if (!isOpaEnabled) {
			logger.info("OPA not enabled, returning");
			return;
		}
		String finalInput = null;
		Response httpResponse;
		try {

			// Form input to opa
			finalInput = getOpaInput(pipeline, deltaVerification);

			logger.debug("Verifying {} with OPA", finalInput);

			/* build our request to OPA */
			RequestBody requestBody = RequestBody.create(JSON, finalInput);
			String opaFinalUrl = String.format("%s/%s", opaUrl.endsWith("/") ? opaUrl.substring(0, opaUrl.length() - 1) : opaUrl, opaPolicyLocation.startsWith("/") ? opaPolicyLocation.substring(1) : opaPolicyLocation);
			
			logger.info("OPA endpoint : {}", opaFinalUrl);
			String opaStringResponse;

			/* fetch the response from the spawned call execution */
			httpResponse = doPost(opaFinalUrl, requestBody);
			opaStringResponse = httpResponse.body().string();
			logger.info("OPA response: {}", opaStringResponse);
			logger.info("proxy enabled : {}, statuscode : {}, opaResultKey : {}", isOpaProxy, httpResponse.code(), opaResultKey);
			if (isOpaProxy) {
				if (httpResponse.code() == 401 ) {
					JsonObject opaResponse = gson.fromJson(opaStringResponse, JsonObject.class);
					StringBuilder denyMessage = new StringBuilder();
					extractDenyMessage(opaResponse, denyMessage);
					if (StringUtils.isNotBlank(denyMessage)) {
						throw new ValidationException(denyMessage.toString(), null);
					} else {
						throw new ValidationException("There is no '" + opaResultKey + "' field in the OPA response", null);
					}
				} else if (httpResponse.code() != 200 ) {
					throw new ValidationException(opaStringResponse, null);
				}
			} else {
				if (httpResponse.code() == 401 ) {
					JsonObject opaResponse = gson.fromJson(opaStringResponse, JsonObject.class);
					StringBuilder denyMessage = new StringBuilder();
					extractDenyMessage(opaResponse, denyMessage);
					if (StringUtils.isNotBlank(denyMessage)) {
						throw new ValidationException(denyMessage.toString(), null);
					} else {
						throw new ValidationException("There is no '" + opaResultKey + "' field in the OPA response", null);
					}
				} else if (httpResponse.code() != 200 ) {
					throw new ValidationException(opaStringResponse, null);
				}
			}
		} catch (IOException e) {
			logger.error("Communication exception for OPA at {}: {}", this.opaUrl, e.toString());
			throw new ValidationException(e.toString(), null);
		}
	}

	private void extractDenyMessage(JsonObject opaResponse, StringBuilder messagebuilder) {
		Set<Entry<String, JsonElement>> fields = opaResponse.entrySet();
		fields.forEach(field -> {
			if (field.getKey().equalsIgnoreCase(opaResultKey)) {
				JsonArray resultKey = field.getValue().getAsJsonArray();
				if (resultKey.size() != 0) {
					resultKey.forEach(result -> {
						if (StringUtils.isNotEmpty(messagebuilder)) {
							messagebuilder.append(", ");
						}
						messagebuilder.append(result.getAsString());
					});
				}
			}else if (field.getValue().isJsonObject()) {
				extractDenyMessage(field.getValue().getAsJsonObject(), messagebuilder);
			} else if (field.getValue().isJsonArray()){
				field.getValue().getAsJsonArray().forEach(obj -> {
					extractDenyMessage(obj.getAsJsonObject(), messagebuilder);
				});
			}
		});
	}

	private String getOpaInput(Pipeline pipeline, boolean deltaVerification) {
		String application;
		String pipelineName;
		String finalInput = null;
		boolean initialSave = false;
		JsonObject newPipeline = pipelineToJsonObject(pipeline);
		if (newPipeline.has("application")) {
			application = newPipeline.get("application").getAsString();
			pipelineName = newPipeline.get("name").getAsString();
			logger.debug("## input : {}", gson.toJson(newPipeline));
			if (newPipeline.has("stages")) {
				initialSave = newPipeline.get("stages").getAsJsonArray().size() == 0;
			}
			logger.debug("## application : {}, pipelineName : {}", application, pipelineName);
			// if deltaVerification is true, add both current and new pipelines in single json
			if (deltaVerification && !initialSave) {
				List<Pipeline> pipelines =
						new ArrayList<>(pipelineDAO.getPipelinesByApplication(application, true));
				logger.debug("## pipeline list count : {}", pipelines.size());
				Optional<Pipeline> currentPipeline =
						pipelines.stream()
						.filter(p -> ((String) p.getName()).equalsIgnoreCase(pipelineName))
						.findFirst();
				if (currentPipeline.isPresent()) {
					finalInput = getFinalOpaInput(newPipeline, pipelineToJsonObject(currentPipeline.get()));
				} else {
					throw new ValidationException("There is no pipeline with name " + pipelineName, null);
				}
			} else {
				finalInput = gson.toJson(addWrapper(addWrapper(newPipeline, "new"), "input"));
			}
		} else {
			throw new ValidationException("The received pipeline doesn't have application field", null);
		}
		return finalInput;
	}

	private String getFinalOpaInput(JsonObject newPipeline, JsonObject currentPipeline) {
		JsonObject input = new JsonObject();
		input.add("new", newPipeline);
		input.add("current", currentPipeline);
		return gson.toJson(addWrapper(input, "input"));
	}

	private JsonObject addWrapper(JsonObject pipeline, String wrapper) {
		JsonObject input = new JsonObject();
		input.add(wrapper, pipeline);
		return input;
	}

	private JsonObject pipelineToJsonObject(Pipeline pipeline) {
		String pipelineStr = gson.toJson(pipeline, Pipeline.class);
		return gson.fromJson(pipelineStr, JsonObject.class);
	}

	/*
	 * private Response doGet(String url) throws IOException { Request req = (new
	 * Request.Builder()).url(url).get().build(); return getResponse(url, req); }
	 */

	private Response doPost(String url, RequestBody requestBody) throws IOException {
		Request req = (new Request.Builder()).url(url).post(requestBody).build();
		return getResponse(url, req);
	}

	private Response getResponse(String url, Request req) throws IOException {
		Response httpResponse = this.opaClient.newCall(req).execute();
		ResponseBody responseBody = httpResponse.body();
		if (responseBody == null) {
			throw new IOException("Http call yielded null response!! url:" + url);
		}
		return httpResponse;
	}
}