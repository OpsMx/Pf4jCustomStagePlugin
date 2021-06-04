package com.opsmx.plugin.stage.custom;

import org.jetbrains.annotations.NotNull;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import com.netflix.spinnaker.orca.api.pipeline.graph.StageDefinitionBuilder;
import com.netflix.spinnaker.orca.api.pipeline.graph.TaskNode;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;

@Extension
@StageDefinitionBuilder.Aliases("verificationGate")
@Component
public class VerificationGateStage implements StageDefinitionBuilder {

	@Override
	public void taskGraph(@NotNull StageExecution stage, @NotNull TaskNode.Builder builder) {
		builder.withTask("verificationGate", VerificationGateTask.class);
	}
}
