package com.leavesc.androidserver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.leavesc.androidserver.common.Constants;
import com.leavesc.androidserver.server.OnServerChangeListener;
import com.leavesc.androidserver.server.ServerPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：leavesC
 * 时间：2018/4/5 16:30
 * 描述：https://github.com/leavesC/AndroidServer
 * https://www.jianshu.com/u/9df45b87cfdf
 */
public class MainActivity extends BaseActivity implements OnServerChangeListener {

    private ServerPresenter serverPresenter;

    private Button btn_startServer;

    private Button btn_stopServer;
    private ImageView imageView;
    private TextView tv_message;
    static MainActivity mInstance;
    private final int REQUEST_WRITE_EXTERNAL_STORAGE = 10;
    private static final int UPDATE_UI = 201;
    private static final String TAG = "MainActivity";
    private Bitmap  mBitmap;
    private Handler mTotalHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:

                    imageView.setImageBitmap(mBitmap);

            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = this;
        initView();
        imageView = findViewById(R.id.act_imag);
        serverPresenter = new ServerPresenter(this, this);
        btn_startServer.performClick();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
    public static MainActivity getInstance() {
        return mInstance;
    }
    private void initView() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_startServer: {
                        serverPresenter.startServer(MainActivity.this);
                        break;
                    }
                    case R.id.btn_stopServer: {
                        serverPresenter.stopServer(MainActivity.this);
                        break;
                    }
                }
            }
        };
        btn_startServer = findViewById(R.id.btn_startServer);
        btn_stopServer = findViewById(R.id.btn_stopServer);
        tv_message = findViewById(R.id.tv_message);
        btn_startServer.setOnClickListener(clickListener);
        btn_stopServer.setOnClickListener(clickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverPresenter.unregister(this);
        serverPresenter = null;
    }

    @Override
    public void onServerStarted(String ipAddress) {
        btn_startServer.setVisibility(View.GONE);
        btn_stopServer.setVisibility(View.VISIBLE);
        Log.e(TAG, "IP Address: " + ipAddress);
        if (!TextUtils.isEmpty(ipAddress)) {
            List<String> addressList = new ArrayList<>();
            addressList.add("http://" + ipAddress + ":" + Constants.PORT_SERVER + Constants.GET_FILE);
            addressList.add("http://" + ipAddress + ":" + Constants.PORT_SERVER + Constants.GET_IMAGE);
            addressList.add("http://" + ipAddress + ":" + Constants.PORT_SERVER + Constants.POST_JSON);
            tv_message.setText(TextUtils.join("\n", addressList));
        } else {
            tv_message.setText("error");
        }
    }

    @Override
    public void onServerStopped() {
        btn_startServer.setVisibility(View.VISIBLE);
        btn_stopServer.setVisibility(View.GONE);
        tv_message.setText("服务器停止了");
    }

    @Override
    public void onServerError(String errorMessage) {
        btn_startServer.setVisibility(View.VISIBLE);
        btn_stopServer.setVisibility(View.GONE);
        tv_message.setText(errorMessage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("请开放文件写入权限");
                    finish();
                }
                break;
        }
    }
    public  void setImag(String string){
         mBitmap = stringToBitmap(string);
        mTotalHandler.sendEmptyMessage(UPDATE_UI);
        }
    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
