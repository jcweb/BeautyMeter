package cn.yaman.activity.center;

import com.tcl.smart.beauty.R;
import com.tcl.smart.beauty.databinding.ActivityHelpBinding;

import androidx.annotation.Nullable;
import cn.lamb.activity.BaseActivity;
import cn.lamb.base.util.ToastUtils;
import cn.lamb.http.HttpParams;
import cn.yaman.core.CommonTitlebarStrategy;
import cn.yaman.entity.HelpEntity;
import cn.yaman.http.HttpResponse;
import cn.yaman.http.HttpUtils;
import cn.yaman.http.YamanHttpCallback;
import cn.yaman.utils.JsonUtils;

public class HelpActivity extends BaseActivity<ActivityHelpBinding> {
    private String url;
    @Override
    public int bindContentView() {
        return R.layout.activity_help;
    }

    @Override
    public void onProcessor() {
        setStatusbarMode(true);
        String title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        initTitlebar(title);
        getHelpContent();
    }

    private void getHelpContent(){
        HttpParams params = new HttpParams();
        HttpUtils.newRequester().post(url, params, new YamanHttpCallback(this) {
            @Override
            public void onSuccess(String url, @Nullable Object o) {
                super.onSuccess(url, o);
                if (o == null) {
                    return;
                }
                HttpResponse response = (HttpResponse) o;
                if (response.getResultCode() == 0) {
                    HelpEntity helpEntity = JsonUtils.getParam(response.getData(),HelpEntity.class);
//                    getBinding().tvHelpContent.setText(helpEntity.getContent());
                    loadWebContent(helpEntity.getContent());
                } else {
                    ToastUtils.toastShort(response.getResultMsg());
                }
            }
        });
    }

    /*加载H5内容*/
    private void loadWebContent(String content){
        getBinding().wbHelp.loadDataWithBaseURL(null,content, "text/html", "utf-8", null);
    }

    private void initTitlebar(String title) {
        CommonTitlebarStrategy titlebar = new CommonTitlebarStrategy();
        setTitlebarStrategy(titlebar);
        titlebar.setTitleName(title);
        titlebar.setElevation(R.dimen.dp_02);
        titlebar.setLeftIcon(v -> finish());
    }
}
