package com.leavesc.androidserver.handler;

import android.os.Environment;
import android.util.Log;

import com.yanzhenjie.andserver.RequestHandler;
import com.yanzhenjie.andserver.RequestMethod;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.upload.HttpFileUpload;
import com.yanzhenjie.andserver.upload.HttpUploadContext;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.httpcore.HttpEntityEnclosingRequest;
import org.apache.httpcore.HttpException;
import org.apache.httpcore.HttpRequest;
import org.apache.httpcore.HttpResponse;
import org.apache.httpcore.entity.StringEntity;
import org.apache.httpcore.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UploadFileHandler implements RequestHandler {

    private static final String TAG = "UploadFileHandler";
    /**
     * 保存文件的文件夹。
     */
    private File mDirectory = Environment.getExternalStorageDirectory();

    @RequestMapping(method = {RequestMethod.POST})
    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {
        Log.e(TAG,"开始接受文件上传" + request.toString());
        Log.e(TAG,"开始接受文件上传v0" + HttpRequestParser.isMultipartContentRequest(request));

        if (!HttpRequestParser.isMultipartContentRequest(request)) { // 是否Form传文件的请求。
            Log.e(TAG,"开始接受文件上传v0");

            response(403, "说好的文件呢", response);
        } else {
            try {
                Log.e(TAG,"开始接受文件上传v1");
                processFileUpload(request);
                response(200, "上传成功", response);
            } catch (Exception e) {
                Log.e(TAG,"开始接受文件上传v2");
                response(500, "保存文件失败", response);
            }
        }
        Log.e(TAG,"开始接受文件上传v3" + request.toString());

    }

    private void response(int responseCode, String message, HttpResponse response)
            throws HttpException, IOException {
        response.setStatusCode(responseCode);
        response.setEntity(new StringEntity(message, "utf-8"));
    }

    /**
     * 保存文件和参数处理。
     */
    private void processFileUpload(HttpRequest request) throws Exception {
        FileItemFactory factory = new DiskFileItemFactory(1024 * 1024, mDirectory);
        HttpFileUpload fileUpload = new HttpFileUpload(factory);
        Log.e(TAG,"开始接受文件上传v4");

        // 你还可以监听上传进度：
        // fileUpload.setProgressListener(new ProgressListener(){...});

        HttpUploadContext context = new HttpUploadContext((HttpEntityEnclosingRequest) request);
        Log.e(TAG,"开始接受文件上传v5");

        List<FileItem> fileItems = fileUpload.parseRequest(context);
        Log.e(TAG,"开始接受文件上传v6");
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) { // 普通参数。
                Log.e(TAG,"开始接受文件上传v7" + fileItem.getString());
                String key = fileItem.getName();      // 表单参数名。
                String value = fileItem.getString();  // 表单参数值。
            } else { // 文件。
                // fileItem.getFieldName();           // 表单参数名。
                // fileItem.getName();                // 客户端指定的文件名。
                // fileItem.getSize();                // 文件大小。
                // fileItem.getContentType();         // 文件的MimeType。

                // 把流写到文件夹。
                File uploadedFile = new File(mDirectory, fileItem.getName());
                fileItem.write(uploadedFile);
            }
        }
    }
}