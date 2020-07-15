/*
 * Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v1.
 * You can use this software according to the terms and conditions of the Mulan PSL v1.
 * You may obtain a copy of Mulan PSL v1 at:
 *     http://license.coscl.org.cn/MulanPSL
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v1 for more details.
 *
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
