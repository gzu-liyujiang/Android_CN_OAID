// IDeviceidInterface.aidl
package com.zui.deviceidservice;

/**
 * 联想、摩托罗拉手机开放匿名设备标识符接口
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2020/05/30
 */
interface IDeviceidInterface {

    String getOAID();

    String getUDID();

    boolean isSupport();

    String getVAID(String str);

    String getAAID(String str);

    String createAAIDForPackageName(String str);

}