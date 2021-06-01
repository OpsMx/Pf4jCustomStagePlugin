import React, { ChangeEvent } from 'react';
import { createFakeReactSyntheticEvent, IFormInputProps, OmitControlledInputPropsFrom } from '@spinnaker/core';

export interface IDateInputProps extends IFormInputProps, OmitControlledInputPropsFrom<React.InputHTMLAttributes<any>> {
  date: number;
}

var epochToLocalTime = (epochString: any) => {
  const inputDate = epochString ? new Date(epochString) : new Date();
  let hours = inputDate.getHours();
  let minutes: number | string = inputDate.getMinutes();
  hours = hours % 24;
  minutes = minutes < 10 ? '0' + minutes : minutes;
  hours = hours ? hours : 24;
  const year = inputDate.getFullYear();
  let month: number | string = inputDate.getMonth();
  month = month < 10 ? '0' + month : month;
  let date: number | string = inputDate.getDate();
  date = date < 10 ? '0' + date : date;
  const strTime = year + '-' + month + '-' + date + 'T' + hours + ':' + minutes;
  return strTime;
};

const localTimeToEpoch = (selectedDate: string) => new Date(selectedDate).getTime() / 1000;
export class DateTimePicker extends React.Component<IDateInputProps> {
  public render() {
    const { date, onChange, name, value, ...rest } = this.props;

    const formattedDate = epochToLocalTime(date);

    return (
      <input
        type="datetime-local"
        value={formattedDate}
        onChange={(e: ChangeEvent<HTMLInputElement>) => {
          const date = e.target.value;
          const newValue = localTimeToEpoch(date);
          this.props.onChange(createFakeReactSyntheticEvent({ name, value: newValue }));
        }}
        {...rest}
      ></input>
    );
  }
}
