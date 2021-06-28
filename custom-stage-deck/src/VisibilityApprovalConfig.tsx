import React from 'react';
import {
  ExecutionDetailsSection,
  ExecutionDetailsTasks,
  FormikFormField,
  FormikStageConfig,
  FormValidator,
  IFormInputProps,
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
  LayoutProvider,
  StandardFieldLayout,
  IFormikStageConfigInjectedProps,
  IStageForSpelPreview,
} from '@spinnaker/core';
import './VisibilityApproval.less';
import { EvaluateVariablesStageForm } from './input/dynamicFormFields';

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
  const [chosenStage] = React.useState({} as IStageForSpelPreview);
  const gitHeaders = [
    { label: 'Git Repo', name: 'gitRepo' },
    { label: 'Git Commit ID', name: 'gitCommitId' },
  ];
  const jenkinsHeaders = [
    { label: 'Job name', name: 'jobName' },
    { label: 'Build No.', name: 'buildNo' },
    { label: 'Artifact', name: 'artifact' },
  ];
  const customConnectorHeaders = [
    { label: 'Name', name: 'name' },
    { label: 'Header', name: 'header' },
    { label: 'Data', name: 'data' },
  ];
  return (
    <div className="VisibilityApprovalConfig">
      <FormikStageConfig
        {...props}
        onChange={props.updateStage}
        render={(props: IFormikStageConfigInjectedProps) => (
          <div className="flex">
            <div className="grid leftGrid"></div>
            <div className="grid grid-4 form mainForm">
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
              <div className="grid-span-2 gridHalfSpan">
                <FormikFormField
                  name="gateName"
                  label="Gate Name"
                  help={<HelpField id="opsmx.visibilityApproval.gateName" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <div className="grid-span-2 gridHalfSpan">
                <FormikFormField
                  name="imageIds"
                  label="Image Ids"
                  help={<HelpField id="opsmx.visibilityApproval.imageIds" />}
                  input={(props) => <TextInput {...props} />}
                />
              </div>
              <HorizontalRule />
              <div className="grid-span-4">
                <FormikFormField
                  name="git"
                  label="Git"
                  help={<HelpField id="opsmx.visibilityApproval.git" />}
                  input={() => (
                    <LayoutProvider value={StandardFieldLayout}>
                      <div className="flex-container-v margin-between-lg dynamicFieldSection">
                        <EvaluateVariablesStageForm
                          blockLabel="git"
                          chosenStage={chosenStage}
                          headers={gitHeaders}
                          {...props}
                        />
                      </div>
                    </LayoutProvider>
                  )}
                />
              </div>
              <div className="grid-span-4">
                <FormikFormField
                  name="jenkins"
                  label="Jenkins"
                  help={<HelpField id="opsmx.visibilityApproval.jenkins" />}
                  input={() => (
                    <LayoutProvider value={StandardFieldLayout}>
                      <div className="flex-container-v margin-between-lg dynamicFieldSection">
                        <EvaluateVariablesStageForm
                          blockLabel="jenkins"
                          chosenStage={chosenStage}
                          headers={jenkinsHeaders}
                          {...props}
                        />
                      </div>
                    </LayoutProvider>
                  )}
                />
              </div>
              <div className="grid-span-4">
                <FormikFormField
                  name="customConnector"
                  label="Custom Connector"
                  help={<HelpField id="opsmx.visibilityApproval.customConnector" />}
                  input={() => (
                    <LayoutProvider value={StandardFieldLayout}>
                      <div className="flex-container-v margin-between-lg dynamicFieldSection">
                        <EvaluateVariablesStageForm
                          blockLabel="customConnector"
                          chosenStage={chosenStage}
                          headers={customConnectorHeaders}
                          {...props}
                        />
                      </div>
                    </LayoutProvider>
                  )}
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
    .field('jiraId')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('autopilotCanaryId')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('sonarqubeProjectKey')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('appScanProjectId')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  validator
    .field('aquaWaveImageId')
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
  validator
    .field('git')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));
  validator
    .field('jenkins')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));
  validator
    .field('customConnector')
    .required()
    .withValidators((value, label) => (value = '' ? `${label} is required` : undefined));

  return validator.validateForm();
}
