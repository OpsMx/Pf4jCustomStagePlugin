import React, { Fragment } from 'react';

import { ExecutionDetailsSection, IExecutionDetailsSectionProps, StageFailureMessage } from '@spinnaker/core';
import './VerificationGate.less';

/*
 * You can use this component to provide information to users about
 * how the stage was configured and the results of its execution.
 *
 * In general, you will access two properties of `props.stage`:
 * - `props.stage.outputs` maps to your SimpleStage's `Output` class.
 * - `props.stage.context` maps to your SimpleStage's `Context` class.
 */

export function VerificationGateExecutionDetails(props: IExecutionDetailsSectionProps) {
  return (
    <ExecutionDetailsSection name={props.name} current={props.current}>
      {props.stage.outputs.overallScore >= 0 ? (
        <div>
          <div className="detailpagelogo">
            <span className="score">{props.stage.outputs.overallScore}</span>
            <img
              src="https://cd.foundation/wp-content/uploads/sites/78/2020/05/opsmx-logo-march2019.png"
              alt="logo"
              width="70px"
            ></img>
          </div>
          <table className="table">
            <thead>
              <tr>
                <th>Result</th>
                <th>Report</th>
                <th>Last Updated</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>
                  <span className="scoreSmall">{props.stage.outputs.overallScore}</span>
                </td>
                <td>
                  <a href={props.stage.outputs.canaryReportURL} target="_blank">
                    Report
                  </a>
                </td>
                <td>{new Date(props.stage.endTime).toLocaleString()}</td>
              </tr>
            </tbody>
          </table>
        </div>
      ) : (
        <>
          {' '}
          <img
            src="https://cd.foundation/wp-content/uploads/sites/78/2020/05/opsmx-logo-march2019.png"
            alt="logo"
            width="80px"
            style={{ float: 'right', marginBottom: '10px' }}
          ></img>
          <StageFailureMessage stage={props.stage} message={props.stage.failureMessage} />)
        </>
      )}
    </ExecutionDetailsSection>
  );
}

// The title here will be used as the tab name inside the
// pipeline stage execution view. Camel case will be mapped
// to space-delimited text: randomWait -> Random Wait.
export namespace VerificationGateExecutionDetails {
  export const title = 'verificationGate';
}
