package cn.yaman.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tcl.smart.beauty.R;

import androidx.fragment.app.Fragment;
import cn.yaman.entity.SchemeDetailEntity;

public class NurseProcessFrament extends Fragment implements View.OnClickListener{

    private ImageView img,playImg,pauseImg,NextImg;
    private TextView titleTv,partTv,stepTv,tipTv,timeTv,commitTv;

    private int time;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.item_nurse_process_vp,null);
        img=view.findViewById(R.id.item_nurse_process_vp_img);
        playImg=view.findViewById(R.id.item_nurse_process_vp_play);
        pauseImg=view.findViewById(R.id.item_nurse_process_vp_pause);
        NextImg=view.findViewById(R.id.item_nurse_process_vp_next);

        titleTv=view.findViewById(R.id.item_nurse_process_vp_title);
        partTv=view.findViewById(R.id.item_nurse_process_vp_part);
        stepTv=view.findViewById(R.id.item_nurse_process_vp_content);
        timeTv=view.findViewById(R.id.item_nurse_process_vp_time);
        tipTv=view.findViewById(R.id.item_nurse_process_vp_tip);
        commitTv=view.findViewById(R.id.item_nurse_process_vp_end);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
public void setData(SchemeDetailEntity entity){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with( getActivity()).asGif().load(entity.getGifUrl()).into(img);
            }
        });
}
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.item_nurse_process_vp_play:
                break;
            case R.id.item_nurse_process_vp_pause:
                break;
            case R.id.item_nurse_process_vp_next:
                break;
            case R.id.item_nurse_process_vp_end:
                break;
        }
    }

    private Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
