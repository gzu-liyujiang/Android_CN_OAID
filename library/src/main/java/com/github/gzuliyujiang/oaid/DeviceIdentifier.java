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

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

/**
 * 设备标识符工具类。
 * <p>
 * 双检查锁机制实现单例模式缓存一下标识符，解决APP合规性检测机构检测到的频繁读取设备信息问题
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @noinspection unused, DeprecatedIsStillUsed
 * @since 2021/9/1 10:06
 */
public final class DeviceIdentifier {
    private static volatile boolean registered = false;
    private static volatile String clientId = null;
    private static volatile String imei = null;
    private static volatile String oaid = null;
    private static volatile String androidId = null;
    private static volatile String widevineId = null;
    private static volatile String pseudoId = null;
    private static volatile String guid = null;
    private static volatile String canvasFingerprint = null;

    private DeviceIdentifier() {
        super();
    }

    /**
     * 在应用启动时预取客户端标识及OAID，客户端标识按优先级尝试获取IMEI/MEID、OAID/AAID、AndroidID、GUID。
     * !!注意!!：若最终用户未同意隐私政策，或者不需要用到{@link #getClientId()}及{@link #getOAID}，请不要调用这个方法
     *
     * @param application 全局上下文
     * @see Application#onCreate()
     */
    public static void register(Application application) {
        register(application, null);
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
        if (registered || application == null) {
            return;
        }
        synchronized (DeviceIdentifier.class) {
            if (!registered) {
                DeviceID.register(application, tryWidevine, callback);
                registered = true;
            }
        }
    }

    /**
     * 使用该方法获取客户端唯一标识，需要先在{@link Application}里调用{@link #register(Application)}预取
     *
     * @return 客户端唯一标识的MD5值，可能是IMEI/MEID、OAID/AAID、AndroidID或GUID中的一种
     * @see #register(Application)
     */
    public static String getClientId() {
        return getClientId(false);
    }

    /**
     * 使用该方法获取客户端唯一标识，需要先在{@link Application}里调用{@link #register(Application)}预取
     *
     * @param returnRaw 返回的是否是原始值
     * @return 客户端唯一标识，可能是IMEI/MEID、OAID/AAID、AndroidID或GUID中的一种
     * @see #register(Application)
     * @see DeviceID#getClientId()
     * @see DeviceID#getClientIdMD5()
     * @see DeviceID#getClientIdSHA1()
     */
    public static String getClientId(boolean returnRaw) {
        if (TextUtils.isEmpty(clientId)) {
            synchronized (DeviceIdentifier.class) {
                if (TextUtils.isEmpty(clientId)) {
                    clientId = returnRaw ? DeviceID.getClientId() : DeviceID.getClientIdMD5();
                }
            }
        }
        if (clientId == null) {
            clientId = "";
        }
        return clientId;
    }

    /**
     * 获取唯一设备标识。Android 6.0-9.0 需要申请电话权限才能获取 IMEI/MEID，Android 10+ 非系统应用则不再允许获取 IMEI。
     * <pre>
     *     <uses-permission android:name="android.permission.READ_PHONE_STATE" />
     * </pre>
     *
     * @param context 上下文
     * @return IMEI或MEID，可能为空
     * @noinspection deprecation
     * @deprecated Android 10+ 无法获取，不推荐使用了
     */
    @Deprecated
    public static String getIMEI(Context context) {
        if (imei == null) {
            synchronized (DeviceIdentifier.class) {
                if (imei == null) {
                    imei = DeviceID.getUniqueID(context);
                }
            }
        }
        if (imei == null) {
            imei = "";
        }
        return imei;
    }

    /**
     * 【推荐使用】使用该方法获取OAID/AAID，需要先在{@link Application#onCreate()}里调用{@link #register(Application)}预取
     *
     * @see #register(Application)
     */
    public static String getOAID(Context context) {
        if (TextUtils.isEmpty(oaid)) {
            synchronized (DeviceIdentifier.class) {
                if (TextUtils.isEmpty(oaid)) {
                    oaid = DeviceID.getOAID();
                    if (oaid == null || oaid.length() == 0) {
                        DeviceID.getOAID(context, new IGetter() {
                            @Override
                            public void onOAIDGetComplete(String result) {
                                oaid = result;
                            }

                            @Override
                            public void onOAIDGetError(Exception error) {
                                oaid = "";
                            }
                        });
                    }
                }
            }
        }
        if (oaid == null) {
            oaid = "";
        }
        return oaid;
    }

    /**
     * 获取AndroidID
     *
     * @param context 上下文
     * @return AndroidID，可能为空
     */
    public static String getAndroidID(Context context) {
        if (androidId == null) {
            synchronized (DeviceIdentifier.class) {
                if (androidId == null) {
                    androidId = DeviceID.getAndroidID(context);
                }
            }
        }
        if (androidId == null) {
            androidId = "";
        }
        return androidId;
    }

    /**
     * 获取数字版权管理设备ID。不推荐使用，因为在某些手机上调用会莫名其妙的造成闪退或卡死，还难以排查到原因
     *
     * @return WidevineID，可能为空
     */
    public static String getWidevineID() {
        if (widevineId == null) {
            synchronized (DeviceIdentifier.class) {
                if (widevineId == null) {
                    //noinspection deprecation
                    widevineId = DeviceID.getWidevineID();
                }
            }
        }
        if (widevineId == null) {
            widevineId = "";
        }
        return widevineId;
    }

    /**
     * 通过取出ROM版本、制造商、CPU型号以及其他硬件信息来伪造设备标识
     *
     * @return 伪造的设备标识，不会为空，但会有一定的概率出现重复
     */
    public static String getPseudoID() {
        if (pseudoId == null) {
            synchronized (DeviceIdentifier.class) {
                if (pseudoId == null) {
                    pseudoId = DeviceID.getPseudoID();
                }
            }
        }
        if (pseudoId == null) {
            pseudoId = "";
        }
        return pseudoId;
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
        if (guid == null) {
            synchronized (DeviceIdentifier.class) {
                if (guid == null) {
                    guid = DeviceID.getGUID(context);
                }
            }
        }
        if (guid == null) {
            guid = "";
        }
        return guid;
    }

    /**
     * 【推荐使用】获取画布指纹。具有一定的唯一性，但不能完全保证全球唯一。
     */
    public static String getCanvasFingerprint() {
        if (canvasFingerprint == null) {
            synchronized (DeviceIdentifier.class) {
                if (canvasFingerprint == null) {
                    canvasFingerprint = DeviceID.getCanvasFingerprint();
                }
            }
        }
        if (canvasFingerprint == null) {
            canvasFingerprint = "";
        }
        return canvasFingerprint;
    }

}
