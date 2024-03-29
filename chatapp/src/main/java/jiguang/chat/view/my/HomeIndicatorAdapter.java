package jiguang.chat.view.my;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import jiguang.chat.R;

import java.util.List;

//import com.hbandroid.sliderefreshdemo.R;
//import com.hbandroid.sliderefreshdemo.util.CommonUtil;
//import com.hbandroid.sliderefreshdemo.util.GlideImageLoader;
//import com.joooonho.SelectableRoundedImageView;

/**
 * Title:SlideRefreshDemo
 * <p>
 * Description:
 * <p>
 * <p>
 * Author: baigege(baigegechen@gmail.com)
 * <p>
 * Date：2018-05-04
 */

public class HomeIndicatorAdapter extends RecyclerView.Adapter<HomeIndicatorAdapter.ViewHolder> {

    private List<String> mList;

    private Context mContext;

    private LayoutInflater mInflater;

    public HomeIndicatorAdapter(List<String> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.activity_indicator_item, null, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        CommonUtil.LoadUrlImage(mContext, mList.get(position), holder.mImgView);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImgView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgView = itemView.findViewById(R.id.sriv_img);
        }
    }
}
