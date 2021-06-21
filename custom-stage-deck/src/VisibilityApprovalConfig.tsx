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
import './Verification.less';
import { DateTimePicker } from './input/DateTimePickerInput';

/*
  IStageConfigProps defines properties passed to all Spinnaker Stages.
  See IStageConfigProps.ts (https://github.com/spinnaker/deck/blob/master/app/scripts/modules/core/src/pipeline/config/stages/common/IStageConfigProps.ts) for a complete list of properties.
  Pass a JSON object to the `updateStageField` method to add the `maxWaitTime` to the Stage.

  This method returns JSX (https://reactjs.org/docs/introducing-jsx.html) that gets displayed in the Spinnaker UI.
 */

const HorizontalRule = () => (
  <div className="grid-span-4">
    <hr />
  </div>
);

export function VisibilityApprovalConfig(props: IStageConfigProps) {
  const ANALYSIS_TYPE_OPTIONS: any = [
    { label: 'True', value: 'true' },
    { label: 'False', value: 'false' },
  ];
  return (
    <div className="VisibilityApprovalConfig">
      <FormikStageConfig
        {...props}
        onChange={props.updateStage}
        render={() => (
          <div className="flex">
            <div className="grid"></div>
            <div className="grid grid-4 form">
              <div className="grid-span-3">
                <FormikFormField
                  name="gateUrl"
                  label="Gate Url"
                  help={<HelpField id="opsmx.visibilityApproval.gateUrl" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div>
                <FormikFormField
                  name="lifeTimeHours"
                  label="LifeTimeHours"
                  help={<HelpField id="opsmx.visibilityApproval.lifeTimeHours" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <HorizontalRule />
              <div>
                <FormikFormField
                  name="minimumCanaryResult"
                  label="Minimum Canary Result"
                  help={<HelpField id="opsmx.visibilityApproval.minimumCanaryResult" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div>
                <FormikFormField
                  name="canaryResultScore"
                  label="canary Result Score"
                  help={<HelpField id="opsmx.visibilityApproval.canaryResultScore" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div style={{ paddingLeft: '4em' }}>
                <FormikFormField
                  name="logAnalysis"
                  label="Log Analysis"
                  help={<HelpField id="opsmx.visibilityApproval.logAnalysis" />}
                  input={(props) => <RadioButtonInput {...props} inline={true} options={ANALYSIS_TYPE_OPTIONS} />}
                />
              </div>
              <div style={{ paddingLeft: '4em' }}>
                <FormikFormField
                  name="metricAnalysis"
                  label="Metric Analysis"
                  help={<HelpField id="opsmx.visibilityApproval.metricAnalysis" />}
                  input={(props) => <RadioButtonInput {...props} inline={true} options={ANALYSIS_TYPE_OPTIONS} />}
                />
              </div>
              <HorizontalRule />
              <div className="grid-span-2">
                <FormikFormField
                  name="baselineStartTime"
                  label="Baseline StartTime"
                  help={<HelpField id="opsmx.visibilityApproval.baselineStartTime" />}
                  input={(props) => <DateTimePicker {...props} />}
                />
              </div>
              <div className="grid-span-2">
                <FormikFormField
                  name="canaryStartTime"
                  label="Canary StartTime"
                  help={<HelpField id="opsmx.visibilityApproval.canaryStartTime" />}
                  input={(props) => <DateTimePicker {...props} />}
                />
              </div>
              <HorizontalRule />
              <div className="grid-span-2">
                <FormikFormField
                  name="gateName"
                  label="Gate Name"
                  help={<HelpField id="opsmx.visibilityApproval.gateName" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div className="grid-span-2">
                <FormikFormField
                  name="imageIds"
                  label="Image Ids"
                  help={<HelpField id="opsmx.visibilityApproval.imageIds" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
            </div>
            <div className="opsmxLogo">
              <img
                src="https://cd.foundation/wp-content/uploads/sites/78/2020/05/opsmx-logo-march2019.png"
                alt="logo"
              ></img>
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
    .field('gateUrl')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('lifeTimeHours')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('minimumCanaryResult')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('canaryResultScore')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('logAnalysis')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('metricAnalysis')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('gateName')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('imageIds')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator.field('baselineStartTime').required();

  validator.field('canaryStartTime').required();

  return validator.validateForm();
}
