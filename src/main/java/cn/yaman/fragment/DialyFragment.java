package cn.yaman.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.othershe.calendarview.bean.DateBean;
import com.othershe.calendarview.listener.OnSingleChooseListener;
import com.othershe.calendarview.utils.CalendarUtil;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.FragmentDialyBinding;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.fragment.BaseFragment;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.Constants.QueryType;
import cn.yaman.activity.RecordDetailActivity;
import cn.yaman.entity.RecordEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.PreferenceUtils;
import cn.yaman.utils.TimeUtils;

public class DialyFragment extends BaseFragment<FragmentDialyBinding> implements OnSingleChooseListener {
    private int[] cDate = CalendarUtil.getCurrentDate();
    private static final String DATE_FORMAT = "MM/dd hh:mm";

    @Override
    public int bindContentView() {
        return R.layout.fragment_dialy;
    }

    @Override
    public void onProcessor() {
        getBinding().cvFragmentDialy
                .setStartEndDate("2016.1", "2028.12")
                .setDisableStartEndDate("2016.10.10", "2028.10.10")
                .setInitDate(cDate[0] + "." + cDate[1])
                .setSingleDate(cDate[0] + "." + cDate[1] + "." + cDate[2])
                .init();
        getBinding().cvFragmentDialy.setOnSingleChooseListener(this);

    }

    public void updateCalendar(String[] list, int type, int year, int month) {
        if (type == QueryType.MONTH_NEXT) {
            getBinding().cvFragmentDialy.nextMonth();
        } else if (type == QueryType.MONTH_PRE) {
            getBinding().cvFragmentDialy.lastMonth();
        }
        HashMap<String, String> map = new HashMap<>();

        for (int i = 0; i < list.length; i++) {
            String str = list[i];
            if (str.startsWith("0")) {
                str = str.substring(1);
            }
            map.put(year + "." + month + "." + str, "enable");
        }
        getBinding().cvFragmentDialy.setSpecifyMap(map)
                .setSingleDate(year + "." + month + "." + list[0])
                .setStartEndDate("2016.1", "2028.12")
                .setDisableStartEndDate("2016.10.10", "2028.10.10")
                .setInitDate(year + "." + month)
                .init();
    }

    /*加载最近的三条护肤记录*/
    public void autoRecentlyRecord(List<RecordEntity> list) {
        getBinding().llContainerItem.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            RecordEntity entity = list.get(i);
            /*动态加载护肤记录*/
            View view = LayoutInflater.from(getContext()).inflate(R.layout.record_list_item, null);

            View line = LayoutInflater.from(getContext()).inflate(R.layout.include_horiz_divide_green, null);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
            line.setLayoutParams(params);

            ImageView iv = view.findViewById(R.id.iv_record_item);
            TextView tvName = view.findViewById(R.id.tv_record_item_name);
            TextView tvDate = view.findViewById(R.id.tv_record_item_date);
            TextView tvTime = view.findViewById(R.id.tv_record_item_time);
            Glide.with(this).load(entity.getImageUrl()).into(iv);
            tvName.setText(entity.getSname());
            tvDate.setText(TimeUtils.dateToStr(DATE_FORMAT, entity.getHappenTime()));
            tvTime.setText(TimeUtils.secondToStr(entity.getSecond()));
            getBinding().llContainerItem.addView(view);
            if (i < list.size() - 1) {
                getBinding().llContainerItem.addView(line);
            }
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), RecordDetailActivity.class);
                    intent.putExtra("recordId", entity.getId() + "");
                    startActivity(intent);
                }
            });
        }

    }

    /*按日获取数据*/
    private void getDailyRecord(long time) {
        HttpParams params = new HttpParams();
        params.put("deviceId", "1");
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
//        params.put("number", 3);
        params.put("happenTimeLong", time);
        HttpUtils.newRequester().post(HttpUrl.RECORD_LIST, params, new YamanHttpCallback() {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    List<RecordEntity> list = JsonUtils.getParamList(response.getData(), RecordEntity.class);
                    if (list != null) {
                        autoRecentlyRecord(list);
                    }
                }
            }

            @Override
            public void onFailure(String url, String statusCode) {
                super.onFailure(url, statusCode);
                ToastUtils.toastShort(statusCode + "");
            }
        });
    }

    @Override
    public void onSingleChoose(View view, DateBean date) {
        String dateStr = date.getSolar()[0] + "-" + date.getSolar()[1] + "-" + date.getSolar()[2];
        long time = TimeUtils.strToDate(dateStr);
        getDailyRecord(time);
    }
}
