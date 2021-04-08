// IDeviceidInterface.aidl
package com.zui.deviceidservice;

// Declare any non-default types here with import statements

interface IDeviceidInterface {

  String getUDID();

  String getOAID();

  boolean isSupport();

}
