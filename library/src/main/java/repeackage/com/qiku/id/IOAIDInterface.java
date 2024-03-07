/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package repeackage.com.qiku.id;
public interface IOAIDInterface extends android.os.IInterface
{
  /** Default implementation for IOAIDInterface. */
  public static class Default implements IOAIDInterface
  {
    @Override public boolean isSupported() throws android.os.RemoteException
    {
      return false;
    }
    @Override public String getUDID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public String getOAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public String getVAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public String getAAID() throws android.os.RemoteException
    {
      return null;
    }
    @Override public void shutDown() throws android.os.RemoteException
    {
    }
    @Override public void resetOAID() throws android.os.RemoteException
    {
    }
    @Override public boolean limitReadOAID() throws android.os.RemoteException
    {
      return false;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements IOAIDInterface
  {
    private static final String DESCRIPTOR = "com.qiku.id.IOAIDInterface";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.qiku.id.IOAIDInterface interface,
     * generating a proxy if needed.
     */
    public static IOAIDInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof IOAIDInterface))) {
        return ((IOAIDInterface)iin);
      }
      return new Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_isSupported:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.isSupported();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        case TRANSACTION_getUDID:
        {
          data.enforceInterface(descriptor);
          String _result = this.getUDID();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_getOAID:
        {
          data.enforceInterface(descriptor);
          String _result = this.getOAID();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_getVAID:
        {
          data.enforceInterface(descriptor);
          String _result = this.getVAID();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_getAAID:
        {
          data.enforceInterface(descriptor);
          String _result = this.getAAID();
          reply.writeNoException();
          reply.writeString(_result);
          return true;
        }
        case TRANSACTION_shutDown:
        {
          data.enforceInterface(descriptor);
          this.shutDown();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_resetOAID:
        {
          data.enforceInterface(descriptor);
          this.resetOAID();
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_limitReadOAID:
        {
          data.enforceInterface(descriptor);
          boolean _result = this.limitReadOAID();
          reply.writeNoException();
          reply.writeInt(((_result)?(1):(0)));
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements IOAIDInterface
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public boolean isSupported() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_isSupported, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().isSupported();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public String getUDID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getUDID, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getUDID();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public String getOAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getOAID, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getOAID();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public String getVAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getVAID, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getVAID();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public String getAAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        String _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_getAAID, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().getAAID();
          }
          _reply.readException();
          _result = _reply.readString();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      @Override public void shutDown() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_shutDown, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().shutDown();
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void resetOAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_resetOAID, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().resetOAID();
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public boolean limitReadOAID() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        boolean _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_limitReadOAID, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().limitReadOAID();
          }
          _reply.readException();
          _result = (0!=_reply.readInt());
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static IOAIDInterface sDefaultImpl;
    }
    static final int TRANSACTION_isSupported = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_getUDID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_getOAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    static final int TRANSACTION_getVAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
    static final int TRANSACTION_getAAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
    static final int TRANSACTION_shutDown = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    static final int TRANSACTION_resetOAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
    static final int TRANSACTION_limitReadOAID = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
    public static boolean setDefaultImpl(IOAIDInterface impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static IOAIDInterface getDefaultImpl() {
      return Proxy.sDefaultImpl;
    }
  }
  public boolean isSupported() throws android.os.RemoteException;
  public String getUDID() throws android.os.RemoteException;
  public String getOAID() throws android.os.RemoteException;
  public String getVAID() throws android.os.RemoteException;
  public String getAAID() throws android.os.RemoteException;
  public void shutDown() throws android.os.RemoteException;
  public void resetOAID() throws android.os.RemoteException;
  public boolean limitReadOAID() throws android.os.RemoteException;
}
