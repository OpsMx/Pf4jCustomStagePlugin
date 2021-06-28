import { HelpContentsRegistry } from '@spinnaker/core';

/*
  This is a contrived example of how to use an `initialize` function to hook into arbitrary Deck services. 
  This `initialize` function provides the help field text for the `CustomStageConfig` stage form.

  You can hook into any service exported by the `@spinnaker/core` NPM module, e.g.:
   - CloudProviderRegistry
   - DeploymentStrategyRegistry

  When you use a registry, you are diving into Deck's implementation to add functionality. 
  These registries and their methods may change without warning.
*/
export const initialize = () => {
  HelpContentsRegistry.register('opsmx.visibilityApproval.gateUrl', 'Please enter the Gate URL');
  HelpContentsRegistry.register('opsmx.visibilityApproval.jiraId', 'Please enter the JIRA ID');
  HelpContentsRegistry.register('opsmx.visibilityApproval.autopilotCanaryId', 'Please enter the Autopilot Canary');
  HelpContentsRegistry.register(
    'opsmx.visibilityApproval.sonarqubeProjectKey',
    'Please enter the Sonarqube Prject Key',
  );
  HelpContentsRegistry.register('opsmx.visibilityApproval.appScanProjectId', 'Please enter the App Scan ID');
  HelpContentsRegistry.register('opsmx.visibilityApproval.aquaWaveImageId', 'Please enter the Aqua Wave Image IDs');
  HelpContentsRegistry.register('opsmx.visibilityApproval.gateName', 'Please enter Gate Name that is created in OES');
  HelpContentsRegistry.register('opsmx.visibilityApproval.imageIds', 'Please enter the Image IDs');
  HelpContentsRegistry.register('opsmx.visibilityApproval.git', 'Please enter the git details (repo,commitId).');
  HelpContentsRegistry.register(
    'opsmx.visibilityApproval.jenkins',
    'Please enter the jenkins details (jobName,buildNo,artifact).',
  );
  HelpContentsRegistry.register(
    'opsmx.visibilityApproval.customConnector',
    'Please enter the Custom Connector details (name,header,data).',
  );
};
