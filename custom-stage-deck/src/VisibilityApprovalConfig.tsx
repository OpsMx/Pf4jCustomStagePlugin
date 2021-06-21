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
import './VisibilityApproval.less';
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
              <div className="grid-span-4">
                <FormikFormField
                  name="gateUrl"
                  label="Gate Url"
                  help={<HelpField id="opsmx.visibilityApproval.gateUrl" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div className="grid-span-4">
                <FormikFormField
                  name="jiraId"
                  label="Jira Id"
                  help={<HelpField id="opsmx.visibilityApproval.jiraId" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div className="grid-span-4">
                <FormikFormField
                  name="autopilotCanaryId"
                  label="Autopilot Canary Id"
                  help={<HelpField id="opsmx.visibilityApproval.autopilotCanaryId" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div className="grid-span-4">
                <FormikFormField
                  name="sonarqubeProjectKey"
                  label="Sonarqube Project Key"
                  help={<HelpField id="opsmx.visibilityApproval.sonarqubeProjectKey" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div className="grid-span-4">
                <FormikFormField
                  name="appScanProjectId"
                  label="AppScan Project Id"
                  help={<HelpField id="opsmx.visibilityApproval.appScanProjectId" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div className="grid-span-4">
                <FormikFormField
                  name="aquaWaveImageId"
                  label="Aqua Wave Image Id"
                  help={<HelpField id="opsmx.visibilityApproval.aquaWaveImageId" />}
                  input={(props) => <TextInput {...props} />}
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
              <HorizontalRule />
              <p>Git</p>
              <p>Jenkins</p>
              <p>Custom Connector</p>
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
