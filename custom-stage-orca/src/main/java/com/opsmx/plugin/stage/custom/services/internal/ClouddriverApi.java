package com.opsmx.plugin.stage.custom.services.internal;

import com.netflix.spinnaker.kork.artifacts.model.Artifact;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.PUT;

public interface ClouddriverApi {

    @PUT("/artifacts/fetch")
    Response fetchArtifact(@Body Artifact artifact);
}
