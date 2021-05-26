import { IDeckPlugin } from '@spinnaker/core';
import { policyStage } from './PolicyStage';
import { initialize } from './initialize';

export const plugin: IDeckPlugin = {
  initialize,
  stages: [policyStage],
};
