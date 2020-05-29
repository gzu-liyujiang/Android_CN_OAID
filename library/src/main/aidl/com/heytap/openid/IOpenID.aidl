// IOpenID.aidl
package com.heytap.openid;

// Declare any non-default types here with import statements

interface IOpenID {

     String getSerID(String pkgName, String sign, String type);

}
