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
  HelpContentsRegistry.register('opsmx.policyStage.policyProxy', 'Please enter the POLICY Proxy Hostname and Port.');
  HelpContentsRegistry.register('opsmx.policyStage.policyPath', 'Please enter Policy path that applies to this stage.');
  HelpContentsRegistry.register('opsmx.policyStage.payload', 'Please enter the Payload in single line.');
  HelpContentsRegistry.register('opsmx.policyStage.gateName', 'Gate Name.');
  HelpContentsRegistry.register('opsmx.policyStage.imageIds', 'Image IDs.');
};
