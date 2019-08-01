package com.bobo.updatedialoglib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;


public class UpdateDialog extends Fragment {

    private ImageView closeIv;
    private TextView versionTv, contentTv, progressTv;
    private Button confirmBtn;
    private String version, content, appUrl, path;
    private boolean cancelable;
    private OnConfirmListener listener;
    //    private LinearLayout parent;
    private FragmentActivity mActivity;
    private final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0x100;
    private BaseDownloadTask baseDownloadTask;
    private RelativeLayout progressRl;
    private BarPercentView barPercentView;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FileDownloader.setup(mActivity);
        initViews(view);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initViews(@NonNull View view) {
        closeIv = view.findViewById(R.id.fragment_update_closeIv);
        versionTv = view.findViewById(R.id.fragment_update_versionTv);
        contentTv = view.findViewById(R.id.fragment_update_contentTv);
        confirmBtn = view.findViewById(R.id.fragment_update_confirmBtn);
        progressRl = view.findViewById(R.id.fragment_update_progressRl);
        progressTv = view.findViewById(R.id.fragment_update_progressTv);
        barPercentView = view.findViewById(R.id.fragment_update_barPercentView);
        closeIv.setVisibility(cancelable ? View.VISIBLE : View.GONE);
        confirmBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
                return;
            } else {
                downloadApk(appUrl);
            }
            if (listener != null) listener.onConfirm(v);
        });
        progressRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barPercentView.getProgress() == 1) {
                    install(new File(path), mActivity);
                }
            }
        });
        closeIv.setOnClickListener(v -> {
            if (baseDownloadTask == null){
                dismissUpdateDialog();
                return;
            }
            if (!baseDownloadTask.isRunning() || baseDownloadTask.pause()) {
                dismissUpdateDialog();
            }
        });
        versionTv.setText(TextUtils.isEmpty(version) ? "" : version);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentTv.setText(Html.fromHtml(TextUtils.isEmpty(content) ? "" : content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            contentTv.setText(Html.fromHtml(TextUtils.isEmpty(content) ? "" : content));
        }
    }

    public static class Builder {

        private UpdateDialog updateDialog;

        public Builder() {
            this.updateDialog = new UpdateDialog();
        }

        public Builder setVersion(String version) {
            updateDialog.version = version;
            return this;
        }

        public Builder setContent(String content) {
            updateDialog.content = content;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            updateDialog.cancelable = cancelable;
            return this;
        }

        public Builder setDownloadUrl(String url) {
            updateDialog.appUrl = url;
            return this;
        }

        public Builder setOnConfirmListener(OnConfirmListener listener) {
            updateDialog.listener = listener;
            return this;
        }

        public UpdateDialog build() {
            return updateDialog;
        }


    }

    public interface OnConfirmListener {
        void onConfirm(View view);
    }

    public void showUpdateDialog(FragmentActivity activity) {
        if (this.isAdded() && this.isVisible()) return;
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(android.R.id.content, this);
        fragmentTransaction.setCustomAnimations(R.anim.push_bottom_in, R.anim.push_bottom_out);
        fragmentTransaction.show(this);
        if (activity.isFinishing() || activity.isDestroyed()) {
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            fragmentTransaction.commit();
        }
//        setBackgroundAlpha(activity,0.6f);
    }

    public void dismissUpdateDialog() {
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (this.isAdded() && this.isVisible()) {
            fragmentTransaction.setCustomAnimations(R.anim.push_bottom_in, R.anim.push_bottom_out);
            fragmentTransaction.hide(this);
            fragmentTransaction.commit();
            new Handler().postDelayed(this::destroyUpdateDialog, 500);
        }
//        setBackgroundAlpha(Objects.requireNonNull(getActivity()),1f);
    }

    private void destroyUpdateDialog() {
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(this);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    private void downloadApk(String apkUrl) {
        progressRl.setVisibility(View.VISIBLE);
        confirmBtn.setVisibility(View.GONE);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + apkUrl.substring(apkUrl.lastIndexOf("/") + 1);
        final File file = new File(path);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!dir.exists() && dir.mkdir()) {
            baseDownloadTask = FileDownloader.getImpl().create(apkUrl).setPath(path).setCallbackProgressTimes(10).setListener(new FileDownloadLargeFileListener() {

                @Override
                protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                }

                @Override
                protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                    float percent = 1f * soFarBytes / totalBytes * 100;
                    if (percent >= 3) {
                        barPercentView.setPercentage(percent);
                        progressTv.setText((int) percent + "%");
                    }
                }

                @Override
                protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                }

                @Override
                protected void completed(BaseDownloadTask task) {
                    barPercentView.setPercentage(100);
                    progressTv.setText(100 + "%");
                    install(file, mActivity);
                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {

                }

                @Override
                protected void warn(BaseDownloadTask task) {

                }
            });
            baseDownloadTask.setAutoRetryTimes(5);
            baseDownloadTask.start();
        } else {
            baseDownloadTask = FileDownloader.getImpl().create(apkUrl).setPath(path).setCallbackProgressTimes(10).setListener(new FileDownloadLargeFileListener() {

                @Override
                protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                }

                @Override
                protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                    float percent = 1f * soFarBytes / totalBytes * 100;
                    if (percent >= 3) {
                        barPercentView.setPercentage(percent);
                        progressTv.setText((int) percent + "%");
                    }
                }

                @Override
                protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {

                }

                @Override
                protected void completed(BaseDownloadTask task) {
                    barPercentView.setPercentage(100);
                    progressTv.setText(100 + "%");
                    install(file, mActivity);
                }

                @Override
                protected void error(BaseDownloadTask task, Throwable e) {

                }

                @Override
                protected void warn(BaseDownloadTask task) {

                }
            });
            baseDownloadTask.setAutoRetryTimes(5);
            baseDownloadTask.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被用户同意，可以去放肆了。
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                } else {
                    downloadApk(appUrl);
                }
            } else {
                // 权限被用户拒绝了，洗洗睡吧。
            }
        }
    }

    /**
     * 安装
     */
    private void install(File filePath, Context context) {
        if (filePath.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Log.d("UpdateDialog", "install: " + filePath);
                Uri contentUri = FileProvider.getUriForFile(context, mActivity.getPackageName() + ".fileprovider", filePath);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(filePath), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } else {
            downloadApk(appUrl);
        }
    }

    //    private void setBackgroundAlpha(Activity context, float alpha) {
////        Window window = this.getWindow();
////        WindowManager.LayoutParams wl = window.getAttributes();
////        wl.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
////        wl.alpha = alpha;//这句就是设置窗口里崆件的透明度的．０.０全透明．１.０不透明．
////        window.setAttributes(wl);
//        Window window = context.getWindow();
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        layoutParams.alpha = alpha;
//        window.setAttributes(layoutParams);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//    }

}
