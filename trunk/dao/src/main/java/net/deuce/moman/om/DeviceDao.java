package net.deuce.moman.om;

import org.hibernate.Query;

public class DeviceDao extends EntityDao<Device> {

  public Device findDevice(String deviceId) {
    Query query = getSession().createQuery(String.format("select e from %s e where e.deviceId = :deviceId",
        User.class.getName()));
    query.setParameter("deviceId", deviceId);
    return (Device) query.uniqueResult();
  }
}