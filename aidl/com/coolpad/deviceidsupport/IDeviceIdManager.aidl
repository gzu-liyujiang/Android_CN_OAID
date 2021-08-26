//IDeviceIdManager.aidl
package com.coolpad.deviceidsupport;

/**
 * 酷派手机开放匿名设备标识符接口
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/08/26
 */
interface IDeviceIdManager {

    String getUDID(String str);

    String getOAID(String str);

    String getVAID(String str);

    String getAAID(String str);

    String getIMEI(String str);

    boolean isCoolOs();

    String getCoolOsVersion();

}