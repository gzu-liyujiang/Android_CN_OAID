/*
 * Copyright (c) 2019-2021 gzu-liyujiang <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 *
 */
package com.github.gzuliyujiang.oaid.impl;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDLog;

import java.lang.reflect.Method;

/**
 * 参阅 http://f4.market.xiaomi.com/download/MiPass/058fc4374ac89aea6dedd9dc03c60a5498241e0dd/DeviceId.jar
 * 即 com.miui.deviceid.IdentifierManager
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class XiaomiImpl implements IOAID {
    private final Context context;
    private Class<?> idProviderClass;
    private Object idProviderImpl;

    @SuppressLint("PrivateApi")
    public XiaomiImpl(Context context) {
        this.context = context;
        try {
            idProviderClass = Class.forName("com.android.id.impl.IdProviderImpl");
            idProviderImpl = idProviderClass.newInstance();
        } catch (Throwable e) {
            OAIDLog.print(e);
        }
    }

    @Override
    public boolean supported() {
        return idProviderImpl != null;
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        if (idProviderClass == null || idProviderImpl == null) {
            getter.onOAIDGetError(new NullPointerException("Xiaomi IdProvider not exists"));
            return;
        }
        try {
            String oaid = invokeMethod("getOAID");
            if (oaid != null && oaid.length() > 0) {
                getter.onOAIDGetComplete(oaid);
            } else {
                throw new RuntimeException("Xiaomi OAID get failed");
            }
        } catch (Throwable e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

    private String invokeMethod(String methodName) {
        String result = null;
        try {
            Method method = idProviderClass.getMethod(methodName, Context.class);
            result = (String) method.invoke(idProviderImpl, context);
        } catch (Throwable e) {
            OAIDLog.print(e);
        }
        return result;
    }

}
