// MsaIdInterface.aidl
package com.bun.lib;

/**
 * 移动安全联盟定义的开放匿名设备标识符接口
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2020/05/30
 */
interface MsaIdInterface {

    boolean isSupported();

    boolean isDataArrived();

    String getOAID();

    String getVAID();

    String getAAID();

    void shutDown();

}
