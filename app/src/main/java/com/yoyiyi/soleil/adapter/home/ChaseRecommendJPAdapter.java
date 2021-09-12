package com.yoyiyi.soleil.adapter.home;

import android.content.Intent;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yoyiyi.soleil.R;
import com.yoyiyi.soleil.bean.chase.RecommendBangumi;
import com.yoyiyi.soleil.module.bangumi.BangumiDetailActivity;
import com.yoyiyi.soleil.utils.NumberUtils;

import java.util.List;

/**
 * @author zzq  作者 E-mail:   soleilyoyiyi@gmail.com
 * @date 创建时间：2017/6/7 23:31
 * 描述:
 */
public class ChaseRecommendJPAdapter extends
        BaseQuickAdapter<RecommendBangumi.RecommendJpBean.RecommendBeanX,BaseViewHolder> {
    public ChaseRecommendJPAdapter(@Nullable List<RecommendBangumi.RecommendJpBean.RecommendBeanX> data) {
            super(R.layout.item_home_chase_body, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, RecommendBangumi.RecommendJpBean.RecommendBeanX recommendBean) {
        Glide.with(mContext)
                .load(recommendBean.cover)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into((ImageView) holder.getView(R.id.iv_video_preview));
        holder.setText(R.id.tv_video_follow, NumberUtils.format(recommendBean.favourites) + "人追番")
                .setText(R.id.tv_video_title, recommendBean.title)
                .setText(R.id.tv_video_update, "更新至第" + recommendBean.newest_ep_index + "话")
                .setVisible(R.id.tv_video_state, false);
        holder.itemView.setOnClickListener(view -> mContext.startActivity(new Intent(mContext, BangumiDetailActivity.class)));
    }
}
