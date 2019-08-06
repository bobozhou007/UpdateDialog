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
                .setContent("1.下载链接兼容中文<br/>2.下载链接可以是非以.apk结尾的文件的链接<br/>3.兼容7.0系统及以上apk文件的访问权限<br/>4.是会计法asked爱丽丝到付阿斯顿发了爱丽丝短发史蒂夫爱上短发拉升发达舒服了阿时间到付爱丽丝放大了阿舒服点阿三短发史蒂夫了阿三发达舒服多了阿理发店拉萨短发时短发阿三了短发拉萨短发就啊老师短发阿舒服点拉屎短发就啊是的肌肤拉风啦是否阿三了短发阿斯顿发生发生发生短发阿斯顿发生短发阿三分阿三发生发大水发达阿三了短发手拉大发生了短发手拉大房间里说的非拉屎的将弗拉索夫大连市短发拉升发达史蒂夫阿三代理费阿三短发时短发沙发上短发拉升短发拉萨的非拉萨非")
                .setCancelable(true)
                .setDebug(true)
                .setDownloadUrl("https://statics.tyy16888.com/download/tyy_driver_07-17_19_49_v2.5.3_release.apk")
                .build().showUpdateDialog(this);
    }
}
