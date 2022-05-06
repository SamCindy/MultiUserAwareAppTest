package com.hp.multiuserawareapptest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

public class SingletonService extends Service {
    private static final String TAG = SingletonService.class.getSimpleName();
    private UserManager mUserManager;
    public SingletonService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mUserManager = (UserManager)getSystemService(Context.USER_SERVICE);
        Log.d(TAG, "onCreate: is system user ? " + mUserManager.isSystemUser());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: is system user ? " + mUserManager.isSystemUser());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSrvInstance;
    }

    private String getServiceUserHandle() {
        UserHandle uh = android.os.Process.myUserHandle();
        Log.d(TAG, "Call getServiceUserHandle ? " + mUserManager.isSystemUser());
        return uh.toString();
    }

    private final ISingletonService.Stub mSrvInstance = new ISingletonService.Stub() {
        @Override
        public String getServiceUserHandle() throws RemoteException {
            return SingletonService.this.getServiceUserHandle();
        }
    };
}