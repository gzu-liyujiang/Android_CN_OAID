/*
 * Copyright (c) 2019-2021 gzu-liyujiang <1032694760@qq.com>
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
package com.github.gzuliyujiang.oaid.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAIDGetter;
import com.uodis.opendevice.aidl.OpenDeviceIdentifierService;

/**
 * //public class HuaweiDeviceIdImpl implements IDeviceId {
 * //    private final Context context;
 * //
 * //    public HuaweiAdvertisingIdImpl(Context context) {
 * //        this.context = context;
 * //    }
 * //
 * //    @Override
 * //    public boolean supportOAID() {
 * //        try {
 * //            Class.forName("com.huawei.hms.ads.identifier.AdvertisingIdClient");
 * //            return true;
 * //        } catch (Exception e) {
 * //            Logger.print(e);
 * //            return false;
 * //        }
 * //    }
 * //
 * //    @Override
 * //    public void doGet(@NonNull final IOAIDGetter getter) {
 * //        final Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
 * //            @Override
 * //            public boolean handleMessage(@NonNull Message msg) {
 * //                if (msg.what == 1) {
 * //                    getter.onOAIDGetComplete(msg.obj.toString());
 * //                } else {
 * //                    getter.onOAIDGetError((Exception) msg.obj);
 * //                }
 * //                return true;
 * //            }
 * //        });
 * //        Executors.newSingleThreadExecutor().execute(new Runnable() {
 * //            @Override
 * //            public void run() {
 * //                try {
 * //                    AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
 * //                    if (info == null || info.getId() == null) {
 * //                        throw new RuntimeException("Huawei AdvertisingIdClient.Info get failed");
 * //                    }
 * //                    handler.obtainMessage(1, info.getId()).sendToTarget();
 * //                } catch (Exception e) {
 * //                    Logger.print(e);
 * //                    handler.obtainMessage(-1, e).sendToTarget();
 * //                }
 * //            }
 * //        });
 * //    }
 * //
 * //    @SuppressWarnings("deprecation")
 * //    @Override
 * //    public void doGet(@NonNull final IGetter getter) {
 * //        doGet(new IOAIDGetter() {
 * //            @Override
 * //            public void onOAIDGetComplete(@NonNull String oaid) {
 * //                getter.onDeviceIdGetComplete(oaid);
 * //            }
 * //
 * //            @Override
 * //            public void onOAIDGetError(@NonNull Exception exception) {
 * //                getter.onDeviceIdGetError(exception);
 * //            }
 * //        });
 * //    }
 * //
 * //}
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民（1032694760@qq.com）
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class HuaweiDeviceIdImpl implements IDeviceId {
    private final Context context;

    public HuaweiDeviceIdImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supportOAID() {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.huawei.hwid", 0);
            return pi != null;
        } catch (Exception e) {
            Logger.print(e);
            return false;
        }
    }

    @Override
    public void doGet(@NonNull final IOAIDGetter getter) {
        Intent intent = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
        intent.setPackage("com.huawei.hwid");
        try {
            boolean isBinded = context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Logger.print("Huawei OPENIDS_SERVICE connected");
                    try {
                        OpenDeviceIdentifierService anInterface = OpenDeviceIdentifierService.Stub.asInterface(service);
                        String IDs = anInterface.getIDs();
                        if (IDs == null || IDs.length() == 0) {
                            throw new RuntimeException("Huawei IDs get failed");
                        }
                        getter.onOAIDGetComplete(IDs);
                    } catch (Exception e) {
                        Logger.print(e);
                        getter.onOAIDGetError(e);
                    } finally {
                        context.unbindService(this);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Logger.print("Huawei OPENIDS_SERVICE disconnected");
                }
            }, Context.BIND_AUTO_CREATE);
            if (!isBinded) {
                throw new RuntimeException("Huawei OPENIDS_SERVICE bind failed");
            }
        } catch (Exception e) {
            getter.onOAIDGetError(e);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void doGet(@NonNull final IGetter getter) {
        doGet(new IOAIDGetter() {
            @Override
            public void onOAIDGetComplete(@NonNull String oaid) {
                getter.onDeviceIdGetComplete(oaid);
            }

            @Override
            public void onOAIDGetError(@NonNull Exception exception) {
                getter.onDeviceIdGetError(exception);
            }
        });
    }

}
