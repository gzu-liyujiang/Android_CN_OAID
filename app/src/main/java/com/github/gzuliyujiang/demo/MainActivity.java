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

package com.github.gzuliyujiang.demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.IGetter;
import com.yanzhenjie.permission.AndPermission;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String[] PERMISSIONS_All_NEED = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private MyHandler handler;
    private TextView tvOAID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MyHandler(this);
        checkAllPermissions(this);
        setContentView(R.layout.activity_main);
        TextView tvDeviceInfo = findViewById(R.id.tv_device_info);
        tvDeviceInfo.setText("品牌型号：");
        tvDeviceInfo.append(Build.BRAND);
        tvDeviceInfo.append(" ");
        tvDeviceInfo.append(Build.MODEL);
        tvDeviceInfo.append("\n");
        tvDeviceInfo.append("生产厂商：");
        tvDeviceInfo.append(Build.MANUFACTURER);
        tvDeviceInfo.append("\n");
        tvDeviceInfo.append("系统版本：");
        tvDeviceInfo.append(Build.VERSION.RELEASE);
        tvDeviceInfo.append(" (API ");
        tvDeviceInfo.append(String.valueOf(Build.VERSION.SDK_INT));
        tvDeviceInfo.append(")");
        findViewById(R.id.btn_get_oaid).setOnClickListener(this);
        tvOAID = findViewById(R.id.tv_oaid);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void checkAllPermissions(final Context context) {
        AndPermission.with(context)
                .runtime()
                .permission(PERMISSIONS_All_NEED)
                .onDenied(list -> {
                    if (AndPermission.hasAlwaysDeniedPermission(context, PERMISSIONS_All_NEED)) {
                        Toast.makeText(context, "部分功能被禁止，被禁止的功能将无法使用", Toast.LENGTH_SHORT).show();
                        Logger.print("部分功能被禁止");
                        showNormalDialog(MainActivity.this);
                    }
                }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_oaid) {
            Logger.print("DeviceID doGet");
            DeviceID.with(this).doGet(new IGetter() {
                @Override
                public void onDeviceIdGetComplete(@NonNull String deviceId) {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = deviceId;
                    handler.sendMessage(msg);
                    Logger.print("onDeviceIdGetComplete====>" + deviceId);
                }

                @Override
                public void onDeviceIdGetError(@NonNull Exception exception) {
                    Logger.print("onDeviceIdGetError====>" + exception);
                }
            });
        }
    }

    private void showNormalDialog(final Context context) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle("去申请权限");
        normalDialog.setMessage("部分权限被你禁止了，可能误操作，可能会影响部分功能，是否去要去重新设置？");
        normalDialog.setPositiveButton("是",
                (dialog, which) -> getAppDetailSettingIntent(context));
        normalDialog.setNegativeButton("否",
                (dialog, which) -> dialog.dismiss());
        normalDialog.show();
    }

    static private void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> activity;

        private MyHandler(MainActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MainActivity mainActivity = activity.get();
            if (mainActivity == null) {
                return;
            }
            if (msg.what == 1) {
                mainActivity.tvOAID.setText(msg.obj.toString());
            }
        }

    }

}
