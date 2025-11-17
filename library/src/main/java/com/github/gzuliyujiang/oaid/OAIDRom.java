/*
 * Copyright (c) 2016-present. 贵州纳雍穿青人李裕江 and All Contributors.
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.github.gzuliyujiang.oaid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * ROM识别工具类
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/29
 */
@SuppressWarnings("All")
public final class OAIDRom {

    private OAIDRom() {
        super();
    }

    public static String sysProperty(String key, String defValue) {
        String res = null;
        try {
            @SuppressLint("PrivateApi") Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", new Class<?>[]{String.class, String.class});
            res = (String) method.invoke(clazz, new Object[]{key, defValue});
        } catch (Throwable e) {
            OAIDLog.print("System property invoke error: " + e);
        }
        if (res == null) {
            res = "";
        }
        return res;
    }

    public static boolean isHuawei() {
        // 华为手机、荣耀手机
        return Build.MANUFACTURER.equalsIgnoreCase("HUAWEI") ||
                Build.BRAND.equalsIgnoreCase("HUAWEI");
    }

    public static boolean isHonor() {
        // 荣耀手机
        return Build.BRAND.equalsIgnoreCase("HONOR");
    }

    public static boolean isHarmonyOS() {
        // 鸿濛系统手机
        try {
            // HarmonyOS版本号可读取`hw_sc.build.platform.version`
            Class<?> clazz = Class.forName("com.huawei.system.BuildEx");
            Object osBrand = clazz.getMethod("getOsBrand", new Class[0]).invoke(clazz, new Object[0]);
            return osBrand != null && osBrand.toString().length() > 0;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean isEmui() {
        // 除了华为品牌，其他品牌也可能刷了EMUI
        return !TextUtils.isEmpty(sysProperty("ro.build.version.emui", ""));
    }

    public static boolean isMagicUI() {
        // 除了华为品牌，其他品牌也可能刷了MagicUI
        return !TextUtils.isEmpty(sysProperty("ro.build.version.magic", ""));
    }

    public static boolean isOppo() {
        // 欧珀手机、真我手机
        return Build.MANUFACTURER.equalsIgnoreCase("OPPO") ||
                Build.BRAND.equalsIgnoreCase("OPPO") ||
                Build.BRAND.equalsIgnoreCase("REALME") ||
                !TextUtils.isEmpty(sysProperty("ro.build.version.opporom", ""));
    }

    public static boolean isVivo() {
        // 维沃手机、爱酷手机
        return Build.MANUFACTURER.equalsIgnoreCase("VIVO") ||
                Build.BRAND.equalsIgnoreCase("VIVO") ||
                !TextUtils.isEmpty(sysProperty("ro.vivo.os.version", ""));
    }

    public static boolean isXiaomi() {
        // 小米手机、红米手机
        return Build.MANUFACTURER.equalsIgnoreCase("XIAOMI") ||
                Build.BRAND.equalsIgnoreCase("XIAOMI") ||
                Build.BRAND.equalsIgnoreCase("REDMI");
    }

    public static boolean isBlackShark() {
        // 黑鲨手机
        return Build.MANUFACTURER.equalsIgnoreCase("BLACKSHARK") ||
                Build.BRAND.equalsIgnoreCase("BLACKSHARK");
    }

    public static boolean isMiui() {
        // 除了小米品牌，其他品牌也可能刷了MIUI
        return !TextUtils.isEmpty(sysProperty("ro.miui.ui.version.name", ""));
    }

    public static boolean isMiuiGlobal() {
        // MIUI国际版不支持OAID，只支持AAID
        if (!TextUtils.isEmpty(sysProperty("ro.com.google.gmsversion", ""))) {
            // 预装有 GMS 则视为国际版
            return true;
        }
        if (sysProperty("ro.miui.region", "").equalsIgnoreCase("CN")) {
            // 区域为 CN 则视为国内版
            return false;
        }
        if (sysProperty("ro.product.mod_device", "").endsWith("_global")) {
            // 含 global 标识则视为国际版
            return true;
        }
        // 默认视为国内版
        return false;
    }

    public static boolean isOnePlus() {
        // 一加手机。HydrogenOS版本号可读取`ro.rom.version`，ColorOS版本号可读取`ro.build.version.oplusrom`
        return Build.MANUFACTURER.equalsIgnoreCase("ONEPLUS") ||
                Build.BRAND.equalsIgnoreCase("ONEPLUS");
    }

    public static boolean isSamsung() {
        // 三星手机
        return Build.MANUFACTURER.equalsIgnoreCase("SAMSUNG") ||
                Build.BRAND.equalsIgnoreCase("SAMSUNG");
    }

    public static boolean isMeizu() {
        // 魅族手机
        return Build.MANUFACTURER.equalsIgnoreCase("MEIZU") ||
                Build.BRAND.equalsIgnoreCase("MEIZU") ||
                Build.DISPLAY.toUpperCase().contains("FLYME");
    }

    public static boolean isLenovo() {
        // 联想手机、乐檬手机
        return Build.MANUFACTURER.equalsIgnoreCase("LENOVO") ||
                Build.BRAND.equalsIgnoreCase("LENOVO") ||
                Build.BRAND.equalsIgnoreCase("ZUK");
    }

    public static boolean isNubia() {
        // 努比亚手机
        return Build.MANUFACTURER.equalsIgnoreCase("NUBIA") ||
                Build.BRAND.equalsIgnoreCase("NUBIA");
    }

    public static boolean isASUS() {
        // 华硕手机
        return Build.MANUFACTURER.equalsIgnoreCase("ASUS") ||
                Build.BRAND.equalsIgnoreCase("ASUS");
    }

    public static boolean isZTE() {
        // 中兴手机
        return Build.MANUFACTURER.equalsIgnoreCase("ZTE") ||
                Build.BRAND.equalsIgnoreCase("ZTE");
    }

    public static boolean isMotorola() {
        // 摩托罗拉手机
        return Build.MANUFACTURER.toUpperCase().contains("MOTO") ||
                Build.BRAND.toUpperCase().contains("MOTO") ||
                Build.BRAND.equalsIgnoreCase("MOTOROLA");
    }

    @Deprecated(since = "4.2.14", forRemoval = true)
    public static boolean isMotolora() {
        return isMotorola();
    }

    public static boolean isFreeme() {
        // 卓易手机
        return !TextUtils.isEmpty(sysProperty("ro.build.freeme.label", ""));
    }

    public static boolean isCoolpad(Context context) {
        // 酷派手机
        if (Build.MANUFACTURER.toUpperCase().contains("COOL") ||
                Build.BRAND.toUpperCase().contains("COOL") ||
                Build.MODEL.toUpperCase().contains("COOL")) {
            return true;
        }
        try {
            context.getPackageManager().getPackageInfo("com.coolpad.deviceidsupport", 0);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean isCoosea() {
        // 酷赛手机
        return sysProperty("ro.odm.manufacturer", "").equalsIgnoreCase("PRIZE");
    }

    public static boolean isSSUI() {
        // 这是啥玩意的手机或系统？百度及谷歌都搜不到相关资料
        return !TextUtils.isEmpty(sysProperty("ro.ssui.product", ""));
    }

    public static boolean is360OS() {
        // 360OS手机
        return !TextUtils.isEmpty(sysProperty("ro.build.uiversion", ""));
    }
}
