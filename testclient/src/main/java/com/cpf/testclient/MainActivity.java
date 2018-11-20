package com.cpf.testclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private int i;

    private ImageView mIv_default_image;
    private TextView mTv_error_content;
    private Button mBtn_send_json;
    private Button mBtn_getImage;
    private Button mBtn_save_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        mTv_error_content = findViewById(R.id.home_error_content);
        mIv_default_image = findViewById(R.id.home_image);
        mBtn_getImage = findViewById(R.id.home_btn_getImage);
        mBtn_send_json = findViewById(R.id.home_btn_send_json);
        mBtn_save_image = findViewById(R.id.home_btn_save_image);

        mBtn_getImage.setOnClickListener(this);
        mBtn_save_image.setOnClickListener(this);
        mBtn_send_json.setOnClickListener(this);
    }

    /**
     * 获取用户图片
     */
    private void getAllUserPicture() {
        //注意，测试过程中url中的host 一定要与服务器端的host相同，也就是ip要相同
        String url = "http://192.168.232.2:1995/image/";
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d("lct333", "下载图片失败: "+e.toString());
                        mTv_error_content.setText("失败：获取图片失败" );
                    }
                    @Override
                    public void onResponse(Bitmap response, int id) {
                        mIv_default_image.setImageBitmap(response);
                        mTv_error_content.setText("成功：获取图片成功" );

                    }
                });
    }


    public void postImage( Bitmap bitmap) {
//        bitmap =CommUtils.adjustPhotoRotation(bitmap);
        String url ="http://192.168.232.2:1995/upload";
        String fileName = System.currentTimeMillis() + ".jpg";
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "张三");//
        File file = CommUtils.saveImg(bitmap, fileName, this);
        if (file != null) {
            try {

                OkHttpUtils.post()
                        .addFile("avatarFile", fileName, file)//
                        .url(url)
                        .params(map)
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mTv_error_content.setText("失败：上传图片失败");
                        Log.d("lct333", "1111111111111111111111111111111111111 pos odb onError:  请球网络失败call =" + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mTv_error_content.setText("成功：上传图片成功");
                        Log.d("lct333", "onResponse: 上传图片成功" + response);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     */
    public void getUserInformation() {
                String url ="http://192.168.232.2:1995/json";
        Log.d("lct", "onResponse: 开始 ");


                PersonEntity entity = new PersonEntity();
                entity.age = 18;
                entity.desc = "认真的人总是最可爱";
                entity.name = "最可爱的人";

                OkHttpUtils
                        .postString()
                        .url(url)
                        .content(new Gson().toJson(entity))
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                mTv_error_content.setText("失败："+e.toString());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("lct", "onResponse: 获取的数据 ");
                                mTv_error_content.setText("成功：" + response);


                            }
                        });
    }

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACTION_REQUEST_PERMISSIONS);

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == mBtn_save_image){
            mIv_default_image.setDrawingCacheEnabled(true);
            Bitmap bitmap = mIv_default_image.getDrawingCache();
            if(bitmap != null) {
                postImage(bitmap);
            }else{
                Toast.makeText(this,"请先获取图片，再次进行上传操作",Toast.LENGTH_SHORT).show();
            }
            mIv_default_image.setDrawingCacheEnabled(false);
        }else  if(v == mBtn_send_json){
            getUserInformation();
        }else if(v == mBtn_getImage){
            getAllUserPicture();
        }
    }
}
