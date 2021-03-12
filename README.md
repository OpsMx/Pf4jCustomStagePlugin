# Pf4jCustomStagePlugin
Pf4j Custom Stage Plugin

This custom stage plugin involves 3 microservices.

     1. Orca
    
     2. Deck
     
     3. Echo

There are 2 ways of deploying plugins in the spinnaker.

## ** Method 1 **

   #### Tasks list

   - [x] Run `./gradlew clean releaseBundle`.
   - [x] Put the `build/distributions/pf4jCustomStagePlugin-v1.0.1.zip` into your github repo.
          See  [Opsmx Spinnaker Plugin Repository] (https://github.com/OpsMx/spinnakerPluginRepository).
   - [x] Configure the Spinnaker service. Put the following in the service.yml to enable the plugin and configure the extension.
          * Orca configuration

Adding the following to your orca.yml or ~/.hal/default/profiles/orca-local.yml config will load and start the latest CustomStage plugin during app startup.
```
spinnaker:
  extensibility:
    plugins:
      Opsmx.CustomStagePlugin:
        enabled: true
        version: 1.0.1
        config:
          defaultVmDetails: '{
                              "username": "ubuntu",
                              "password": "xxxxx",
                              "port": 22,
                              "server": "xx.xx.xx.xx"
                            }'
          defaultgitAccount: '{
                                "artifactAccount": "my-github-artifact-account",
                                "reference": "https://api.github.com/repos/opsmx/Pf4jCustomStagePlugin/contents/script.sh",
                                "type": "github/file",
                                "version": "main"
                              }'
    repositories:
      opsmx-repo:
        url: https://raw.githubusercontent.com/opsmx/spinnakerPluginRepository/master/repositories.json
```
         
   - [x] Restart the microservice.

## ** Method 2 **

   #### Tasks list

   - [x] Run `./gradlew clean releaseBundle`.
   - [x] Put the `<custom-stage-orca>/build/distributions/orca.zip` into spinnaker's microservice plugins root directory.
         Default spinnaker's microservice plugins root directory is `<opt>/<microservice>/plugins/`. eg:- (opt/orca/plugins).
   - [x] Do the same for Echo microservice as in **step 2**.
   - [x] Restart the microservice.
 
   **NOTE: ** `Method 2 cannot be used for deploying ` **DECK** `plugin.`
