# Pf4jCustomStagePlugin
Pf4j Custom Stage Plugin

This custom stage plugin involves 2 microservices.

     1. Orca
    
     2. Deck

1.  Orca configuration

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

2.  Deck configuration

Adding the following to your gate.yml or ~/.hal/default/profiles/gate-local.yml config will load and start the latest CustomStage plugin during app startup.
```
spinnaker:
 extensibility:
    plugins:
    deck-proxy:
      enabled: true
      plugins:
        Opsmx.CustomStagePlugin:
          enabled: true
          version: 1.0.1
    repositories:
      opsmx-repo:
        url: https://raw.githubusercontent.com/opsmx/spinnakerPluginRepository/master/plugins.json
```
