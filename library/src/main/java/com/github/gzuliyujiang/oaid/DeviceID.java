/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.oaid.impl.OAIDFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
@SuppressWarnings("ALL")
public final class DeviceID {
    private static String clientId;
    private static String oaid;

    private DeviceID() {
        super();
    }

    /**
     * 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID、AndroidID、GUID。
     * !!注意!!：如果不需要用到{@link #getClientId()}及{@link #getOAID()}，请不要调用这个方法
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
                oaid = result;
                OAIDLog.print("Client id is OAID");
            }

            @Override
            public void onOAIDGetError(@NonNull Throwable error) {
                String id = DeviceID.getWidevineID();
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
     * 使用该方法获取客户端唯一标识，需要先在{@link Application}里调用{@link #register(Application)}预取
     *
     * @return 客户端唯一标识，可能是IMEI、OAID、WidevineID、AndroidID或GUID中的一种
     * @see #register(Application)
     */
    @NonNull
    public static String getClientId() {
        return clientId == null ? "" : clientId;
    }

    /**
     * 获取预取的客户端唯一标识的MD5值
     *
     * @see #getClientId()
     * @see #register(Application)
     */
    @NonNull
    public static String getClientIdMD5() {
        return calculateHash(getClientId(), "MD5");
    }

    /**
     * 获取预取的客户端唯一标识的SHA-1值
     *
     * @see #getClientId()
     * @see #register(Application)
     */
    @NonNull
    public static String getClientIdSHA1() {
        return calculateHash(getClientId(), "SHA-1");
    }

    /**
     * 使用该方法获取OAID，需要先在{@link Application#onCreate()}里调用{@link #register(Application)}预取
     *
     * @see #register(Application)
     */
    @NonNull
    public static String getOAID() {
        return oaid == null ? "" : oaid;
    }

    /**
     * 异步获取OAID，如果使用该方法获取OAID，请不要调用{@link #register(Application)}进行预取
     *
     * @param context 上下文
     * @param getter  回调
     */
    public static void getOAID(@NonNull Context context, @NonNull IGetter getter) {
        OAIDFactory.create(context).doGet(getter);
    }

    /**
     * 判断设备是否支持 OAID 或 AAID 。大多数国产系统需要 Android 10+ 才支持获取 OAID，需要安卓 Google Play Services 才能获取 AAID。
     *
     * @param context 上下文
     * @see #getOAID(Context, IGetter)
     */
    public static boolean supportedOAID(@NonNull Context context) {
        return OAIDFactory.create(context).supported();
    }

    /**
     * 获取唯一设备标识。Android 6.0-9.0 需要申请电话权限才能获取 IMEI，Android 10+ 非系统应用则不再允许获取 IMEI。
     * <pre>
     *     <uses-permission
     *         android:name="android.permission.READ_PHONE_STATE"
     *         android:maxSdkVersion="29" />
     * </pre>
     *
     * @param context 上下文
     * @return IMEI或MEID，可能为空
     * @see Manifest.permission.READ_PHONE_STATE
     */
    @NonNull
    @SuppressWarnings("deprecation")
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getUniqueID(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 不允许获取 IMEI、MEID 之类的设备唯一标识
            OAIDLog.print("IMEI/MEID not allowed on Android 10+");
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            OAIDLog.print("android.permission.READ_PHONE_STATE not granted");
            // Android 6.0-9.0 需要申请电话权限才能获取设备唯一标识
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
     * @deprecated 不需要传递上下文
     */
    @Deprecated
    @NonNull
    public static String getWidevineID(@NonNull Context context) {
        return getWidevineID();
    }

    /**
     * 获取数字版权管理设备ID
     *
     * @return WidevineID，可能为空
     */
    @NonNull
    public static String getWidevineID() {
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
     * @return 伪造的设备标识，不会为空，但会有一定的概率出现重复
     */
    @NonNull
    public static String getPseudoID() {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.BOARD.length() % 10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(Arrays.deepToString(Build.SUPPORTED_ABIS).length() % 10);
        } else {
            // noinspection deprecation
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
     * 随机生成全局唯一标识并存到{@code SharedPreferences}、{@code ExternalStorage}及{@code SystemSettings}。
     * 为保障在Android10以下版本上的稳定性，需要加入权限{@code WRITE_EXTERNAL_STORAGE}及{@code WRITE_SETTINGS}。
     * <pre>
     *     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     *         android:maxSdkVersion="29"
     *         tools:ignore="ScopedStorage" />
     *     <uses-permission
     *         android:name="android.permission.WRITE_SETTINGS"
     *         tools:ignore="ProtectedPermissions" />
     * </pre>
     *
     * @return GUID，不会为空，但应用卸载后会丢失
     * @see android.provider.Settings#ACTION_MANAGE_WRITE_SETTINGS
     */
    @NonNull
    public static String getGUID(@NonNull Context context) {
        String uuid = Settings.System.getString(context.getContentResolver(), "GUID_uuid");
        OAIDLog.print("Get uuid from system settings: " + uuid);
        if (!TextUtils.isEmpty(uuid)) {
            return uuid;
        }
        boolean hasStoragePermission = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            hasStoragePermission = true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasStoragePermission = false;
        } else {
            hasStoragePermission = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        File file = null;
        if (hasStoragePermission && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory(), "Android/.GUID_uuid");
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                uuid = reader.readLine();
                OAIDLog.print("Get uuid from external storage: " + uuid);
                if (!TextUtils.isEmpty(uuid)) {
                    return uuid;
                }
            } catch (Exception e) {
                OAIDLog.print(e);
            }
        }
        SharedPreferences preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE);
        uuid = preferences.getString("uuid", "");
        OAIDLog.print("Get uuid from shared preferences: " + uuid);
        if (!TextUtils.isEmpty(uuid)) {
            return uuid;
        }
        uuid = UUID.randomUUID().toString();
        OAIDLog.print("Generate uuid by random: " + uuid);
        preferences.edit().putString("uuid", uuid).apply();
        OAIDLog.print("Save uuid to shared preferences: " + uuid);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(context)) {
            try {
                Settings.System.putString(context.getContentResolver(), "GUID_uuid", uuid);
                OAIDLog.print("Save uuid to system settings: " + uuid);
            } catch (Throwable e) {
                OAIDLog.print(e);
            }
        } else {
            OAIDLog.print("android.permission.WRITE_SETTINGS not granted");
        }
        if (file == null) {
            return uuid;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer.write(uuid);
            writer.flush();
            OAIDLog.print("Save uuid to external storage: " + uuid);
        } catch (Throwable e) {
            OAIDLog.print(e);
        }
        return uuid;
    }

    /**
     * 计算哈希值，算法可以是MD2、MD5、SHA-1、SHA-224、SHA-256、SHA-512等，支持的算法见
     * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest
     */
    @NonNull
    public static String calculateHash(String str, String algorithm) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
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
