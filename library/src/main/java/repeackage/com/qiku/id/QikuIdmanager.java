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

package repeackage.com.qiku.id;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.github.gzuliyujiang.oaid.OAIDLog;

import java.lang.reflect.Method;

/**
 * @author 10cl
 * @noinspection DataFlowIssue, unused
 * @since 2024/03/06
 */
public final class QikuIdmanager {

    public static int CODE_IS_SUPPORTED = IBinder.FIRST_CALL_TRANSACTION + 1;
    public static int CODE_GET_UDID = IBinder.FIRST_CALL_TRANSACTION + 2;
    public static int CODE_GET_OAID = IBinder.FIRST_CALL_TRANSACTION + 3;
    public static int CODE_GET_VAID = IBinder.FIRST_CALL_TRANSACTION + 4;
    public static int CODE_GET_AAID = IBinder.FIRST_CALL_TRANSACTION + 5;
    public static int CODE_SHUTDOWN = IBinder.FIRST_CALL_TRANSACTION + 6;
    public static final int CODE_RESET_OAID = IBinder.FIRST_CALL_TRANSACTION + 7;
    public static final int CODE_LIMIT_READ_OAID = IBinder.FIRST_CALL_TRANSACTION + 8;
    private IBinder mIBinder = null;

    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    public QikuIdmanager() {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            String ui360 = (String) (get.invoke(c, "ro.build.uiversion", ""));
            if (ui360.contains("360UI")) {
                Class<?> ServiceManager = Class.forName("android.os.ServiceManager");
                Method method_getService = ServiceManager.getDeclaredMethod("getService", String.class);
                mIBinder = (IBinder) method_getService.invoke(null, "qikuid");
            }
        } catch (Throwable e) {
            OAIDLog.print("Failure get qikuid service");
            OAIDLog.print(e);
        }
    }

    public boolean isSupported() {
        if (mIBinder != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_IS_SUPPORTED, data, reply, 0);
                int result = reply.readInt();
                return result == 1;
            } catch (RemoteException e) {
                OAIDLog.print(e);
            } finally {
                data.recycle();
                reply.recycle();
            }
        }
        return false;
    }

    public String getUDID() {
        if (mIBinder != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_GET_UDID, data, reply, 0);
                return reply.readString();
            } catch (RemoteException e) {
                OAIDLog.print(e);
            } finally {
                data.recycle();
                reply.recycle();
            }
        }
        return null;
    }

    public String getOAID() {
        if (mIBinder != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_GET_OAID, data, reply, 0);
                return reply.readString();
            } catch (RemoteException e) {
                OAIDLog.print(e);
            } finally {
                data.recycle();
                reply.recycle();
            }
        }
        return null;
    }

    public String getVAID() {
        if (mIBinder != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_GET_VAID, data, reply, 0);
                return reply.readString();
            } catch (RemoteException e) {
                OAIDLog.print(e);
            } finally {
                data.recycle();
                reply.recycle();
            }
        }
        return null;
    }

    public String getAAID() {
        if (mIBinder != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_GET_AAID, data, reply, 0);
                return reply.readString();
            } catch (RemoteException e) {
                OAIDLog.print(e);
            } finally {
                data.recycle();
                reply.recycle();
            }
        }
        return null;
    }

    public void shutDown() {
        if (mIBinder != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_SHUTDOWN, data, reply, 0);
            } catch (RemoteException e) {
                OAIDLog.print(e);
            } finally {
                data.recycle();
                reply.recycle();
            }
        }
    }

    /**
     * true为限制了应用获取开放匿名设备标识符，false为未限制
     */
    public boolean isLimited() {
        if (mIBinder != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_LIMIT_READ_OAID, data, reply, 0);
                return reply.readInt() != 0;
            } catch (RemoteException e) {
                OAIDLog.print(e);
            } finally {
                data.recycle();
                reply.recycle();
            }
        }
        return false;
    }

}