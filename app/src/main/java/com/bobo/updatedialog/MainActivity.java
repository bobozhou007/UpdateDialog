package com.bobo.updatedialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.bobo.updatedialoglib.UpdateDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        showUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showUpdate() {
        new UpdateDialog.Builder()
                .setVersion("v1.0.1")
                .setContent("1.下载链接兼容中文<br/>2.下载链接可以是非以.apk结尾的文件的链接<br/>3.兼容7.0系统及以上apk文件的访问权限")
                .setCancelable(true)
                .setDebug(true)
                .setDownloadUrl("https://statics.tyy16888.com/download/tyy_driver_07-17_19_49_v2.5.3_release.apk")
                .build().showUpdateDialog(this);
    }
}
