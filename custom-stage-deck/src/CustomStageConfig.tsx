import React from 'react';
import {
  ExecutionDetailsSection,
  ExecutionDetailsTasks,
  FormikFormField,
  FormikStageConfig,
  FormValidator,
  HelpContentsRegistry,
  HelpField,
  IExecutionDetailsSectionProps,
  IStage,
  TextInput,
  IStageConfigProps,
  IStageTypeConfig,
  NumberInput,
  Validators,
} from '@spinnaker/core';

import './CustomStage.less';

/*
  IStageConfigProps defines properties passed to all Spinnaker Stages.
  See IStageConfigProps.ts (https://github.com/spinnaker/deck/blob/master/app/scripts/modules/core/src/pipeline/config/stages/common/IStageConfigProps.ts) for a complete list of properties.
  Pass a JSON object to the `updateStageField` method to add the `maxWaitTime` to the Stage.

  This method returns JSX (https://reactjs.org/docs/introducing-jsx.html) that gets displayed in the Spinnaker UI.
 */
export function CustomStageConfig(props: IStageConfigProps) {
  return (
    <div className="CustomStageConfig">
      <FormikStageConfig
        {...props}
        onChange={props.updateStage}
        render={(props) => (
        <div className="form-horizontal">
          <FormikFormField
            name="vmDetails"
            label="VM Details"
            help={<HelpField id="opsmx.customStage.vmDetails" />}
            input={(props) => <textarea className="form-control code" placeholder=" Vm details payload " rows={5} {...props} />}
          />
          <FormikFormField
            name="payload"
            label="Github Account Artifact Payload"
            help={<HelpField id="opsmx.customStage.payload" />}
            input={(props) => <textarea className="form-control code" placeholder=" Github payload " rows={5} {...props} />}
          />
        </div>
        )}
      />
    </div>
  );
}

export function validate(stageConfig: IStage) {
  const validator = new FormValidator(stageConfig);

  validator
    .field('maxWaitTime')
    .required()
    .withValidators((value, label) => (value < 0 ? `${label} must be non-negative` : undefined));

  return validator.validateForm();
}

