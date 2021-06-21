import { IDeckPlugin } from '@spinnaker/core';
import { visibilityApproval } from './VisibilityApproval';
import { initialize } from './initialize';

export const plugin: IDeckPlugin = {
  initialize,
  stages: [visibilityApproval],
};
