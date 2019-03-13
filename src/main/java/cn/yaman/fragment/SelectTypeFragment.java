package cn.yaman.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcl.smart.beauty.R;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

/**
 * 选择文件类型
 * @author timpkins
 */
public class SelectTypeFragment extends DialogFragment implements OnClickListener {
    protected static String KEY_NAME = "kname";
    private static final String TAG = SelectTypeFragment.class.getSimpleName();
    private static final String BASE_PATH = Environment.getExternalStorageDirectory() + File.separator + "/Documents/";
    public static final int TYPE_IMAGE = 0x01; // 图片
    public static final int TYPE_VOICE = 0x02; // 语音
    public static final int TYPE_VIDEO = 0x03; // 视频
    public static final int PATH_RECORD = 0x04; // 录制/拍摄
    public static final int PATH_ALBUM = 0x05; // 从相册选择
    private static final int REQUEST_RECORD = 0x01;
    private static final int REQUEST_ALBUM = 0x02;
    private FragmentDismiss fragmentDismiss;
    private int type = 0, path = 0;
    private LinearLayout llType, llPath;
    private Uri recordUri;
    private File shotFile = null;
    private OnFileSelected onFileSelected;
    public static final String KEY_TYPE = "ktype"; // 选择的多媒体类型

    public static SelectTypeFragment newInstance(String name) {
        SelectTypeFragment fragment = new SelectTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_NAME, name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        type = getArguments().getInt(KEY_TYPE, 0);
        if (type > 0) {
            showPath(true);
        }
    }

    public void setFragmentDismiss(FragmentDismiss fragmentDismiss) {
        this.fragmentDismiss = fragmentDismiss;
    }

    public void setOnFileSelected(OnFileSelected onFileSelected) {
        this.onFileSelected = onFileSelected;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_type, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llType = view.findViewById(R.id.llType);
        TextView tvImage = view.findViewById(R.id.tvImage);
        TextView tvVideo = view.findViewById(R.id.tvVideo);
        TextView tvVoice = view.findViewById(R.id.tvVoice);

        llPath = view.findViewById(R.id.llPath);
        TextView tvRecord = view.findViewById(R.id.tvRecord);
        TextView tvAlbum = view.findViewById(R.id.tvAlbum);

        showPath(false);

        tvImage.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
        tvVoice.setOnClickListener(this);
        tvRecord.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.tvImage) {
            type = TYPE_IMAGE;
            showPath(true);
        } else if (vid == R.id.tvVideo) {
            type = TYPE_VIDEO;
            showPath(true);
        } else if (vid == R.id.tvVoice) {
            type = TYPE_VOICE;
            showPath(true);
        } else if (vid == R.id.tvRecord) {
            record();
        } else if (vid == R.id.tvAlbum) {
            album();
        }

    }

    private void showPath(boolean isPath) {
        llPath.setVisibility(isPath ? View.VISIBLE : View.GONE);
        llType.setVisibility(isPath ? View.GONE : View.VISIBLE);
    }

    private void record() {
        if (type == TYPE_IMAGE) { // 拍摄图片
            recordUri = getFileName(TYPE_IMAGE);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, recordUri);
            startActivityForResult(intent, REQUEST_RECORD);
        } else if (type == TYPE_VIDEO) { // 拍摄视频
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            recordUri = getFileName(TYPE_VIDEO);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, recordUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, REQUEST_RECORD);
        } else if (type == TYPE_VOICE) { // 录制音频
            /*Intent intent = new Intent(Media.RECORD_SOUND_ACTION);
            recordUri = getFileName(TYPE_VOICE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, recordUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, REQUEST_RECORD);*/
        }
    }

    private void album() {
        if (type == TYPE_IMAGE) { // 选择图片
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_ALBUM);
        } else if (type == TYPE_VIDEO) { // 选择视频
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_ALBUM);
        } else if (type == TYPE_VOICE) { // 选择音频
           /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, REQUEST_ALBUM);*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String url = "";
            if (REQUEST_ALBUM == requestCode) {
                if (type == TYPE_VIDEO) {
                    url = getPath(data.getData(), Video.Media.EXTERNAL_CONTENT_URI);
                } else if (type == TYPE_IMAGE) {
                    url = getPath(data.getData(), Images.Media.EXTERNAL_CONTENT_URI);
                } else {
//                    url = getPath(data.getData(), Audio.Media.EXTERNAL_CONTENT_URI);
                }

                Log.e(TAG, "选择媒体文件 = " + url);
            } else if (REQUEST_RECORD == requestCode) {
//                url = recordUri.getPath();
                url = shotFile.getAbsolutePath();
                Log.e(TAG, "拍摄媒体文件 = " + url);
            }
            onFileSelected.onFileSelected(type, url);
            fragmentDismiss.onDismiss();
        }
    }

    public String getPath(Uri uri, Uri content_uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = getActivity().getContentResolver().query(content_uri, null, Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(Video.Media.DATA));
        cursor.close();

       /* String path = null;
                 String[] proj = { MediaStore.Images.Media.DATA };
                 Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
                 if(null!=cursor&&cursor.moveToFirst()){
                         int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                     path = cursor.getString(column_index);
                         cursor.close();
                     }*/


        return path;
    }

    public interface FragmentDismiss {
        void onDismiss();
    }

    public interface OnFileSelected {

        void onFileSelected(int type, String url);
    }

    private Uri getFileName(int type) {
        String suffix = "";
        if (type == TYPE_VOICE) {
            suffix = "amr";
        } else if (type == TYPE_VIDEO) {
            suffix = "mp4";
        } else if (type == TYPE_IMAGE) {
            suffix = "jpg";
        }
        shotFile = new File(BASE_PATH);
        if (!shotFile.exists()) {
            shotFile.mkdirs();
        }
        shotFile = new File(BASE_PATH, System.currentTimeMillis() + "." + suffix);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            recordUri = Uri.fromFile(shotFile);
        } else {
            //FileProvider为7.0新增应用间共享文件,在7.0上暴露文件路径会报FileUriExposedException
            //为了适配7.0,所以需要使用FileProvider,具体使用百度一下即可
            recordUri = FileProvider.getUriForFile(getContext(), "com.yiyuansafety.monitor.fileprovider", shotFile);//通过FileProvider创建一个content类型的Uri
        }
        /*try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return recordUri;
    }
}