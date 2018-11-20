package com.leavesc.androidserver.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.leavesc.androidserver.MainApplication;
import com.leavesc.androidserver.R;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.SimpleRequestHandler;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.view.View;

import org.apache.httpcore.HttpEntity;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.entity.ContentType;
import org.apache.httpcore.entity.FileEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static com.yanzhenjie.andserver.util.FileUtils.getMimeType;

/**
 * 作者：leavesC
 * 时间：2018/4/5 16:30
 * 描述：https://github.com/leavesC/AndroidServer
 * https://www.jianshu.com/u/9df45b87cfdf
 */
public class DownloadImageHandler extends SimpleRequestHandler {

    public static final String TAG = "DownloadImageHandler";
    private File file = new File(Environment.getExternalStorageDirectory(), "leavesC.jpg");

    @RequestMapping(method = {RequestMethod.GET})
    @Override
    protected View handle(HttpRequest request) throws HttpException, IOException {
        Log.e(TAG,"开始下载图片 start");
        writeToSdCard();
        HttpEntity httpEntity = new FileEntity(file, ContentType.create(getMimeType(file.getAbsolutePath()), Charset.defaultCharset()));
        return new View(200, httpEntity);
    }

    private void writeToSdCard() throws IOException {
        Log.e(TAG,"开始下载图片。。。。");

        if (!file.exists()) {
            synchronized (DownloadImageHandler.class) {
                if (!file.exists()) {
                    boolean b = file.createNewFile();
                    if (!b) {
                        throw new IOException("What broken cell phone.");
                    }
                    Bitmap bitmap = BitmapFactory.decodeResource(MainApplication.get().getResources(), R.mipmap.default_icon);
                    OutputStream outputStream = null;
                    try {
                        Log.e(TAG,"开始下载图片写入");
                        outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG,"开始下载图片失败" +e.toString() );
                    } finally {
                        if (outputStream != null) {
                            outputStream.flush();
                            outputStream.close();
                        }
                    }
                }
            }
        }
    }

}