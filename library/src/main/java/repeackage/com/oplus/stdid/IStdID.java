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

package repeackage.com.oplus.stdid;


import repeackage.com.heytap.openid.IOpenID;

/**
 * @author luoyesiqiu
 * @since 2023/10/28 09:13
 */
public interface IStdID extends IOpenID {
    public static abstract class Stub extends IOpenID.Stub {
        private static final java.lang.String DESCRIPTOR = "com.oplus.stdid.IStdID";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IStdID asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IStdID))) {
                return ((IStdID) iin);
            }
            return (IStdID)new IStdID.Stub.Proxy(obj);
        }


        private static class Proxy implements IStdID {
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
                    boolean _status = mRemote.transact(IStdID.Stub.TRANSACTION_getSerID, _data, _reply, 0);
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
            if (Proxy.sDefaultImpl == null && impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IOpenID getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

}
