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
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;

import java.lang.reflect.Method;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class XiaomiDeviceIdImpl implements IDeviceId {
    private Context context;
    private Class idProvider;

    public XiaomiDeviceIdImpl(Context context) {
        this.context = context;
    }

    @SuppressLint("PrivateApi")
    @Override
    public boolean supportOAID() {
        try {
            idProvider = Class.forName("com.android.id.impl.IdProviderImpl");
            return true;
        } catch (Exception e) {
            Logger.print(e);
            return false;
        }
    }

    @SuppressLint("PrivateApi")
    @SuppressWarnings("unchecked")
    @Override
    public void doGet(@NonNull IGetter getter) {
        if (idProvider == null) {
            try {
                idProvider = Class.forName("com.android.id.impl.IdProviderImpl");
            } catch (Exception e) {
                Logger.print(e);
            }
        }
        String did = null;
        try {
            Method udidMethod = idProvider.getMethod("getDefaultUDID", Context.class);
            did = invokeMethod(udidMethod);
        } catch (Exception e) {
            Logger.print(e);
        }
        if (did != null && did.length() > 0) {
            getter.onDeviceIdGetComplete(did);
            return;
        }
        try {
            Method oaidMethod = idProvider.getMethod("getOAID", Context.class);
            did = invokeMethod(oaidMethod);
            if (did != null && did.length() > 0) {
                getter.onDeviceIdGetComplete(did);
            } else {
                getter.onDeviceIdGetError(new RuntimeException("Xiaomi OAID get failed"));
            }
        } catch (Exception e) {
            Logger.print(e);
            getter.onDeviceIdGetError(e);
        }
    }

    private String invokeMethod(Method method) {
        String result = null;
        if (method != null) {
            try {
                result = (String) method.invoke(idProvider.newInstance(), context);
            } catch (Exception e) {
                Logger.print(e);
            }
        }
        return result;
    }

}
