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
import DateTimePicker from './input/DateTimePickerInput';

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
              name="logAnalysis"
              label="Log Analysis"
              help={<HelpField id="opsmx.verificationGate.logAnalysis" />}
              input={(props) => <RadioButtonInput {...props} inline={true} options={ANALYSIS_TYPE_OPTIONS} />}
            />
            <FormikFormField
              name="metricAnalysis"
              label="Metric Analysis"
              help={<HelpField id="opsmx.verificationGate.metricAnalysis" />}
              input={(props) => <RadioButtonInput {...props} inline={true} options={ANALYSIS_TYPE_OPTIONS} />}
            />
            <FormikFormField
              name="baselineStartTime"
              label="Baseline StartTime"
              help={<HelpField id="opsmx.verificationGate.baselineStartTime" />}
              input={(props) => <DateTimePicker {...props} />}
            />
            <FormikFormField
              name="canaryStartTime"
              label="Canary StartTime"
              help={<HelpField id="opsmx.verificationGate.canaryStartTime" />}
              input={(props) => <DateTimePicker {...props} />}
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
