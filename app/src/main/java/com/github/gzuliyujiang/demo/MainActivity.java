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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IOAIDGetter;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MyHandler handler;
    private TextView tvOAID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MyHandler(this);
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
            deviceId.doGet(new IOAIDGetter() {
                @Override
                public void onOAIDGetComplete(@NonNull String oaid) {
                    Logger.print("onOAIDGetComplete====>" + deviceId);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = deviceId;
                    handler.sendMessage(msg);
                }

                @Override
                public void onOAIDGetError(@NonNull Exception exception) {
                    Logger.print("onOAIDGetError====>" + exception);
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = exception;
                    handler.sendMessage(msg);
                }
            });
        }
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
