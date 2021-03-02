import { IDeckPlugin } from '@spinnaker/core';
import { customStage } from './CustomStage';
import { initialize } from './initialize';

export const plugin: IDeckPlugin = {
  initialize,
  stages: [customStage],
};
