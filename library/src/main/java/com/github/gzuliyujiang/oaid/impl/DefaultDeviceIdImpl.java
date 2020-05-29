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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
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
 * 随机生成一个全局唯一标识，通过{@link SharedPreferences}及{@link Environment#getExternalStoragePublicDirectory(String)}进行永久化存储。
 * 注：APP无法获得{@link android.Manifest.permission#WRITE_SETTINGS}权限，故放弃使用{@link android.provider.Settings.System}进行存储。
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
    public void doGet(@NonNull IGetter getter) {
        String guid = readGuid();
        if (TextUtils.isEmpty(guid)) {
            guid = UUID.randomUUID().toString();
            Logger.print("generate guid: " + guid);
            getter.onDeviceIdGetComplete(guid);
            String finalGuid = guid;
            Executors.newSingleThreadExecutor().execute(() -> {
                writeToSharedPreferences(finalGuid);
                writeToExternalStorage(finalGuid);
            });
        } else {
            getter.onDeviceIdGetComplete(guid);
        }
    }

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

    private String readFromSharedPreferences() {
        SharedPreferences sp = context.getSharedPreferences(".OAID", Context.MODE_PRIVATE);
        return sp.getString("l__y__j", null);
    }

    private void writeToSharedPreferences(String guid) {
        Logger.print("write guid to SharedPreferences: " + guid);
        SharedPreferences sp = context.getSharedPreferences(".OAID", Context.MODE_PRIVATE);
        sp.edit().putString("l__y__j", guid).apply();
    }

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
