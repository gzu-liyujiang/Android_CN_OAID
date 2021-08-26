// IdsSupplier.aidl
package com.android.creator;

/**
 * 卓易手机的开放匿名设备标识符接口
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/08/26
 */
interface IdsSupplier {

    boolean isSupported();

    String getUDID(String str);

    String getOAID();

    String getVAID();

    String getAAID(String str);

}