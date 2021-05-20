import { IDeckPlugin } from '@spinnaker/core';
import { verificationGate } from './VerificationGate';
import { initialize } from './initialize';

export const plugin: IDeckPlugin = {
  initialize,
  stages: [verificationGate],
};
