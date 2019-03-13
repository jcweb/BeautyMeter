package cn.yaman;

import com.tcl.smart.beauty.BuildConfig;

/**
 * @author timpkins
 */
public final class Constants {

    public static final class Language {
        public static final String ZH = "zh"; // 中文
        public static final String EN = "en"; // 英文
    }

    public static final class Preferences {
        public static final String APP_LANGUAGE = "appLanguage";
    }

    public static final class HttpUrl {
        public static final String USER_LOGIN = BuildConfig.BASE_URL + "/user/login"; // 用户登录
        public static final String SEND_CODE = BuildConfig.BASE_URL + "/user/generateCaptcha"; // 获取验证码
        public static final String USER_MODIFY = BuildConfig.BASE_URL + "/user/modifyUser"; // 修改个人信息
        public static final String USE_HELP = BuildConfig.BASE_URL + "/usinghelp/select"; // 使用帮助
        public static final String ABOUT_US = BuildConfig.BASE_URL + "/abouts/select"; // 关于我们
        public static final String MODIFY_PW = BuildConfig.BASE_URL + "/user/modifyPassword"; // 修改密码
        public static final String FORGET_PW = BuildConfig.BASE_URL + "/user/forgetPassword"; // 忘记密码
        public static final String MODIFY_PHONE = BuildConfig.BASE_URL + "/user/modifyPhone"; // 修改手机号
        public static final String FEED_BACK = BuildConfig.BASE_URL + "/feedback/add"; // 修改手机号
        public static final String USER_PROTOCOL = BuildConfig.BASE_URL + "/protocol/select"; // 用户协议和隐私声明
        public static final String APP_UPDATE = BuildConfig.BASE_URL + "/appPackage/update"; // 版本升级
        public static final String COUNTRY_CODE = BuildConfig.BASE_URL + "/countryCode/getList"; // 获取国家码
        public static final String USER_REGIST = BuildConfig.BASE_URL + "/user/register"; // 用户注册
        public static final String REGIST_CHECK = BuildConfig.BASE_URL + "/user/verifycaptcha"; // 验证码校验
        public static final String IMAGE_UPLOAD = BuildConfig.BASE_URL + "/file/imageUpload"; // 上传用户头像

        /*护理相关*/
        public static final String SCHEME_LIST = BuildConfig.BASE_URL + "/scheme/getFaceList"; //获取方案列表
        public static final String SCHEME_DETAIL_LIST = BuildConfig.BASE_URL + "/operation/getListBySchemeId"; //获取方案详情列表
        public static final String RECORD_LIST = BuildConfig.BASE_URL + "/history/selectListBy"; //获取护理记录
        public static final String RECORD_DETAIL = BuildConfig.BASE_URL + "/history/getDetail";//获取记录详情
        public static final String RECORD_DATE = BuildConfig.BASE_URL + "/history/selectDayList";//获取某月有护理记录的日期字符串
        public static final String RECORD_ADD = BuildConfig.BASE_URL + "/history/add";//添加护理记录
        public static final String QUERY_RECORD_MONTH = BuildConfig.BASE_URL + "/history/selectMonthRecord";//按月查询记录

        /*设备相关*/
        public static final String DEVICE_LIST = BuildConfig.BASE_URL + "/product/getList"; //获取设备列表
        public static final String DEVICE_ADD = BuildConfig.BASE_URL + "/device/add"; //新增设备
        public static final String DEVICE_BIND_LIST = BuildConfig.BASE_URL + "/device/getBindList"; //获取绑定的设备列表
        public static final String DEVICE_DETAIL = BuildConfig.BASE_URL + "/device/getDetail"; //获取设备详情
        public static final String DEVICE_UNBIND = BuildConfig.BASE_URL + "/device/unBind"; //解除绑定
        public static final String DEVICE_RECENTLY = BuildConfig.BASE_URL + "/history/selectNewestOne"; //获取最近的一条操作设备信息
    }

    public static final class QueryType{
        public final static int MONTH_NEXT = 0x11;
        public final static int MONTH_PRE = 0x10;
    }
}
