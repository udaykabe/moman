package net.deuce.moman.controller;

import net.deuce.moman.om.DeviceService;
import net.deuce.moman.om.EntityService;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceController extends DispatcherController {

  @Autowired
  private DeviceService deviceService;

  protected EntityService getService() {
    return deviceService;
  }
}