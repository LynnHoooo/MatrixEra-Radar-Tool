package com.jstyle.blesdk2208.callback;


import com.jstyle.blesdk2208.model.Device;

public interface OnScanResults {
  void Success(Device date);
  void Fail(int code);
}
