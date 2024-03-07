package repeackage.com.qiku.id;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.Method;

public class QikuIdmanager {
    final static String TAG = "QikuIdmanager";
    private IBinder mIBinder = null;


    public static int CODE_IS_SUPPORTED =IBinder.FIRST_CALL_TRANSACTION + 1;
    public static int CODE_GET_UDID =IBinder.FIRST_CALL_TRANSACTION + 2;
    public static int CODE_GET_OAID =IBinder.FIRST_CALL_TRANSACTION + 3;
    public static int CODE_GET_VAID =IBinder.FIRST_CALL_TRANSACTION + 4;
    public static int CODE_GET_AAID =IBinder.FIRST_CALL_TRANSACTION + 5;
    public static int CODE_SHUTDOWN =IBinder.FIRST_CALL_TRANSACTION + 6;
    public static final int CODE_RESET_OAID =IBinder.FIRST_CALL_TRANSACTION + 7;
    public static final int CODE_LIMIT_READ_OAID =IBinder.FIRST_CALL_TRANSACTION + 8;


    public QikuIdmanager() {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            String ui360 = (String)(get.invoke(c, "ro.build.uiversion", ""));

            if (ui360.contains("360UI")) {
                Class<?> ServiceManager = Class.forName("android.os.ServiceManager");
                if (ServiceManager != null) {
                    Method method_getService = ServiceManager.getDeclaredMethod("getService", String.class);
                    if (method_getService != null) {
                        mIBinder = (IBinder)method_getService.invoke(null, "qikuid");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failure get qikuid service", e);
        }
    }


    public boolean isSupported() {
        if (mIBinder != null) {

            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_IS_SUPPORTED, data, reply, 0);
                int result = reply.readInt();
                return result == 1 ? true:false;
            } catch (RemoteException e) {
                e.printStackTrace();
            }finally{
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
                String udid = reply.readString();
                return udid;
            } catch (RemoteException e) {
                e.printStackTrace();
            }finally{
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
                String oaid = reply.readString();
                return oaid;
            } catch (RemoteException e) {
                e.printStackTrace();
            }finally{
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
                String vaid = reply.readString();
                return vaid;
            } catch (RemoteException e) {
                e.printStackTrace();
            }finally{
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
                String aaid = reply.readString();
                return aaid;
            } catch (RemoteException e) {
                e.printStackTrace();
            }finally{
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
                e.printStackTrace();
            }finally{
                data.recycle();
                reply.recycle();
            }
        }
    }

    //true为限制了应用获取开放匿名设备标识符，false为未限制
    public boolean isLimited() {
        if (mIBinder != null) {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                mIBinder.transact(CODE_LIMIT_READ_OAID, data, reply, 0);
                boolean islimited = reply.readBoolean();
                return islimited;
            } catch (RemoteException e) {
                e.printStackTrace();
            }finally{
                data.recycle();
                reply.recycle();
            }
        }
        return false;
    }
}