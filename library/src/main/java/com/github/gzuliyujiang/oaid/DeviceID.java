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
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

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
 * 设备标识符工具类，建议使用{@link DeviceIdentifier}代替
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
@SuppressWarnings("ALL")
public final class DeviceID implements IGetter {
    private Application application;
    private String clientId;
    private String oaid;

    /**
     * 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID、AndroidID、GUID。
     * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId()}及{@link #getOAID()}，请不要调用这个方法
     *
     * @param application 全局上下文
     * @see Application#onCreate()
     */
    public static void register(Application application) {
        if (application == null) {
            return;
        }
        DeviceID instance = Holder.INSTANCE;
        instance.application = application;
        String uniqueID = getUniqueID(application);
        if (!TextUtils.isEmpty(uniqueID)) {
            instance.clientId = uniqueID;
            OAIDLog.print("Client id is IMEI/MEID: " + instance.clientId);
            return;
        }
        getOAID(application, instance);
    }

    /**
     * 使用该方法获取客户端唯一标识，需要先在{@link Application}里调用{@link #register(Application)}预取
     *
     * @return 客户端唯一标识，可能是IMEI、OAID、WidevineID、AndroidID或GUID中的一种
     * @see #register(Application)
     */
    public static String getClientId() {
        String clientId = Holder.INSTANCE.clientId;
        if (clientId == null) {
            clientId = "";
        }
        return clientId;
    }

    /**
     * 获取预取的客户端唯一标识的MD5值
     *
     * @see #getClientId()
     * @see #register(Application)
     */
    public static String getClientIdMD5() {
        return calculateHash(getClientId(), "MD5");
    }

    /**
     * 获取预取的客户端唯一标识的SHA-1值
     *
     * @see #getClientId()
     * @see #register(Application)
     */
    public static String getClientIdSHA1() {
        return calculateHash(getClientId(), "SHA-1");
    }

    /**
     * 使用该方法获取OAID，需要先在{@link Application#onCreate()}里调用{@link #register(Application)}预取
     *
     * @see #register(Application)
     */
    public static String getOAID() {
        String oaid = Holder.INSTANCE.oaid;
        if (oaid == null) {
            oaid = "";
        }
        return oaid;
    }

    /**
     * 异步获取OAID，如果使用该方法获取OAID，请不要调用{@link #register(Application)}进行预取
     *
     * @param context 上下文
     * @param getter  回调
     */
    public static void getOAID(Context context, IGetter getter) {
        OAIDFactory.create(context).doGet(getter);
    }

    /**
     * 判断设备是否支持 OAID 或 AAID 。大多数国产系统需要 Android 10+ 才支持获取 OAID，需要安卓 Google Play Services 才能获取 AAID。
     *
     * @param context 上下文
     * @see #getOAID(Context, IGetter)
     */
    public static boolean supportedOAID(Context context) {
        return OAIDFactory.create(context).supported();
    }

    /**
     * 获取唯一设备标识。Android 6.0-9.0 需要申请电话权限才能获取 IMEI，Android 10+ 非系统应用则不再允许获取 IMEI。
     * <pre>
     *     <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * </pre>
     *
     * @param context 上下文
     * @return IMEI或MEID，可能为空
     * @see Manifest.permission.READ_PHONE_STATE
     */
    public static String getUniqueID(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 不允许获取 IMEI、MEID 之类的设备唯一标识
            OAIDLog.print("IMEI/MEID not allowed on Android 10+");
            return "";
        }
        if (context == null) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            OAIDLog.print("android.permission.READ_PHONE_STATE not granted");
            // Android 6.0-9.0 需要申请电话权限才能获取设备唯一标识
            return "";
        }
        return getIMEI(context);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"HardwareIds", "MissingPermission"})
    private static String getIMEI(Context context) {
        if (context == null) {
            return "";
        }
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getImei();
            if (TextUtils.isEmpty(imei)) {
                imei = tm.getMeid();
            }
            return imei;
        } catch (Exception e) {
            OAIDLog.print(e);
        } catch (Error e) {
            // e.g. NoSuchMethodError: No virtual method getMeid()
            OAIDLog.print(e);
        }
        return "";
    }

    /**
     * 获取AndroidID
     *
     * @param context 上下文
     * @return AndroidID，可能为空
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        if (context == null) {
            return "";
        }
        String id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (id == null || "9774d56d682e549c".equals(id)) {
            id = "";
        }
        return id;
    }

    /**
     * 获取数字版权管理设备ID
     *
     * @return WidevineID，可能为空
     */
    public static String getWidevineID() {
        try {
            //See https://stackoverflow.com/questions/16369818/how-to-get-crypto-scheme-uuid
            //You can find some UUIDs in the https://github.com/google/ExoPlayer source code
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
        } catch (Exception e) {
            OAIDLog.print(e);
        } catch (Error e) {
            OAIDLog.print(e);
        }
        return "";
    }

    /**
     * 通过取出ROM版本、制造商、CPU型号以及其他硬件信息来伪造设备标识
     *
     * @return 伪造的设备标识，不会为空，但会有一定的概率出现重复
     */
    public static String getPseudoID() {
        final int MODULUS = 10;
        StringBuilder sb = new StringBuilder();
        sb.append(Build.BOARD.length() % MODULUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(Arrays.deepToString(Build.SUPPORTED_ABIS).length() % MODULUS);
        } else {
            // noinspection deprecation
            sb.append(Build.CPU_ABI.length() % MODULUS);
        }
        sb.append(Build.DEVICE.length() % MODULUS);
        sb.append(Build.DISPLAY.length() % MODULUS);
        sb.append(Build.HOST.length() % MODULUS);
        sb.append(Build.ID.length() % MODULUS);
        sb.append(Build.MANUFACTURER.length() % MODULUS);
        sb.append(Build.BRAND.length() % MODULUS);
        sb.append(Build.MODEL.length() % MODULUS);
        sb.append(Build.PRODUCT.length() % MODULUS);
        sb.append(Build.BOOTLOADER.length() % MODULUS);
        sb.append(Build.HARDWARE.length() % MODULUS);
        sb.append(Build.TAGS.length() % MODULUS);
        sb.append(Build.TYPE.length() % MODULUS);
        sb.append(Build.USER.length() % MODULUS);
        return sb.toString();
    }

    /**
     * 随机生成全局唯一标识并存到{@code SharedPreferences}、{@code ExternalStorage}及{@code SystemSettings}。
     * 为保障在Android10以下版本上的稳定性，需要加入权限{@code WRITE_EXTERNAL_STORAGE}及{@code WRITE_SETTINGS}。
     * <pre>
     *     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
     *         tools:ignore="ScopedStorage" />
     *     <uses-permission
     *         android:name="android.permission.WRITE_SETTINGS"
     *         tools:ignore="ProtectedPermissions" />
     * </pre>
     *
     * @return GUID，不会为空，但应用卸载后会丢失
     * @see android.provider.Settings#ACTION_MANAGE_WRITE_SETTINGS
     */
    public static String getGUID(Context context) {
        String uuid = getUuidFromSystemSettings(context);
        if (TextUtils.isEmpty(uuid)) {
            uuid = getUuidFromExternalStorage(context);
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = getUuidFromSharedPreferences(context);
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            OAIDLog.print("Generate uuid by random: " + uuid);
            saveUuidToSharedPreferences(context, uuid);
            saveUuidToSystemSettings(context, uuid);
            saveUuidToExternalStorage(context, uuid);
        }
        return uuid;
    }

    private static String getUuidFromSystemSettings(Context context) {
        if (context == null) {
            return "";
        }
        String uuid = Settings.System.getString(context.getContentResolver(), "GUID_uuid");
        OAIDLog.print("Get uuid from system settings: " + uuid);
        return uuid;
    }

    private static void saveUuidToSystemSettings(Context context, String uuid) {
        if (context == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(context)) {
            try {
                Settings.System.putString(context.getContentResolver(), "GUID_uuid", uuid);
                OAIDLog.print("Save uuid to system settings: " + uuid);
            } catch (Exception e) {
                OAIDLog.print(e);
            }
        } else {
            OAIDLog.print("android.permission.WRITE_SETTINGS not granted");
        }
    }

    private static String getUuidFromExternalStorage(Context context) {
        if (context == null) {
            return "";
        }
        String uuid = "";
        File file = getGuidFile(context);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                uuid = reader.readLine();
            } catch (Exception e) {
                OAIDLog.print(e);
            }
        }
        OAIDLog.print("Get uuid from external storage: " + uuid);
        return uuid;
    }

    private static void saveUuidToExternalStorage(Context context, String uuid) {
        if (context == null) {
            return;
        }
        File file = getGuidFile(context);
        if (file == null) {
            OAIDLog.print("UUID file in external storage is null");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (!file.exists()) {
                file.createNewFile();
            }
            writer.write(uuid);
            writer.flush();
            OAIDLog.print("Save uuid to external storage: " + uuid);
        } catch (Exception e) {
            OAIDLog.print(e);
        }
    }

    private static File getGuidFile(Context context) {
        boolean hasStoragePermission = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            hasStoragePermission = true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasStoragePermission = false;
        } else if (context != null) {
            hasStoragePermission = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        if (hasStoragePermission && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return new File(Environment.getExternalStorageDirectory(), "Android/.GUID_uuid");
        }
        return null;
    }

    private static void saveUuidToSharedPreferences(Context context, String uuid) {
        if (context == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE);
        preferences.edit().putString("uuid", uuid).apply();
        OAIDLog.print("Save uuid to shared preferences: " + uuid);
    }

    private static String getUuidFromSharedPreferences(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences("GUID", Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", "");
        OAIDLog.print("Get uuid from shared preferences: " + uuid);
        return uuid;
    }

    /**
     * 计算哈希值，算法可以是MD2、MD5、SHA-1、SHA-224、SHA-256、SHA-512等，支持的算法见
     * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest
     */
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
        } catch (Exception e) {
            OAIDLog.print(e);
            return "";
        }
    }

    private static class Holder {
        static final DeviceID INSTANCE = new DeviceID();
    }

    private DeviceID() {
        super();
    }

    @Override
    public void onOAIDGetComplete(String result) {
        if (TextUtils.isEmpty(result)) {
            onOAIDGetError(new OAIDException("OAID is empty"));
            return;
        }
        clientId = result;
        oaid = result;
        OAIDLog.print("Client id is OAID/AAID: " + clientId);
    }

    @Override
    public void onOAIDGetError(Exception error) {
        String id = DeviceID.getWidevineID();
        if (!TextUtils.isEmpty(id)) {
            clientId = id;
            OAIDLog.print("Client id is WidevineID: " + clientId);
            return;
        }
        id = getAndroidID(application);
        if (!TextUtils.isEmpty(id)) {
            clientId = id;
            OAIDLog.print("Client id is AndroidID: " + clientId);
            return;
        }
        clientId = getGUID(application);
        OAIDLog.print("Client id is GUID: " + clientId);
    }

}
