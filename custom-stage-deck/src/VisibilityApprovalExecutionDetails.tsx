import React, { Fragment, useCallback, useMemo } from 'react';

import { ExecutionDetailsSection, IExecutionDetailsSectionProps, StageFailureMessage } from '@spinnaker/core';
import './VisibilityApproval.less';

/*
 * You can use this component to provide information to users about
 * how the stage was configured and the results of its execution.
 *
 * In general, you will access two properties of `props.stage`:
 * - `props.stage.outputs` maps to your SimpleStage's `Output` class.
 * - `props.stage.context` maps to your SimpleStage's `Context` class.
 */

export function VisibilityApprovalExecutionDetails(props: IExecutionDetailsSectionProps) {
  const getClasses = () => {
    let classes = '';
    if (props.stage.outputs.status == 'approved') {
      classes = 'approvalStatusSuccess';
    } else if (props.stage.outputs.status == 'rejected') {
      classes = 'approvalStatusDanger';
    }
    return classes;
  };

  const getStatus = () => {
    let classes = '';
    if (props.stage.outputs.status == 'approved') {
      classes = 'Approved';
    } else if (props.stage.outputs.status == 'rejected') {
      classes = 'Rejected';
    }
    return classes;
  };
  const exceptionDiv = props.stage.outputs.exception ? (
    <div className="alert alert-danger">
      <div>
        <h5>Exception </h5>
        <div className="Markdown break-word">
          <p>{props.stage.outputs.exception}</p>
        </div>
      </div>
    </div>
  ) : null;

  return (
    <ExecutionDetailsSection name={props.name} current={props.current}>
      {props.stage.outputs.status !== undefined ? (
        <div>
          <div className="detailpagelogo">
            <span className={'approvalStatus ' + getClasses()}>{getStatus()}</span>
            <img
              src="https://cd.foundation/wp-content/uploads/sites/78/2020/05/opsmx-logo-march2019.png"
              alt="logo"
              width="70px"
            ></img>
          </div>
          <table className="table">
            <thead>
              <tr>
                <th>Status</th>
                <th>Comment</th>
                <th>Last Updated</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>
                  <span className={'approvalStatusSmall ' + getClasses()}>{getStatus()}</span>
                </td>
                <td>
                  <pre className={'approvalCommentSection'}>{props.stage.outputs.comments}</pre>
                </td>
                <td>{new Date(props.stage.endTime).toLocaleString()}</td>
              </tr>
            </tbody>
          </table>
          {exceptionDiv}
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
          <StageFailureMessage stage={props.stage} message={props.stage.failureMessage} />
        </>
      )}
    </ExecutionDetailsSection>
  );
}

// The title here will be used as the tab name inside the
// pipeline stage execution view. Camel case will be mapped
// to space-delimited text: randomWait -> Random Wait.
export namespace VisibilityApprovalExecutionDetails {
  export const title = 'Approval';
}
