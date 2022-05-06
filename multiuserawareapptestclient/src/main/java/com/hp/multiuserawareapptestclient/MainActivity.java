package com.hp.multiuserawareapptestclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hp.multiuserawareapptest.ISingletonService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CLIENT.MainActivity";
    private TextView m_stateView = null;
    private TextView m_resultView = null;
    private Button m_button = null;
    private UserManager mUserManager;
    private ISingletonService mService = null;
    private MyServiceConnection mServiceConnection = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserManager = (UserManager) getSystemService(Context.USER_SERVICE);
        setContentView(R.layout.activity_main);
        m_stateView = findViewById(R.id.service_state);
        m_resultView = findViewById(R.id.result);
        m_button = findViewById(R.id.button);

        m_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_resultView.setText("");
                if (mService == null) {
                    m_resultView.setText("Service is not connected");
                    return;
                }

                try {
                    String serviceUserHandleString = mService.getServiceUserHandle();
                    m_resultView.setText(serviceUserHandleString);
                } catch (RemoteException re) {
                    Log.e(TAG, re.toString());
                }
            }
        });

        mServiceConnection = new MyServiceConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mServiceConnection.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mServiceConnection.disconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyServiceConnection implements ServiceConnection {
        private boolean mIsBound = false;
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ISingletonService.Stub.asInterface(iBinder);
            Log.d(TAG, "Service connected");
            m_stateView.setText("Service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            Log.d(TAG, "Service disconnected");
            m_stateView.setText("Service disconnected");
        }

        public void connect() {
            Log.d(TAG, "Connect service");
            if (mIsBound) {
                Log.w(TAG, "Service is already bound");
                return;
            }
            Intent intent = new Intent();
            intent.setClassName("com.hp.multiuserawareapptest", "com.hp.multiuserawareapptest.SingletonService");
            mIsBound = bindService(intent, this, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bind service result: " + mIsBound);
        }

        public void disconnect() {
            Log.d(TAG, "Disconnect service");
            if (!mIsBound) {
                Log.w(TAG, "Service is not bound");
                return;
            }
            unbindService(this);
            mIsBound = false;
        }
    }
}