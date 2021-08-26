// IAdvertisingIdService.aidl
package com.google.android.gms.ads.identifier.internal;

/**
 * 谷歌、索尼、诺基亚、HTC、LG等海外手机开放匿名设备标识符接口
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2020/05/30
 */
interface IAdvertisingIdService {

    String getId();

    boolean isLimitAdTrackingEnabled(boolean boo);

}