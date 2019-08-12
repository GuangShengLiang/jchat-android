package jiguang.chat.activity.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import jiguang.chat.R;
import jiguang.chat.view.my.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class MeFragment extends BaseFragment implements StickyNavLayout.OnStickStateChangeListener, OnBannerListener {
    private Banner banner;
    private View mRootView;
    private RecyclerView mIndicatorView;
    private RecyclerView mContentView;

    private SwipeRefreshLayout swipeLayout;
    private StickyNavLayout stickyNavLayout;
    private RelativeLayout snlIindicator;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        mRootView = layoutInflater.inflate(R.layout.activity_main,
                (ViewGroup) getActivity().findViewById(R.id.main_view), false);
//        mMeView = (MeView) mRootView.findViewById(R.id.me_view);
//        mMeView.initModule(mDensity, mWidth);
//        mMeController = new MeController(this, mWidth);
//        mMeView.setListener(mMeController);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mContext = MyActivity.this;
        banner = mRootView.findViewById(R.id.banner);
        mIndicatorView = mRootView.findViewById(R.id.rv_indicator);
        mContentView = mRootView.findViewById(R.id.rv_content);
        stickyNavLayout = mRootView.findViewById(R.id.stickyNavLayout);
        swipeLayout = mRootView.findViewById(R.id.swipeLayout);
        snlIindicator = mRootView.findViewById(R.id.snlIindicator);
        setBannerData();
        setIndicatorData();
        setContentData();
    }

    /**
     * 设置tab导航栏数据
     */
    private void setIndicatorData() {
        LinearLayoutManager xLinearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mIndicatorView.setLayoutManager(xLinearLayoutManager);
        RecyclerViewDivider divder = new RecyclerViewDivider(false);
        divder.setColor(mContext.getResources().getColor(R.color.trans));
        divder.setDividerHeight(getTabDividerWidth());
        mIndicatorView.addItemDecoration(divder);
        List<String> mUrls = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525409191567&di=5dc20cf2404b99e43fa50678d3757776&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fexp%2Fw%3D500%2Fsign%3D7645a3a60bd162d985ee621c21dea950%2F8644ebf81a4c510fadb40f786159252dd42aa564.jpg");
        }
        HomeIndicatorAdapter adapter = new HomeIndicatorAdapter(mUrls, mContext);
        mIndicatorView.setAdapter(adapter);
    }

    /**
     * 设置tab导航栏数据
     */
    private void setContentData() {
        LinearLayoutManager xLinearLayoutManager = new LinearLayoutManager(mContext);
        mContentView.setLayoutManager(xLinearLayoutManager);
        RecyclerViewDivider divder = new RecyclerViewDivider(true);
        divder.setColor(mContext.getResources().getColor(R.color.colorAccent));
        divder.setDividerHeight(1);
        mContentView.addItemDecoration(divder);
        stickyNavLayout.setOnStickStateChangeListener(this);
        List<String> mContents = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mContents.add("浓眉：我只需要上场好好打球 给不给哨是裁判的事");
        }
        HomeContentAdapter adapter = new HomeContentAdapter(mContents, mContext);
        mContentView.setAdapter(adapter);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mContentView.getAdapter().notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 获取tab间的间隙
     */
    private int getTabDividerWidth() {
        int totleWidth = getScreenWidth(mContext);
        int tabWidth = dip2px(mContext, 34);
        int leftPadding = dip2px(mContext, 30);
        return (totleWidth - leftPadding * 2 - tabWidth * 5) / 4;
    }

    public void isStick(boolean isStick) {

    }

    public void scrollPercent(float percent) {
        snlIindicator.setBackgroundColor(Color.parseColor((String) CommonUtil.getInstance().evaluate(percent, "#e18e36", "#3F51B5")));
        if (percent == 0) {
            swipeLayout.setEnabled(true);
            swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mContentView.getAdapter().notifyDataSetChanged();
                    swipeLayout.setRefreshing(false);
                }
            });
        } else {
            swipeLayout.setEnabled(false);
            swipeLayout.setOnRefreshListener(null);
        }
    }

    /**
     * 设置Banner数据
     */
    private void setBannerData() {
        List<String> urls = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525406238449&di=3847cf21b523ff27772e4d952a3530f5&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2F34fae6cd7b899e51fab3e9c048a7d933c8950d21.jpg");
        }
        banner.setImages(urls).setOnBannerListener(this).setImageLoader(new GlideImageLoader()).start();
    }


    /*
   获取屏幕宽度和高度
    */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public void OnBannerClick(int position) {

    }

//    private View mRootView;
//    public MeView mMeView;
//    private MeController mMeController;
//    private Context mContext;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext = this.getActivity();
//        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
//        mRootView = layoutInflater.inflate(R.layout.fragment_me,
//                (ViewGroup) getActivity().findViewById(R.id.main_view), false);
//        mMeView = (MeView) mRootView.findViewById(R.id.me_view);
//        mMeView.initModule(mDensity, mWidth);
//        mMeController = new MeController(this, mWidth);
//        mMeView.setListener(mMeController);
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        ViewGroup p = (ViewGroup) mRootView.getParent();
//        if (p != null) {
//            p.removeAllViewsInLayout();
//        }
//        return mRootView;
//    }
//
//    @Override
//    public void onResume() {
//        UserInfo myInfo = JMessageClient.getMyInfo();
//        myInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
//            @Override
//            public void gotResult(int i, String s, Bitmap bitmap) {
//                if (i == 0) {
//                    mMeView.showPhoto(bitmap);
//                    mMeController.setBitmap(bitmap);
//                }else {
//                    mMeView.showPhoto(null);
//                    mMeController.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rc_default_portrait));
//                }
//            }
//        });
//        mMeView.showNickName(myInfo);
//        super.onResume();
//    }
//
//    public void cancelNotification() {
//        NotificationManager manager = (NotificationManager) this.getActivity().getApplicationContext()
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancelAll();
//    }
//
//    //退出登录
//    public void Logout() {
//        final Intent intent = new Intent();
//        UserInfo info = JMessageClient.getMyInfo();
//        if (null != info) {
//            SharePreferenceManager.setCachedUsername(info.getUserName());
//            if (info.getAvatarFile() != null) {
//                SharePreferenceManager.setCachedAvatarPath(info.getAvatarFile().getAbsolutePath());
//            }
//            JMessageClient.logout();
//            intent.setClass(mContext, LoginActivity.class);
//            startActivity(intent);
//        } else {
//            ToastUtil.shortToast(mContext, "退出失败");
//        }
//    }

}
