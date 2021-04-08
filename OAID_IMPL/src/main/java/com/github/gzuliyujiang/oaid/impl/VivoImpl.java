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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDLog;
import com.github.gzuliyujiang.oaid.OAIDRom;

import java.util.Objects;

/**
 * 参阅 com.umeng.umsdk:oaid_vivo:1.0.0.1
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class VivoImpl implements IOAID {
    private final Context context;

    public VivoImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        return OAIDRom.sysProperty("persist.sys.identifierid.supported", "0").equals("1");
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        Uri uri = Uri.parse("content://com.vivo.vms.IdProvider/IdentifierId/OAID");
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
            Objects.requireNonNull(cursor).moveToFirst();
            String ret = cursor.getString(cursor.getColumnIndex("value"));
            if (ret != null && ret.length() > 0) {
                OAIDLog.print("oaid from provider: " + uri);
                getter.onOAIDGetComplete(ret);
            } else {
                throw new RuntimeException("OAID query failed");
            }
        } catch (Throwable e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

}
