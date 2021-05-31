import React, { useState } from 'react';

export default function DateTimePicker(props: any) {
  const { date, ...rest } = props;

  let localTimeToEpoch = (selectedDate: string) => new Date(selectedDate).getTime() / 1000;

  let epochToLocalTime = (epochString: number) => {
    let inputDate = new Date(epochString);
    var hours = inputDate.getHours();
    var minutes = inputDate.getMinutes();
    hours = hours % 24;
    hours = hours ? hours : 24;
    minutes = minutes < 10 ? +('0' + minutes) : minutes;
    var year = inputDate.getFullYear();
    var month = inputDate.getMonth();
    month = month < 10 ? +('0' + month) : month;
    var date = inputDate.getDate();
    date = date < 10 ? +('0' + date) : date;
    hours = hours < 10 ? +('0' + hours) : hours;
    minutes = minutes < 10 ? +('0' + minutes) : minutes;
    var strTime = year + '-' + month + '-' + date + 'T' + hours + ':' + minutes;
    return strTime;
  };

  return <input type="datetime-local" value={epocToLocalTime(new Date())} {...props}></input>;
}
