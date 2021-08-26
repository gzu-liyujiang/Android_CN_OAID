// OpenDeviceIdentifierService.aidl
package com.uodis.opendevice.aidl;

/**
 * 华为、荣耀手机开放匿名设备标识符接口
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2020/05/30
 */
interface OpenDeviceIdentifierService {

    String getOaid();

    boolean isOaidTrackLimited();

}