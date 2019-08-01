package com.bobo.updatedialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

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
        UpdateDialog.Builder builder = new UpdateDialog.Builder();
        builder.setVersion("v1.8.8")
                .setContent("1、更新已知bug<br/>2、结算公式调整<br/>3、我的页面布局调整<br/>4、测试更新栏目")
                .setCancelable(true)
                .setDownloadUrl("http://staticscs.tyy16888.com/download/tyy_driver_v2.2.2_debug.apk")
                .build().showUpdateDialog(this);
    }
}