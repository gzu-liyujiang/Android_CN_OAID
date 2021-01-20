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
package com.github.gzuliyujiang.oaid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.impl.UnsupportedDeviceIdImpl;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民（1032694760@qq.com）
 */
public final class DeviceID {

    private DeviceID() {
        super();
    }

    public static IDeviceId with(Context context) {
        IDeviceId deviceId;
        if (SystemUtils.isLenovo() || SystemUtils.isMotolora()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.LenovoDeviceIdImpl(context);
        } else if (SystemUtils.isMeizu()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.MeizuDeviceIdImpl(context);
        } else if (SystemUtils.isNubia()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.NubiaDeviceIdImpl(context);
        } else if (SystemUtils.isXiaomi() || SystemUtils.isBlackShark()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.XiaomiDeviceIdImpl(context);
        } else if (SystemUtils.isSamsung()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.SamsungDeviceIdImpl(context);
        } else if (SystemUtils.isVivo()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.VivoDeviceIdImpl(context);
        } else if (SystemUtils.isASUS()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.AsusDeviceIdImpl(context);
        } else if (SystemUtils.isHuawei()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.HuaweiDeviceIdImpl(context);
        } else if (SystemUtils.isOppo() || SystemUtils.isOnePlus()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.OppoDeviceIdImpl(context);
        } else if (SystemUtils.isZTE() || SystemUtils.isFreeme() || SystemUtils.isSSUI()) {
            deviceId = new com.github.gzuliyujiang.oaid.impl.MsaDeviceIdImpl(context);
        } else {
            deviceId = new UnsupportedDeviceIdImpl();
        }
        Logger.print(deviceInfo() + "\nsupportOAID: " + deviceId.supportOAID());
        return deviceId;
    }

    @NonNull
    @SuppressWarnings("deprecation")
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getUniqueID(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 不允许获取 IMEI、MEID 之类的设备唯一标识
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkPermission(Manifest.permission.READ_PHONE_STATE, Process.myPid(),
                        Process.myUid()) != PackageManager.PERMISSION_GRANTED) {
            // Android 6-9 需要申请电话权限才能获取设备唯一标识
            return "";
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String imei = tm.getImei();
            if (!TextUtils.isEmpty(imei)) {
                return imei;
            }
            String meid = tm.getMeid();
            if (!TextUtils.isEmpty(meid)) {
                return meid;
            }
        }
        return "";
    }

    @NonNull
    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if ("9774d56d682e549c".equals(id)) {
            return "";
        }
        return id == null ? "" : id;
    }

    @NonNull
    public static String getPseudoID() {
        // 通过取出ROM版本、制造商、CPU型号以及其他硬件信息来实现序列号，但是会有很大概率出现重复
        StringBuilder sb = new StringBuilder();
        sb.append(Build.BOARD.length() % 10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(Arrays.deepToString(Build.SUPPORTED_ABIS).length() % 10);
        } else {
            sb.append(Build.CPU_ABI.length() % 10);
        }
        sb.append(Build.DEVICE.length() % 10);
        sb.append(Build.DISPLAY.length() % 10);
        sb.append(Build.HOST.length() % 10);
        sb.append(Build.ID.length() % 10);
        sb.append(Build.MANUFACTURER.length() % 10);
        sb.append(Build.BRAND.length() % 10);
        sb.append(Build.MODEL.length() % 10);
        sb.append(Build.PRODUCT.length() % 10);
        sb.append(Build.BOOTLOADER.length() % 10);
        sb.append(Build.HARDWARE.length() % 10);
        sb.append(Build.TAGS.length() % 10);
        sb.append(Build.TYPE.length() % 10);
        sb.append(Build.USER.length() % 10);
        return sb.toString();
    }

    @NonNull
    public static String getGUID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            preferences.edit().putString("uuid", uuid).apply();
        }
        return uuid;
    }

    public static String deviceInfo() {
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("BrandModel：");
        sb.append(Build.BRAND);
        sb.append(" ");
        sb.append(Build.MODEL);
        sb.append("\n");
        sb.append("Manufacturer：");
        sb.append(Build.MANUFACTURER);
        sb.append("\n");
        sb.append("SystemVersion：");
        sb.append(Build.VERSION.RELEASE);
        sb.append(" (API ");
        sb.append(Build.VERSION.SDK_INT);
        sb.append(")");
        return sb.toString();
    }

}
