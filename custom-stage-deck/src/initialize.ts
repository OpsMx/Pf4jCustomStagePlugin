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
  HelpContentsRegistry.register('opsmx.verificationGate.gateUrl', 'Gate Url.');
  HelpContentsRegistry.register('opsmx.verificationGate.lifeTimeHours', 'Life Time Hours.');
  HelpContentsRegistry.register('opsmx.verificationGate.minimumCanaryResult', 'Minimum Canary Result.');
  HelpContentsRegistry.register('opsmx.verificationGate.canaryResultScore', 'Canary Result Score.');
  HelpContentsRegistry.register('opsmx.verificationGate.logAnalysis', 'Log Analysis.');
  HelpContentsRegistry.register('opsmx.verificationGate.metricAnalysis', 'Metric Analysis.');
  HelpContentsRegistry.register('opsmx.verificationGate.baselineStartTime', 'Baseline StartTime.');
  HelpContentsRegistry.register('opsmx.verificationGate.canaryStartTime', 'Canary StartTime.');
  HelpContentsRegistry.register('opsmx.verificationGate.gateName', 'Gate Name.');
  HelpContentsRegistry.register('opsmx.verificationGate.imageIds', 'Image Ids.');
};
