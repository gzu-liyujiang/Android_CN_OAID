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
package repeackage.com.uodis.opendevice.aidl;

/**
 * 本文件代码根据以下AIDL生成，只改包名以便解决和移动安全联盟的SDK冲突问题：
 * <pre>
 *     // OpenDeviceIdentifierService.aidl
 *     package com.uodis.opendevice.aidl;
 *
 *     interface OpenDeviceIdentifierService {
 *
 *         String getOaid();
 *
 *         boolean isOaidTrackLimited();
 *
 *     }
 * </pre>
 */
@SuppressWarnings("All")
public interface OpenDeviceIdentifierService extends android.os.IInterface {
    /**
     * Default implementation for OpenDeviceIdentifierService.
     */
    public static class Default implements OpenDeviceIdentifierService {
        @Override
        public java.lang.String getOaid() throws android.os.RemoteException {
            return null;
        }

        @Override
        public boolean isOaidTrackLimited() throws android.os.RemoteException {
            return false;
        }

        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements OpenDeviceIdentifierService {
        private static final java.lang.String DESCRIPTOR = "com.uodis.opendevice.aidl.OpenDeviceIdentifierService";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an repeackage.com.uodis.opendevice.aidl.OpenDeviceIdentifierService interface,
         * generating a proxy if needed.
         */
        public static OpenDeviceIdentifierService asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof OpenDeviceIdentifierService))) {
                return ((OpenDeviceIdentifierService) iin);
            }
            return new OpenDeviceIdentifierService.Stub.Proxy(obj);
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
                case TRANSACTION_getOaid: {
                    data.enforceInterface(descriptor);
                    java.lang.String _result = this.getOaid();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                }
                case TRANSACTION_isOaidTrackLimited: {
                    data.enforceInterface(descriptor);
                    boolean _result = this.isOaidTrackLimited();
                    reply.writeNoException();
                    reply.writeInt(((_result) ? (1) : (0)));
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements OpenDeviceIdentifierService {
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
            public java.lang.String getOaid() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.lang.String _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_getOaid, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getOaid();
                    }
                    _reply.readException();
                    _result = _reply.readString();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public boolean isOaidTrackLimited() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                boolean _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_isOaidTrackLimited, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().isOaidTrackLimited();
                    }
                    _reply.readException();
                    _result = (0 != _reply.readInt());
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            public static OpenDeviceIdentifierService sDefaultImpl;
        }

        static final int TRANSACTION_getOaid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_isOaidTrackLimited = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);

        public static boolean setDefaultImpl(OpenDeviceIdentifierService impl) {
            // Only one user of this interface can use this function
            // at a time. This is a heuristic to detect if two different
            // users in the same process use this function.
            if (Stub.Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static OpenDeviceIdentifierService getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }

    public java.lang.String getOaid() throws android.os.RemoteException;

    public boolean isOaidTrackLimited() throws android.os.RemoteException;
}
