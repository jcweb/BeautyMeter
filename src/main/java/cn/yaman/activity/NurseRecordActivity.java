package cn.yaman.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityNurseRecordBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.LogUtils;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.adapter.FragmentAdapter;
import cn.yaman.entity.RecordEntity;
import cn.yaman.entity.RecordMonthEntity;
import cn.yaman.fragment.DialyFragment;
import cn.yaman.fragment.MonthFragment;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;
import cn.yaman.utils.PreferenceUtils;
import cn.yaman.utils.TimeUtils;

public class NurseRecordActivity extends BaseActivity<ActivityNurseRecordBinding> {
    private FragmentAdapter adapter;
    private DialyFragment dialyFragment;
    private MonthFragment monthFragment;
    private int year, month, day;
    private final static int MONTH_NEXT = 0x11;
    private final static int MONTH_PRE = 0x10;
    private String queryTime = "";
    private int queryType;
    long date;
    long curDay;
    @Override
    public int bindContentView() {
        return R.layout.activity_nurse_record;
    }

    @Override
    public void onProcessor() {
        dialyFragment = new DialyFragment();
        monthFragment = new MonthFragment();
        List<Fragment> list = new ArrayList<>();
        list.add(dialyFragment);
        list.add(monthFragment);
        String[] title = getResources().getStringArray(R.array.tab_list);
        adapter = new FragmentAdapter(this, getSupportFragmentManager(), title, list);
        getBinding().vpNurse.setAdapter(adapter);

        getBinding().vpNurse.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getBinding().rgRecord.check(R.id.rb_day);
                } else if (position == 1) {
                    getBinding().rgRecord.check(R.id.rb_month);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getBinding().rgRecord.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_day:
                        getBinding().vpNurse.setCurrentItem(0);
                        queryDayfromCurrentMonth(date);
                        break;
                    case R.id.rb_month:
                        getBinding().vpNurse.setCurrentItem(1);
                        getRecordByMonth(date);
                        break;
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        queryTime = year + "-" + month;
        getBinding().tvTime.setText(year + "年" + month + "月");

        if (getBinding().vpNurse.getCurrentItem() == 0) {
//            getDailyRecord(System.currentTimeMillis());
            queryDayfromCurrentMonth(System.currentTimeMillis());
        } else {
            getRecordByMonth(System.currentTimeMillis());
        }
    }

    public void monthSwitch(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_pre:
                queryTime = dealDateInfo(MONTH_PRE);
                queryType = MONTH_PRE;
                date = TimeUtils.strToDate(queryTime + "-" + day);
                if (getBinding().vpNurse.getCurrentItem() == 0) {
                    queryDayfromCurrentMonth(date);
                } else {
                    getRecordByMonth(date);
                }
                break;
            case R.id.iv_next:
                queryTime = dealDateInfo(MONTH_NEXT);
                queryType = MONTH_NEXT;
                date = TimeUtils.strToDate(queryTime + "-" + day);
                if (getBinding().vpNurse.getCurrentItem() == 0) {
//                    queryDayfromCurrentMonth(date);
                } else {
                    getRecordByMonth(date);
                }
                break;
            default:
        }
    }

    /*时间逻辑处理*/
    private String dealDateInfo(int type) {
        String queryTime = year + "-" + month + "-" + day;
        if (type == MONTH_PRE) {
            int queryMonth = month == 1 ? 12 : month - 1;
            int querYear = month == 1 ? year - 1 : year;
            queryTime = querYear + "-" + queryMonth + "-" + day;
        } else if (type == MONTH_NEXT) {
            int queryMonth = month == 12 ? 1 : month + 1;
            int querYear = month == 12 ? year + 1 : year;
            queryTime = querYear + "-" + queryMonth + "-" + day;
        }
        return queryTime;
    }

    /*按月获取数据*/
    private void getRecordByMonth(long time) {
        HttpParams params = new HttpParams();
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        params.put("deviceId", "1");
        params.put("happenTimeLong", time);
        HttpUtils.newRequester().post(HttpUrl.QUERY_RECORD_MONTH, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    LogUtils.e(response.getData());
                    String[] date = queryTime.split("-");
                    year = Integer.parseInt(date[0]);
                    month = Integer.parseInt(date[1]);
                    getBinding().tvTime.setText(year + "年" + month + "月");
                    RecordMonthEntity entity = JsonUtils.getParam(response.getData(), RecordMonthEntity.class);
                    monthFragment.initMain(entity);
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    /*按日获取数据*/
    private void getDailyRecord(long time) {
        HttpParams params = new HttpParams();
        params.put("deviceId", "1");
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
//        params.put("number", 3);
        params.put("happenTimeLong", time);
        HttpUtils.newRequester().post(HttpUrl.RECORD_LIST, params, new YamanHttpCallback(this) {
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
                        dialyFragment.autoRecentlyRecord(list);
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

    //获取当前月份有数据的日期
    private void queryDayfromCurrentMonth(long time) {
        date=time;
        HttpParams params = new HttpParams();
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        params.put("happenTimeLong", System.currentTimeMillis());
        HttpUtils.newRequester().post(HttpUrl.RECORD_DATE, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    getDailyRecord(time);
                    if (!TextUtils.isEmpty(response.getData())) {
                        String content = response.getData().substring(1, response.getData().length() - 1);
                        String[] list = content.split(",");
                        String[] date = queryTime.split("-");
                        year = Integer.parseInt(date[0]);
                        month = Integer.parseInt(date[1]);
                        getBinding().tvTime.setText(year + "年" + month + "月");
                        dialyFragment.updateCalendar(list, queryType, year, month);
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
}
