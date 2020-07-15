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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * 随机生成一个全局唯一标识，通过`SharedPreferences`及`ExternalStorage`进行永久化存储。
 * 注：非系统及预装APP无法获得`WRITE_SETTINGS`权限，故放弃使用`Settings`进行永久化存储。
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DefaultDeviceIdImpl implements IDeviceId {
    private Context context;

    public DefaultDeviceIdImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supportOAID() {
        return false;
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        Executors.newSingleThreadExecutor().execute(() -> {
            String guid = readGuid();
            if (guid == null) {
                guid = UUID.randomUUID().toString();
                Logger.print("generate guid: " + guid);
                writeToSharedPreferences(guid);
                writeToExternalStorage(guid);
            }
            String finalGuid = guid;
            new Handler(Looper.getMainLooper()).post(() ->
                    getter.onDeviceIdGetComplete(finalGuid));
        });
    }

    @Nullable
    private String readGuid() {
        String guid = readFromSharedPreferences();
        if (guid != null) {
            Logger.print("read guid from SharedPreferences: " + guid);
            return guid;
        }
        guid = readFromExternalStorage();
        if (guid != null) {
            Logger.print("read guid from ExternalStoragePublicDirectory: " + guid);
            writeToSharedPreferences(guid);
            return guid;
        }
        return null;
    }

    @Nullable
    private String readFromSharedPreferences() {
        SharedPreferences sp = context.getSharedPreferences(".OAID", Context.MODE_PRIVATE);
        return sp.getString("l__y__j", null);
    }

    private void writeToSharedPreferences(String guid) {
        Logger.print("write guid to SharedPreferences: " + guid);
        SharedPreferences sp = context.getSharedPreferences(".OAID", Context.MODE_PRIVATE);
        sp.edit().putString("l__y__j", guid).apply();
    }

    @Nullable
    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    private String readFromExternalStorage() {
        String result = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory("Android"), ".OAID");
            if (file.isDirectory() || !file.isFile()) {
                Logger.print("The OAID file doesn't not exist.");
                return null;
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            InputStream stream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();
            if (line != null) {
                result = line;
            }
            stream.close();
        } catch (Exception e) {
            Logger.print(e);
        }
        return result;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "deprecation"})
    private void writeToExternalStorage(String guid) {
        Logger.print("write guid to ExternalStoragePublicDirectory: " + guid);
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory("Android"), ".OAID");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(guid.getBytes());
            fos.close();
        } catch (Exception e) {
            Logger.print(e);
        }
    }

}
