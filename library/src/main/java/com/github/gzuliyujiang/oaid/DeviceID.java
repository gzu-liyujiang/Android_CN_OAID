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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
public final class DeviceID {
    private Application application;
    private boolean tryWidevine;
    private String clientId;
    private String oaid;

    /**
     * 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID/AAID、AndroidID、GUID。
     * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId()}及{@link #getOAID}，请不要调用这个方法
     *
     * @param application 全局上下文
     * @see Application#onCreate()
     */
    public static void register(Application application) {
        register(application, false, null);
    }

    /**
     * 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID/AAID、AndroidID、GUID。
     * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId()}及{@link #getOAID}，请不要调用这个方法
     *
     * @param application 全局上下文
     * @param tryWidevine 是否尝试WidevineID，由于兼容问题，IMEI/MEID及OAID获取失败后默认不尝试获取WidevineID
     * @see Application#onCreate()
     */
    public static void register(Application application, boolean tryWidevine) {
        register(application, tryWidevine, null);
    }

    /**
     * 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID/AAID、AndroidID、GUID。
     * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId()}及{@link #getOAID}，请不要调用这个方法
     *
     * @param application 全局上下文
     * @param callback    注册完成回调
     * @see Application#onCreate()
     */
    public static void register(Application application, IRegisterCallback callback) {
        register(application, false, callback);
    }

    /**
     * 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID/AAID、AndroidID、GUID。
     * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId()}及{@link #getOAID}，请不要调用这个方法
     *
     * @param application 全局上下文
     * @param tryWidevine 是否尝试WidevineID，由于兼容问题，IMEI/MEID及OAID获取失败后默认不尝试获取WidevineID
     * @param callback    注册完成回调
     * @see Application#onCreate()
     */
    public static void register(Application application, boolean tryWidevine, IRegisterCallback callback) {
        if (application == null) {
            if (callback != null) {
                callback.onComplete("", new RuntimeException("application is nulll"));
            }
            return;
        }
        Holder.INSTANCE.application = application;
        Holder.INSTANCE.tryWidevine = tryWidevine;
        String uniqueID = getUniqueID(application);
        if (!TextUtils.isEmpty(uniqueID)) {
            Holder.INSTANCE.clientId = uniqueID;
            OAIDLog.print("Client id is IMEI/MEID: " + Holder.INSTANCE.clientId);
            if (callback != null) {
                callback.onComplete(uniqueID, null);
            }
            return;
        }
        getOAIDOrOtherId(application, tryWidevine, callback);
    }

    private static void getOAIDOrOtherId(Application application, boolean tryWidevine, IRegisterCallback callback) {
        getOAID(application, new IGetter() {
            @Override
            public void onOAIDGetComplete(String result) {
                if (TextUtils.isEmpty(result)) {
                    onOAIDGetError(new OAIDException("OAID is empty"));
                    return;
                }
                Holder.INSTANCE.clientId = result;
                Holder.INSTANCE.oaid = result;
                OAIDLog.print("Client id is OAID/AAID: " + result);
                if (callback != null) {
                    callback.onComplete(result, null);
                }
            }

            @Override
            public void onOAIDGetError(Exception error) {
                getOtherId(error, application, tryWidevine, callback);
            }
        });
    }

    private static void getOtherId(Exception error, Application application, boolean tryWidevine, IRegisterCallback callback) {
        String id = "";
        if (tryWidevine) {
            id = DeviceID.getWidevineID();
            if (!TextUtils.isEmpty(id)) {
                Holder.INSTANCE.clientId = id;
                OAIDLog.print("Client id is WidevineID: " + id);
                if (callback != null) {
                    callback.onComplete(id, error);
                }
                return;
            }
        }
        id = getAndroidID(application);
        if (!TextUtils.isEmpty(id)) {
            Holder.INSTANCE.clientId = id;
            OAIDLog.print("Client id is AndroidID: " + id);
            if (callback != null) {
                callback.onComplete(id, error);
            }
            return;
        }
        id = getGUID(application);
        Holder.INSTANCE.clientId = id;
        OAIDLog.print("Client id is GUID: " + id);
        if (callback != null) {
            callback.onComplete(id, error);
        }
    }

    /**
     * 使用该方法获取客户端唯一标识，需要先在{@link Application}里调用{@link #register(Application)}预取
     *
     * @return 客户端唯一标识，可能是IMEI/MEID、OAID/AAID、AndroidID或GUID中的一种
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
     * 获取预取的客户端唯一标识，可能是IMEI/MEID、OAID/AAID、AndroidID或GUID中的一种，的MD5值
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
        IOAID ioaid = OAIDFactory.create(context);
        OAIDLog.print("OAID implements class: " + ioaid.getClass().getName());
        ioaid.doGet(getter);
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
     * 异步获取手机厂商专有的广告标识符，不支持则回调`onOAIDGetError(Exception)`
     *
     * @param context 上下文
     * @param getter  回调
     */
    public static void getByManufacturer(Context context, IGetter getter) {
        IOAID ioaid = OAIDFactory.ofManufacturer(context);
        OAIDLog.print("OAID implements class: " + ioaid.getClass().getName());
        ioaid.doGet(getter);
    }

    /**
     * 异步获取移动安全联盟通用的广告标识符，不支持则回调`onOAIDGetError(Exception)`
     *
     * @param context 上下文
     * @param getter  回调
     */
    public static void getByMsa(Context context, IGetter getter) {
        IOAID ioaid = OAIDFactory.ofMsa(context);
        OAIDLog.print("OAID implements class: " + ioaid.getClass().getName());
        ioaid.doGet(getter);
    }

    /**
     * 异步获取谷歌商店服务通用的广告标识符，不支持则回调`onOAIDGetError(Exception)`
     *
     * @param context 上下文
     * @param getter  回调
     */
    public static void getByGms(Context context, IGetter getter) {
        IOAID ioaid = OAIDFactory.ofGms(context);
        OAIDLog.print("OAID implements class: " + ioaid.getClass().getName());
        ioaid.doGet(getter);
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
     * @deprecated Android 10+ 无法获取，不推荐使用了
     */
    @Deprecated
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
     * @deprecated 很鸡肋，不推荐使用了，因为在某些手机上调用会莫名其妙的造成闪退或卡死，还难以排查到原因
     */
    @Deprecated
    public static String getWidevineID() {
        MediaDrm mediaDrm = null;
        try {
            //See https://stackoverflow.com/questions/16369818/how-to-get-crypto-scheme-uuid
            //You can find some UUIDs in the https://github.com/google/ExoPlayer source code
            final UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
            mediaDrm = new MediaDrm(WIDEVINE_UUID);
            byte[] widevineId = mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
            if (widevineId == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (byte aByte : widevineId) {
                sb.append(String.format("%02x", aByte));
            }
            return sb.toString();
        } catch (Throwable e) {
            OAIDLog.print(e);
        } finally {
            if (mediaDrm != null) {
                //mediaDrm.close();
                mediaDrm.release();
            }
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

    /**
     * 获取画布指纹。原理：不同设备的硬件以及浏览器实现会导致画布渲染结果存在细微差异。
     */
    public static String getCanvasFingerprint() {
        int width = 200;
        int height = 100;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        paint.setTypeface(Typeface.SERIF);
        canvas.drawText("羡民@李裕江", 10, 50, paint);
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        StringBuilder pixelData = new StringBuilder();
        for (int pixel : pixels) {
            pixelData.append(pixel);
        }
        return calculateHash(pixelData.toString(), "SHA-1");
    }

    private static class Holder {
        static final DeviceID INSTANCE = new DeviceID();
    }

    private DeviceID() {
        super();
    }

}
