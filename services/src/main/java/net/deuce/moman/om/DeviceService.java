package net.deuce.moman.om;

import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService extends EntityService<Device, DeviceDao> {

  @Autowired
  private DeviceDao deviceDao;

  protected DeviceDao getDao() {
    return deviceDao;
  }

  @Transactional(readOnly = true)
  public Device findByDeviceId(String deviceId) {
    return deviceDao.findDevice(deviceId);
  }

  public void toXml(Device device, Element parent) {
    Element el = parent.addElement("device");
    addElement(el, "id", device.getUuid());
    addElement(el, "deviceId", device.getDeviceId());
    addElement(el, "passcode", device.getPasscode());
    el.addElement("user").addAttribute("id", device.getUser().getUuid());
  }

  public Class<Device> getType() {
    return Device.class;
  }

  public String getRootElementName() {
    return "devices";
  }

}