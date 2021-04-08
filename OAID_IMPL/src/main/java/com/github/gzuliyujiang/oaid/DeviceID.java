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
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.oaid.impl.AsusImpl;
import com.github.gzuliyujiang.oaid.impl.DefaultImpl;
import com.github.gzuliyujiang.oaid.impl.HuaweiImpl;
import com.github.gzuliyujiang.oaid.impl.LenovoImpl;
import com.github.gzuliyujiang.oaid.impl.MeizuImpl;
import com.github.gzuliyujiang.oaid.impl.MsaImpl;
import com.github.gzuliyujiang.oaid.impl.NubiaImpl;
import com.github.gzuliyujiang.oaid.impl.OppoImpl;
import com.github.gzuliyujiang.oaid.impl.SamsungImpl;
import com.github.gzuliyujiang.oaid.impl.VivoImpl;
import com.github.gzuliyujiang.oaid.impl.XiaomiImpl;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.UUID;

/**
 * 设备标识符工具类
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @version 3.0.0
 * @since 2020/5/30
 */
public final class DeviceID {
    private static String clientId;

    private DeviceID() {
        super();
    }

    /**
     * 在应用启动时预取客户端标识，按优先级尝试获取IMEI/MEID、OAID、AndroidID、GUID
     *
     * @param application 全局上下文
     * @see Application#onCreate()
     */
    public static void register(final Application application) {
        String uniqueID = getUniqueID(application);
        if (!TextUtils.isEmpty(uniqueID)) {
            clientId = uniqueID;
            OAIDLog.print("Client id is IMEI/MEID");
            return;
        }
        getOAID(application, new IGetter() {
            @Override
            public void onOAIDGetComplete(@NonNull String result) {
                clientId = result;
                OAIDLog.print("Client id is OAID");
            }

            @Override
            public void onOAIDGetError(@NonNull Throwable error) {
                String id = DeviceID.getWidevineID(application);
                if (!TextUtils.isEmpty(id)) {
                    clientId = id;
                    OAIDLog.print("Client id is WidevineID");
                    return;
                }
                id = getAndroidID(application);
                if (!TextUtils.isEmpty(id)) {
                    clientId = id;
                    OAIDLog.print("Client id is AndroidID");
                    return;
                }
                clientId = getGUID(application);
                OAIDLog.print("Client id is GUID");
            }
        });
    }

    /**
     * 获取预取到的客户端标识
     *
     * @return 客户端标识，可能是IMEI、OAID、AndroidID或GUID
     */
    public static String getClientId() {
        return clientId == null ? "" : clientId;
    }

    /**
     * @see #getClientId()
     */
    public static String getClientIdMD5() {
        return clientId == null ? "" : calculateHash(clientId, "MD5");
    }

    /**
     * @see #getClientId()
     */
    public static String getClientIdSHA1() {
        return clientId == null ? "" : calculateHash(clientId, "SHA-1");
    }

    /**
     * 异步获取OAID
     *
     * @param context 上下文
     * @param getter  回调
     */
    public static void getOAID(@NonNull Context context, @NonNull IGetter getter) {
        IOAID oaid;
        if (OAIDRom.isLenovo() || OAIDRom.isMotolora()) {
            oaid = new LenovoImpl(context);
        } else if (OAIDRom.isMeizu()) {
            oaid = new MeizuImpl(context);
        } else if (OAIDRom.isNubia()) {
            oaid = new NubiaImpl(context);
        } else if (OAIDRom.isXiaomi() || OAIDRom.isBlackShark()) {
            oaid = new XiaomiImpl(context);
        } else if (OAIDRom.isSamsung()) {
            oaid = new SamsungImpl(context);
        } else if (OAIDRom.isVivo()) {
            oaid = new VivoImpl(context);
        } else if (OAIDRom.isASUS()) {
            oaid = new AsusImpl(context);
        } else if (OAIDRom.isHuawei()) {
            oaid = new HuaweiImpl(context);
        } else if (OAIDRom.isOppo() || OAIDRom.isOnePlus()) {
            oaid = new OppoImpl(context);
        } else if (OAIDRom.isZTE() || OAIDRom.isFreeme() || OAIDRom.isSSUI()) {
            oaid = new MsaImpl(context);
        } else {
            oaid = new DefaultImpl();
        }
        oaid.doGet(getter);
    }

    /**
     * 获取唯一设备标识
     *
     * @param context 上下文
     * @return IMEI或MEID，可能为空
     */
    @NonNull
    @SuppressWarnings("deprecation")
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getUniqueID(@NonNull Context context) {
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

    /**
     * 获取AndroidID
     *
     * @param context 上下文
     * @return AndroidID，可能为空
     */
    @NonNull
    @SuppressLint("HardwareIds")
    public static String getAndroidID(@NonNull Context context) {
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if ("9774d56d682e549c".equals(id)) {
            return "";
        }
        return id == null ? "" : id;
    }

    /**
     * 获取数字版权管理设备ID
     *
     * @param context 上下文
     * @return WidevineID，可能为空
     */
    @NonNull
    public static String getWidevineID(@NonNull Context context) {
        try {
            //Widevine介绍：https://baike.baidu.com/item/Widevine/3613955
            //参阅 https://stackoverflow.com/questions/16369818/how-to-get-crypto-scheme-uuid
            //You can find some UUIDs in the https://github.com/google/ExoPlayer source code
            //final UUID COMMON_PSSH_UUID = new UUID(0x1077EFECC0B24D02L, 0xACE33C1E52E2FB4BL);
            //final UUID CLEARKEY_UUID = new UUID(0xE2719D58A985B3C9L, 0x781AB030AF78D30EL);
            //final UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
            //final UUID PLAYREADY_UUID = new UUID(0x9A04F07998404286L, 0xAB92E65BE0885F95L);
            final UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
            MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID);
            byte[] widevineId = mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
            if (widevineId == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (byte aByte : widevineId) {
                sb.append(String.format("%02x", aByte));
            }
            return sb.toString();
        } catch (UnsupportedSchemeException e) {
            OAIDLog.print(e);
        }
        return "";
    }

    /**
     * 通过取出ROM版本、制造商、CPU型号以及其他硬件信息来伪造设备标识
     *
     * @return 伪造的设备标识，不会为空，但会有很大概率出现重复
     */
    @NonNull
    public static String getPseudoID() {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.BOARD.length() % 10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(Arrays.deepToString(Build.SUPPORTED_ABIS).length() % 10);
        } else {
            //noinspection deprecation
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

    /**
     * 随机生成全局唯一标识
     *
     * @return GUID，不会为空，但应用卸载后会丢失
     */
    @NonNull
    public static String getGUID(@NonNull Context context) {
        SharedPreferences preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            preferences.edit().putString("uuid", uuid).apply();
        }
        return uuid;
    }

    /**
     * 计算哈希值，算法可以是MD2、MD5、SHA-1、SHA-224、SHA-256、SHA-512等。
     * 支持的算法见 https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest
     */
    public static String calculateHash(String str, String algorithm) {
        try {
            byte[] data = str.getBytes();
            byte[] bytes = MessageDigest.getInstance(algorithm).digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(String.format("%02x", aByte));
            }
            return sb.toString();
        } catch (Throwable e) {
            return "";
        }
    }

}
