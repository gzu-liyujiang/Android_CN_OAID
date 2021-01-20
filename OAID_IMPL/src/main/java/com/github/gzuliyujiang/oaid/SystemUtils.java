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

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;

import java.lang.reflect.Method;

/**
 * Created by liyujiang on 2020/5/29
 *
 * @author 大定府羡民（1032694760@qq.com）
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class SystemUtils {

    public static String sysProperty(String key, String defValue) {
        String res = null;
        try {
            @SuppressLint("PrivateApi") Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class, String.class);
            res = (String) method.invoke(clazz, new Object[]{key, defValue});
        } catch (Exception e) {
            Logger.print(e);
        }
        if (res == null) {
            res = "";
        }
        return res;
    }

    public static boolean isASUS() {
        // 华硕手机
        return Build.MANUFACTURER.toUpperCase().equals("ASUS");
    }

    public static boolean isHuawei() {
        // 华为手机
        return Build.MANUFACTURER.toUpperCase().equals("HUAWEI");
    }

    public static boolean isZTE() {
        // 中兴手机
        return Build.MANUFACTURER.toUpperCase().equals("ZTE");
    }

    public static boolean isXiaomi() {
        // 小米手机
        return Build.MANUFACTURER.toUpperCase().equals("XIAOMI");
    }

    public static boolean isOppo() {
        // 欧珀手机
        return Build.MANUFACTURER.toUpperCase().equals("OPPO");
    }

    public static boolean isVivo() {
        // 维沃手机
        return Build.MANUFACTURER.toUpperCase().equals("VIVO");
    }

    public static boolean isOnePlus() {
        // 一加手机
        return Build.MANUFACTURER.toUpperCase().equals("ONEPLUS");
    }

    public static boolean isBlackShark() {
        // 黑鲨手机
        return Build.MANUFACTURER.toUpperCase().equals("BLACKSHARK");
    }

    public static boolean isSamsung() {
        // 三星手机
        return Build.MANUFACTURER.toUpperCase().equals("SAMSUNG");
    }

    public static boolean isMotolora() {
        // 摩托罗拉手机
        return Build.MANUFACTURER.toUpperCase().equals("MOTOLORA");
    }

    public static boolean isNubia() {
        // 努比亚手机
        return Build.MANUFACTURER.toUpperCase().equals("NUBIA");
    }

    public static boolean isMeizu() {
        // 魅族手机
        return Build.MANUFACTURER.toUpperCase().equals("MEIZU");
    }

    public static boolean isLenovo() {
        // 联想手机
        return Build.MANUFACTURER.toUpperCase().equals("LENOVO");
    }

    public static boolean isFreeme() {
        // 卓易手机
        if (Build.MANUFACTURER.toUpperCase().equals("FREEMEOS")) {
            return true;
        }
        String pro = sysProperty("ro.build.freeme.label", "");
        return !TextUtils.isEmpty(pro) && pro.toUpperCase().equals("FREEMEOS");
    }

    public static boolean isSSUI() {
        // 这是啥玩意的手机？百度及谷歌都搜不到相关资料
        if (Build.MANUFACTURER.toUpperCase().equals("SSUI")) {
            return true;
        }
        String pro = sysProperty("ro.ssui.product", "unknown");
        return !TextUtils.isEmpty(pro) && !pro.toUpperCase().equals("UNKNOWN");
    }

}
