package cn.yaman.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.erea.tool.CharacterParserUtil;
import com.erea.tool.CountryComparator;
import com.erea.tool.CountrySortAdapter;
import com.erea.tool.CountrySortModel;
import com.erea.tool.GetCountryNameSort;
import com.erea.tool.SideBar;
import com.erea.tool.SideBar.OnTouchingLetterChangedListener;
import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityAreaSelectBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.CountryEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;

public class AreaSelectActivity extends BaseActivity<ActivityAreaSelectBinding> implements OnTouchingLetterChangedListener, OnItemClickListener {

    String TAG = "CountryActivity";

    private List<CountrySortModel> mAllCountryList;

    private ListView country_lv_countryList;

    private CountrySortAdapter adapter;

    private SideBar sideBar;

    private CountryComparator pinyinComparator;

    private GetCountryNameSort countryChangeUtil;

    private CharacterParserUtil characterParserUtil;

    @Override
    public int bindContentView() {
        return R.layout.activity_area_select;
    }

    @Override
    public void onProcessor() {
        initView();
        getCountryList();
        initTitlebar("手机号归宿地");
    }

    private void initTitlebar(String title) {
        setStatusbarMode(true);
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(title);
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }

    /**
     * 初始化界面
     */
    private void initView() {
        country_lv_countryList = getBinding().countryLvList;

        sideBar = getBinding().countrySidebar;
        sideBar.setOnTouchingLetterChangedListener(this);
        country_lv_countryList.setOnItemClickListener(this);
        mAllCountryList = new ArrayList<CountrySortModel>();
        pinyinComparator = new CountryComparator();
        countryChangeUtil = new GetCountryNameSort();
        characterParserUtil = new CharacterParserUtil();

        // 将联系人进行排序，按照A~Z的顺序
        Collections.sort(mAllCountryList, pinyinComparator);
        adapter = new CountrySortAdapter(this, mAllCountryList);
        country_lv_countryList.setAdapter(adapter);

    }


    /*
     * 通过接口获取国家列表
     * */

    private void getCountryList() {
        HttpUtils.newRequester(true).post(HttpUrl.COUNTRY_CODE, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    List<CountryEntity> countryList = JsonUtils.getParamList(response.getData(), CountryEntity.class);
                    if (countryList != null && countryList.size() > 0) {
                        setCountryList(countryList);
                    }
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    /**
     * 获取国家列表
     */
    private void setCountryList(List<CountryEntity> list) {
        String[] countryList = getResources().getStringArray(R.array.country_code_list_ch);

        for (int i = 0, length = list.size(); i < length; i++) {
            CountryEntity entity = list.get(i);

            String countryName = entity.getCountry();
            String countryNumber = entity.getMobilePrefix();
            String countrySortKey = characterParserUtil.getSelling(countryName);
            CountrySortModel countrySortModel = new CountrySortModel(countryName, countryNumber,
                    countrySortKey);
            String sortLetter = countryChangeUtil.getSortLetterBySortKey(countrySortKey);
            if (sortLetter == null) {
                sortLetter = countryChangeUtil.getSortLetterBySortKey(countryName);
            }

            countrySortModel.sortLetters = sortLetter;
            mAllCountryList.add(countrySortModel);
        }

        Collections.sort(mAllCountryList, pinyinComparator);
        adapter.updateListView(mAllCountryList);
        Log.e(TAG, "changdu" + mAllCountryList.size());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String countryName = null;
        String countryNumber = null;

        // 点击后返回
        countryName = mAllCountryList.get(position).countryName;
        countryNumber = mAllCountryList.get(position).countryNumber;

        Intent intent = new Intent();
        intent.putExtra("countryCode", "+" + countryNumber);
        setResult(RESULT_OK, intent);
        Log.e(TAG, "countryName: + " + countryName + "countryNumber: " + countryNumber);
        finish();
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = adapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            country_lv_countryList.setSelection(position);
        }
    }
}
