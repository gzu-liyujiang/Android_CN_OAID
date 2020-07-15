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
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.github.gzuliyujiang.oaid.IDeviceId;
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
        tvDeviceInfo.setText(DeviceID.deviceInfo());
        findViewById(R.id.btn_oaid_get).setOnClickListener(this);
        tvOAID = findViewById(R.id.tv_oaid_result);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_oaid_get) {
            Logger.print("DeviceID doGet");
            IDeviceId deviceId = DeviceID.with(this);
            if (!deviceId.supportOAID()) {
                tvOAID.setText("不支持OAID，须自行生成GUID");
                return;
            }
            deviceId.doGet(new IGetter() {
                @Override
                public void onDeviceIdGetComplete(@NonNull String deviceId) {
                    Logger.print("onDeviceIdGetComplete====>" + deviceId);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = deviceId;
                    handler.sendMessage(msg);
                }

                @Override
                public void onDeviceIdGetError(@NonNull Exception exception) {
                    Logger.print("onDeviceIdGetError====>" + exception);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = exception;
                    handler.sendMessage(msg);
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
            if (msg.what == -1) {
                mainActivity.tvOAID.setText(String.format("出错了：%s", msg.obj.toString()));
            } else {
                mainActivity.tvOAID.setText(msg.obj.toString());
            }
        }

    }

}
