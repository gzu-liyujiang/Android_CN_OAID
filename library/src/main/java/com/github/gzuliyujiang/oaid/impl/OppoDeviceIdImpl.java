/*
 * MIT License
 *
 * Copyright (c) 2020 贵州穿青人@李裕江 <1032694760@qq.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.gzuliyujiang.oaid.impl;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;
import com.heytap.openid.IOpenID;

import java.lang.reflect.Method;
import java.security.MessageDigest;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class OppoDeviceIdImpl implements IDeviceId {
    private Context context;
    private String sign;

    public OppoDeviceIdImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supportOAID() {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.heytap.openid", 0);
            return pi != null;
        } catch (Exception e) {
            Logger.print(e);
            return false;
        }
    }

    @Override
    public void doGet(@NonNull IGetter getter) {
        Intent intent = new Intent("action.com.heytap.openid.OPEN_ID_SERVICE");
        intent.setComponent(new ComponentName("com.heytap.openid", "com.heytap.openid.IdentifyService"));
        boolean isBinded = context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Logger.print("HeyTap IdentifyService connected");
                try {
                    String ouid = realGetOUID(service);
                    if (ouid == null || ouid.length() == 0) {
                        getter.onDeviceIdGetError(new RuntimeException("HeyTap OUID get failed"));
                    } else {
                        getter.onDeviceIdGetComplete(ouid);
                    }
                } catch (Exception e) {
                    Logger.print(e);
                    getter.onDeviceIdGetError(e);
                } finally {
                    context.unbindService(this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Logger.print("HeyTap IdentifyService disconnected");
            }
        }, Context.BIND_AUTO_CREATE);
        if (!isBinded) {
            getter.onDeviceIdGetError(new RuntimeException("HeyTap IdentifyService bind failed"));
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    private String realGetOUID(IBinder service) {
        String pkgName = context.getPackageName();
        if (sign == null) {
            try {
                Signature[] signatures = context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES).signatures;
                byte[] byteArray = signatures[0].toByteArray();
                MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
                if (messageDigest != null) {
                    byte[] digest = messageDigest.digest(byteArray);
                    StringBuilder sb = new StringBuilder();
                    for (byte b : digest) {
                        sb.append(Integer.toHexString((b & 255) | 256).substring(1, 3));
                    }
                    sign = sb.toString();
                }
                //IOpenID anInterface = new IOpenID.Stub.asInterface(service);
                Method asInterface = IOpenID.Stub.class.getDeclaredMethod("asInterface", IBinder.class);
                IOpenID anInterface = (IOpenID) asInterface.invoke(null, service);
                return anInterface.getSerID(pkgName, sign, "OUID");
            } catch (Exception e) {
                Logger.print(e);
            }
        }
        return null;
    }

}
