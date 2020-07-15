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

package com.github.gzuliyujiang.oaid;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.FileUtils;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 通过{@link Uri}访问外部存储文件，支持{@link ContentResolver#SCHEME_FILE}及{@link ContentResolver#SCHEME_CONTENT}
 * Created by liyujiang on 2020/6/4.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@SuppressWarnings("unused")
public final class ContentUtils {

    private ContentUtils() {
        throw new UnsupportedOperationException("You can't instantiate me");
    }

    public static String readString(Context context, Uri uri) {
        try {
            return readStringThrown(context, uri);
        } catch (Throwable e) {
            Logger.print(e);
            return "";
        }
    }

    public static String readStringThrown(Context context, Uri uri) throws Throwable {
        byte[] bytes = readBytesThrown(context.getContentResolver().openInputStream(uri));
        return new String(bytes);
    }

    @Nullable
    public static byte[] readBytes(Context context, Uri uri) {
        try {
            return readBytesThrown(context, uri);
        } catch (Throwable e) {
            Logger.print(e);
        }
        return null;
    }

    @NonNull
    public static byte[] readBytesThrown(Context context, Uri uri) throws Throwable {
        if (uri == null) {
            throw new NullPointerException("uri is empty, can not read content");
        }
        Logger.print(String.format("read content from uri: %s", uri));
        ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = Objects.requireNonNull(pfd).getFileDescriptor();
        FileInputStream is = new FileInputStream(fileDescriptor);
        byte[] content = readBytesThrown(is);
        FileUtils.closeQuietly(is);
        pfd.close();
        return content;
    }

    @NonNull
    public static byte[] readBytesThrown(InputStream is) throws IOException {
        if (is == null) {
            throw new IOException("input stream is null");
        }
        DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 数据输入流转为字节数组输出流所用缓冲大小，过小会造成读取太慢： 1024 * 1024 = 1MB
        byte[] buff = new byte[1024 * 1024];
        while (true) {
            int len = dis.read(buff);
            if (len == -1) {
                break;
            } else {
                os.write(buff, 0, len);
            }
        }
        byte[] bytes = os.toByteArray();
        FileUtils.closeQuietly(os);
        FileUtils.closeQuietly(dis);
        return bytes;
    }

    public static void writeBytes(Context context, Uri uri, byte[] content) {
        try {
            writeBytesThrown(context, uri, content);
        } catch (Throwable e) {
            Logger.print(e);
        }
    }

    public static void writeBytesThrown(Context context, Uri uri, byte[] content) throws Throwable {
        if (uri == null) {
            throw new NullPointerException("uri is empty, can not write content");
        }
        Logger.print(String.format("write content to uri: %s", uri));
        try (OutputStream stream = context.getContentResolver().openOutputStream(uri)) {
            Objects.requireNonNull(stream).write(content);
        }
    }

    public static void writeString(Context context, Uri uri, String content) {
        try {
            writeStringThrown(context, uri, content);
        } catch (Throwable e) {
            Logger.print(e);
        }
    }

    public static void writeStringThrown(Context context, Uri uri, String content) throws Throwable {
        writeBytesThrown(context, uri, content.getBytes());
    }

}
