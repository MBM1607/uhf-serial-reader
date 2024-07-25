import * as React from 'react';

import { UhfSerialReaderViewProps } from './UhfSerialReader.types';

export default function UhfSerialReaderView(props: UhfSerialReaderViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}
