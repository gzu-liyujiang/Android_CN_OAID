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

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class NubiaDeviceIdImpl implements IDeviceId {
    private Context context;

    public NubiaDeviceIdImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supportOAID() {
        return false;
    }

    @Override
    public void doGet(@NonNull IGetter getter) {
        String oaid = null;
        Bundle bundle = null;
        try {
            Uri uri = Uri.parse("content://cn.nubia.identity/identity");
            if (Build.VERSION.SDK_INT > 17) {
                ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(uri);
                if (client != null) {
                    bundle = client.call("getOAID", null, null);
                    if (Build.VERSION.SDK_INT >= 24) {
                        client.close();
                    } else {
                        client.release();
                    }
                }
            } else {
                bundle = context.getContentResolver().call(uri, "getOAID", null, null);
            }
            if (bundle == null) {
                getter.onDeviceIdGetError(new RuntimeException("getOAID call failed"));
                return;
            }
            if (bundle.getInt("code", -1) == 0) {
                oaid = bundle.getString("id");
            }
            String failedMsg = bundle.getString("message");
            if (oaid != null && oaid.length() > 0) {
                getter.onDeviceIdGetComplete(oaid);
            } else {
                getter.onDeviceIdGetError(new RuntimeException(failedMsg));
            }
        } catch (Exception e) {
            Logger.print(e);
            getter.onDeviceIdGetError(e);
        }
    }

}
