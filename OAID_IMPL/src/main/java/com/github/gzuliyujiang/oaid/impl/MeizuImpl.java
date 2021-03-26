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
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDLog;

import java.util.Objects;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class MeizuImpl implements IOAID {
    private final Context context;

    public MeizuImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        try {
            ProviderInfo pi = context.getPackageManager().resolveContentProvider("com.meizu.flyme.openidsdk", 0);
            if (pi != null) {
                return true;
            }
        } catch (Throwable e) {
            OAIDLog.print(e);
        }
        return false;
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        Uri uri = Uri.parse("content://com.meizu.flyme.openidsdk/");
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, new String[]{"oaid"}, null)) {
            Objects.requireNonNull(cursor).moveToFirst();
            String ret = cursor.getString(cursor.getColumnIndex("value"));
            if (ret == null || ret.length() == 0) {
                throw new RuntimeException("OAID query failed");
            }
            getter.onOAIDGetComplete(ret);
        } catch (Throwable e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

}
