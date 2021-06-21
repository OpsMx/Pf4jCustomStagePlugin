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
  HelpContentsRegistry.register(
    'opsmx.visibilityApproval.lifeTimeHours',
    'Please enter the Life Time in Hours like 0.5',
  );
  HelpContentsRegistry.register(
    'opsmx.visibilityApproval.minimumCanaryResult',
    'Please enter the Minimum Canary Result',
  );
  HelpContentsRegistry.register('opsmx.visibilityApproval.canaryResultScore', 'Please enter the Canry Result Score');
  HelpContentsRegistry.register('opsmx.visibilityApproval.logAnalysis', 'Please Enable Log Analysis with true / false');
  HelpContentsRegistry.register(
    'opsmx.visibilityApproval.metricAnalysis',
    'Please Enable Metric Analysis with true / false',
  );
  HelpContentsRegistry.register('opsmx.visibilityApproval.baselineStartTime', 'Please select Baseline Start Time');
  HelpContentsRegistry.register('opsmx.visibilityApproval.canaryStartTime', 'Please select Canry Start Time');
  HelpContentsRegistry.register('opsmx.visibilityApproval.gateName', 'Please enter Gate Name that is created in OES');
  HelpContentsRegistry.register('opsmx.visibilityApproval.imageIds', 'Please enter the Image IDs');
};
