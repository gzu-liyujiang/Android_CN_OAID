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
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDLog;

import java.lang.reflect.Method;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class XiaomiImpl implements IOAID {
    private final Context context;
    private Class<?> idProvider;

    @SuppressLint("PrivateApi")
    public XiaomiImpl(Context context) {
        this.context = context;
        try {
            idProvider = Class.forName("com.android.id.impl.IdProviderImpl");
        } catch (Throwable e) {
            OAIDLog.print(e);
        }
    }

    @Override
    public boolean supported() {
        return idProvider != null;
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        if (idProvider == null) {
            getter.onOAIDGetError(new NullPointerException("Xiaomi IdProvider not exists"));
            return;
        }
        try {
            Method udidMethod = idProvider.getMethod("getDefaultUDID", Context.class);
            String did = invokeMethod(udidMethod);
            if (did != null && did.length() > 0) {
                getter.onOAIDGetComplete(did);
                return;
            }
            Method oaidMethod = idProvider.getMethod("getOAID", Context.class);
            did = invokeMethod(oaidMethod);
            if (did != null && did.length() > 0) {
                getter.onOAIDGetComplete(did);
            } else {
                throw new RuntimeException("Xiaomi OAID get failed");
            }
        } catch (Throwable e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

    private String invokeMethod(Method method) {
        String result = null;
        if (method != null) {
            try {
                result = (String) method.invoke(idProvider.newInstance(), context);
            } catch (Throwable e) {
                OAIDLog.print(e);
            }
        }
        return result;
    }

}
