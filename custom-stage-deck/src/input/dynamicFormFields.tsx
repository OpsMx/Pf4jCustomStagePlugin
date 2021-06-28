import React from 'react';
import {
  FormikFormField,
  IFormikStageConfigInjectedProps,
  ILayoutProps,
  IStageForSpelPreview,
  LayoutContext,
  TextInput,
  Tooltip,
  useIsMountedRef,
  ValidationMessage,
} from '@spinnaker/core';
import { FieldArray, FormikProvider } from 'formik';

interface IEvaluateVariablesStageFormProps extends IFormikStageConfigInjectedProps {
  chosenStage: IStageForSpelPreview;
  headers: Array<{ name: string; label: string }>;
  blockLabel: string;
}
export function EvaluateVariablesStageForm(props: IEvaluateVariablesStageFormProps) {
  const { formik, headers, blockLabel } = props;
  const stage = props.formik.values;
  // const { variables = [] } = stage;
  // eslint-disable-next-line no-debugger
  const variables = stage[blockLabel] ?? [];
  const isMountedRef = useIsMountedRef();
  const emptyValue = (() => {
    const obj: any = {};
    headers.forEach((header) => {
      obj[header.name] = null;
    });
    return obj;
  })();
  React.useEffect(() => {
    if (variables.length === 0) {
      // This setTimeout is necessary because the interaction between pipelineConfigurer.js and stage.module.js
      // causes this component to get mounted multiple times.  The second time it gets mounted, the initial
      // variable is already added to the array, and then gets auto-touched by SpinFormik.tsx.
      // The end effect is that the red validation warnings are shown immediately when the Evaluate Variables stage is added.
      setTimeout(() => isMountedRef.current && formik.setFieldValue(blockLabel, [emptyValue]), 100);
    }
  }, [variables]);
  const FieldLayoutComponent = React.useContext(LayoutContext);
  const [deleteCount, setDeleteCount] = React.useState(0);
  return (
    <>
      <table>
        <thead>
          <tr>
            {headers.map((header) => (
              <th key={header.name}>{header.label}</th>
            ))}
          </tr>
        </thead>
        <FormikProvider value={formik}>
          <FieldArray
            key={deleteCount}
            name={blockLabel}
            render={(arrayHelpers) => (
              <>
                <FieldLayoutComponent input={null} validation={{ hidden: true } as any} />
                {variables.map((_: any, index: number) => {
                  const onDeleteClicked = () => {
                    setDeleteCount((count) => count + 1);
                    arrayHelpers.handleRemove(index)();
                  };
                  return (
                    <tr key={`${deleteCount}-${index}`}>
                      {headers.map((header) => (
                        <td key={`${header.name}-td`}>
                          <FormikFormField
                            name={`${blockLabel}[${index}][${header.name}]`}
                            required={true}
                            input={(inputProps) => <TextInput {...inputProps} placeholder={` Enter ${header.label}`} />}
                            layout={VariableNameFormLayout}
                          />
                        </td>
                      ))}
                      <td>
                        <Tooltip value="Remove variable">
                          <button className="btn btn-sm btn-default" onClick={onDeleteClicked}>
                            <span className="glyphicon glyphicon-trash" />
                          </button>
                        </Tooltip>
                      </td>
                    </tr>
                  );
                })}
                <tr>
                  <td colSpan={headers.length}>
                    <button
                      type="button"
                      className="btn btn-block btn-sm add-new"
                      onClick={arrayHelpers.handlePush(emptyValue)}
                    >
                      <span className="glyphicon glyphicon-plus-sign" />
                      Add {blockLabel}
                    </button>
                  </td>
                </tr>
              </>
            )}
          />
        </FormikProvider>
      </table>
    </>
  );
}
function VariableNameFormLayout(props: ILayoutProps) {
  const { input, validation } = props;
  const { messageNode, category, hidden } = validation;
  return (
    <div className="flex-container-v margin-between-md">
      {input}
      {!hidden && <ValidationMessage message={messageNode} type={category} />}
    </div>
  );
}
