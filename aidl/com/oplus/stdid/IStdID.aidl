// IStdID.aidl
package com.oplus.stdid;

/**
 * OPPO、OnePlus老版手机开放匿名设备标识符接口
 *
 * @author luoyesiqiu
 * @since 2023/10/28 09:13
 */
interface IStdID {

    String getSerID(String pkgName, String sign, String type);

}
