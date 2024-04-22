package com.opsmx.plugin.stage.custom.tasks;

import com.netflix.spinnaker.kork.plugins.api.PluginComponent;
import com.netflix.spinnaker.orca.api.pipeline.Task;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import com.opsmx.plugin.stage.custom.config.CustomStageConfig;
import com.opsmx.plugin.stage.custom.monitor.MonitorClient;
import org.jetbrains.annotations.NotNull;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Extension
@PluginComponent
public class CustomStageTask implements Task {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private CustomStageConfig config;

    private MonitorClient monitorClient;

    public CustomStageTask(CustomStageConfig config, Optional<MonitorClient> client) {
        this.config = config;
        monitorClient = client.orElseGet(()->MonitorClient.create(config.getDatadogApiKey(), config.getDatadogAppKey()));
    }

    @NotNull
    @Override
    public TaskResult execute(@NotNull StageExecution stage) {
        log.info(" CustomStageTask execute start ");
        return TaskResult.builder(ExecutionStatus.SUCCEEDED).build();
    }


}
