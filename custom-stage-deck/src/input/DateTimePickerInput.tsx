import React, { useState } from 'react';

export default function DateTimePicker(props: any) {
  return <input type="datetime-local" {...props}></input>;
}
