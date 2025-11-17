// IOAIDInterface.aidl
package com.qiku.id;

/**
 * 360OS手机开放匿名设备标识符接口
 *
 * @author 10cl
 * @since 2024/03/06
 */
interface IOAIDInterface {

    boolean isSupported();

    String getUDID();

    String getOAID();

    String getVAID();

    String getAAID();

    void shutDown();

    void resetOAID();

    boolean limitReadOAID();

}
