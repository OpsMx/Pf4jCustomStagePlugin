import { IDeckPlugin } from '@spinnaker/core';
import { policyGate } from './PolicyGate';
import { initialize } from './initialize';

export const plugin: IDeckPlugin = {
  initialize,
  stages: [policyGate],
};
