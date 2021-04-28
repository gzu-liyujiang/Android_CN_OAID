/*
 * Copyright (c) 2019-2021 gzu-liyujiang <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 *
 */

/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package repeackage.com.heytap.openid;

/**
 * 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题：
 * <pre>
 *     // IOpenID.aidl
 *     package com.heytap.openid;
 *
 *     interface IOpenID {
 *
 *         String getSerID(String pkgName, String sign, String type);
 *
 *     }
 * </pre>
 */
@SuppressWarnings("All")
public interface IOpenID extends android.os.IInterface {
    /**
     * Default implementation for IOpenID.
     */
    public static class Default implements IOpenID {
        @Override
        public java.lang.String getSerID(java.lang.String pkgName, java.lang.String sign, java.lang.String type) throws android.os.RemoteException {
            return null;
        }

        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IOpenID {
        private static final java.lang.String DESCRIPTOR = "com.heytap.openid.IOpenID";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an repeackage.com.heytap.openid.IOpenID interface,
         * generating a proxy if needed.
         */
        public static IOpenID asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IOpenID))) {
                return ((IOpenID) iin);
            }
            return new IOpenID.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_getSerID: {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    java.lang.String _result = this.getSerID(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements IOpenID {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public java.lang.String getSerID(java.lang.String pkgName, java.lang.String sign, java.lang.String type) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeString(sign);
                    _data.writeString(type);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_getSerID, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getSerID(pkgName, sign, type);
                    }
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            public static IOpenID sDefaultImpl;
        }

        static final int TRANSACTION_getSerID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);

        public static boolean setDefaultImpl(IOpenID impl) {
            if (Stub.Proxy.sDefaultImpl == null && impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IOpenID getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }

    public java.lang.String getSerID(java.lang.String pkgName, java.lang.String sign, java.lang.String type) throws android.os.RemoteException;
}
