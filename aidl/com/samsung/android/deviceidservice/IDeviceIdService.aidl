// IDeviceIdService.aidl
package com.samsung.android.deviceidservice;

/**
 * 三星手机开放匿名设备标识符接口
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2020/05/30
 */
interface IDeviceIdService {

    String getOAID();

    String getVAID(String str);

    String getAAID(String str);

}