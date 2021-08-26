/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.github.gzuliyujiang.oaid.impl;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.IBinder;
import android.os.RemoteException;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDException;
import com.github.gzuliyujiang.oaid.OAIDLog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import repeackage.com.heytap.openid.IOpenID;

/**
 * 参阅 com.umeng.umsdk:oaid_oppo:1.0.4
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class OppoImpl implements IOAID {
    private final Context context;
    private String sign;

    public OppoImpl(Context context) {
        if (context instanceof Application) {
            this.context = context;
        } else {
            this.context = context.getApplicationContext();
        }
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.heytap.openid", 0);
            return pi != null;
        } catch (Exception e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Intent intent = new Intent("action.com.heytap.openid.OPEN_ID_SERVICE");
        intent.setComponent(new ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService"));
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                try {
                    return realGetOUID(service);
                } catch (OAIDException | RemoteException e) {
                    throw e;
                } catch (Exception e) {
                    throw new OAIDException(e);
                }
            }
        });
    }

    @SuppressLint("PackageManagerGetSignatures")
    private String realGetOUID(IBinder service) throws PackageManager.NameNotFoundException,
            NoSuchAlgorithmException, RemoteException, OAIDException {
        String pkgName = context.getPackageName();
        if (sign == null) {
            Signature[] signatures = context.getPackageManager().getPackageInfo(pkgName,
                    PackageManager.GET_SIGNATURES).signatures;
            byte[] byteArray = signatures[0].toByteArray();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            byte[] digest = messageDigest.digest(byteArray);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
            }
            sign = sb.toString();
            return getSerId(service, pkgName, sign);
        }
        return getSerId(service, pkgName, sign);
    }

    private String getSerId(IBinder service, String pkgName, String sign) throws RemoteException, OAIDException {
        IOpenID anInterface = IOpenID.Stub.asInterface(service);
        if (anInterface == null) {
            throw new OAIDException("IOpenID is null");
        }
        return anInterface.getSerID(pkgName, sign, "OUID");
    }

}
