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
  RadioButtonInput,
  DayPickerInput,
  IStageConfigProps,
  IStageTypeConfig,
  NumberInput,
  Validators,
} from '@spinnaker/core';
import './VerificationGate.less';
import { DateTimePicker } from './input/DateTimePickerInput';

/*
  IStageConfigProps defines properties passed to all Spinnaker Stages.
  See IStageConfigProps.ts (https://github.com/spinnaker/deck/blob/master/app/scripts/modules/core/src/pipeline/config/stages/common/IStageConfigProps.ts) for a complete list of properties.
  Pass a JSON object to the `updateStageField` method to add the `maxWaitTime` to the Stage.

  This method returns JSX (https://reactjs.org/docs/introducing-jsx.html) that gets displayed in the Spinnaker UI.
 */
export function VerificationGateConfig(props: IStageConfigProps) {
  const ANALYSIS_TYPE_OPTIONS: any = [
    { label: 'True', value: 'true' },
    { label: 'False', value: 'false' },
  ];
  return (
    <div className="VerificationGateConfig">
      <FormikStageConfig
        {...props}
        onChange={props.updateStage}
        render={(props) => (
          <div className="form-horizontal">
            <div className="flex-container">
              <div className="flex-item-left">
                <FormikFormField
                  name="gateUrl"
                  label="Gate Url"
                  help={<HelpField id="opsmx.verificationGate.gateUrl" />}
                  input={(props) => <TextInput {...props} />}
                />
                <FormikFormField
                  name="lifeTimeHours"
                  label="LifeTimeHours"
                  help={<HelpField id="opsmx.verificationGate.lifeTimeHours" />}
                  input={(props) => <TextInput {...props} />}
                />
                <FormikFormField
                  name="minimumCanaryResult"
                  label="Minimum Canary Result"
                  help={<HelpField id="opsmx.verificationGate.minimumCanaryResult" />}
                  input={(props) => <TextInput {...props} />}
                />
                <FormikFormField
                  name="canaryResultScore"
                  label="canary Result Score"
                  help={<HelpField id="opsmx.verificationGate.canaryResultScore" />}
                  input={(props) => <TextInput {...props} />}
                />
                <FormikFormField
                  name="gateName"
                  label="Gate Name"
                  help={<HelpField id="opsmx.verificationGate.gateName" />}
                  input={(props) => <TextInput {...props} />}
                />
                <FormikFormField
                  name="imageIds"
                  label="Image Ids"
                  help={<HelpField id="opsmx.verificationGate.imageIds" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div className="flex-item-right logoImg">
                <img
                  src="data:image/png;base64,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"
                  alt="logo"
                ></img>
              </div>
            </div>
            <div className="flex-container">
              <div className="flex-item-left">
                <FormikFormField
                  name="logAnalysis"
                  label="Log Analysis"
                  help={<HelpField id="opsmx.verificationGate.logAnalysis" />}
                  input={(props) => <RadioButtonInput {...props} inline={true} options={ANALYSIS_TYPE_OPTIONS} />}
                />
              </div>
              <div className="flex-item-right">
                <FormikFormField
                  name="metricAnalysis"
                  label="Metric Analysis"
                  help={<HelpField id="opsmx.verificationGate.metricAnalysis" />}
                  input={(props) => <RadioButtonInput {...props} inline={true} options={ANALYSIS_TYPE_OPTIONS} />}
                />
              </div>
            </div>
            <div className="flex-container">
              <div className="flex-item-left">
                <FormikFormField
                  name="baselineStartTime"
                  label="Baseline StartTime"
                  help={<HelpField id="opsmx.verificationGate.baselineStartTime" />}
                  input={(props) => <DateTimePicker {...props} />}
                />
              </div>
              <div className="flex-item-right">
                <FormikFormField
                  name="canaryStartTime"
                  label="Canary StartTime"
                  help={<HelpField id="opsmx.verificationGate.canaryStartTime" />}
                  input={(props) => <DateTimePicker {...props} />}
                />
              </div>
            </div>
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
