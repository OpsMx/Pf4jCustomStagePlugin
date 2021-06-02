import React, { ChangeEvent } from 'react';
import { createFakeReactSyntheticEvent, IFormInputProps } from '@spinnaker/core';

const epochToDate = (epoch: number) => {
  const d = new Date(0); // The 0 there is the key, which sets the date to the epoch
  d.setUTCMilliseconds(epoch);
  return d;
};

const epochToLocalTime = (epochString: any) => {
  const inputDate = epochString ? epochToDate(epochString) : new Date();
  let hours = inputDate.getHours();
  let minutes: number | string = inputDate.getMinutes();
  hours = hours % 24;
  minutes = minutes < 10 ? '0' + minutes : minutes;
  hours = hours ? hours : 24;
  const year = inputDate.getFullYear();
  let month: number | string = inputDate.getMonth() + 1;
  month = month < 10 ? '0' + month : month;
  let date: number | string = inputDate.getDate();
  date = date < 10 ? '0' + date : date;
  const strTime = year + '-' + month + '-' + date + 'T' + hours + ':' + minutes;
  return strTime;
};

const localTimeToEpoch = (selectedDate: string) => new Date(selectedDate).getTime();
export class DateTimePicker extends React.Component<IFormInputProps> {
  public render() {
    const { onChange, name, value } = this.props;

    const formattedDate = epochToLocalTime(value);

    return (
      <input
        type="datetime-local"
        {...this.props}
        value={formattedDate}
        onChange={(e: ChangeEvent<HTMLInputElement>) => {
          const date = e.target.value;
          const newValue = localTimeToEpoch(date);
          onChange(createFakeReactSyntheticEvent({ name, value: newValue }));
        }}
      ></input>
    );
  }
}
