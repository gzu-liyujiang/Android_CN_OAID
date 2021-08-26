// IDidAidlInterface.aidl
package com.asus.msa.SupplementaryDID;

/**
 * 华硕手机开放匿名设备标识符接口
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2020/05/30
 */
interface IDidAidlInterface {

    boolean isSupport();

    String getUDID();

    String getOAID();

    String getVAID();

    String getAAID();

}