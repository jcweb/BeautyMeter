package cn.yaman.activity.center;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityFeedbackBinding;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.Constants.HttpUrl;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.PreferenceUtils;
import cn.yaman.view.FlowLayout;
import cn.yaman.view.TagAdapter;
import cn.yaman.view.TagFlowLayout;
import cn.yaman.view.TagFlowLayout.OnTagClickListener;

public class FeedbackActivity extends BaseActivity<ActivityFeedbackBinding> {
    private TagFlowLayout tagFlowLayout;
    private String[] list;
    private int currentItem;
    @Override
    public int bindContentView() {
        return R.layout.activity_feedback;
    }

    @Override
    public void onProcessor() {
        getBinding().setUi(this);
        setStatusbarMode(true);
        initTitlebar();
        initView();
    }

    private void initView(){
        list = getResources().getStringArray(R.array.feedback_list_content);
        tagFlowLayout = findViewById(R.id.fl_feedback);
        tagFlowLayout.setAdapter(new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(FeedbackActivity.this).inflate(R.layout.item_feedback_text,
                        tagFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });
        tagFlowLayout.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                currentItem = position;
                return false;
            }
        });
    }

    public void commitContent(View view){
        HttpParams params = new HttpParams();
        String contact = getBinding().tdFeedbackIphone.getText().toString();
        if(!TextUtils.isEmpty(contact)){
            params.put("contact", contact);
        }
        params.put("opinion",getBinding().etFeedContent.getText().toString());
        params.put("type",currentItem);
        params.put("userId", PreferenceUtils.getInstance().getUserEntity().getUser().getId());
        HttpUtils.newRequester().post(HttpUrl.FEED_BACK, params, new YamanHttpCallback() {

            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    ToastUtils.toastShort("提交成功");
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });

    }

    private void initTitlebar() {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(getString(R.string.center_feedback_title));
        titlebar.setLeftIcon(v -> finish());
    }
}
