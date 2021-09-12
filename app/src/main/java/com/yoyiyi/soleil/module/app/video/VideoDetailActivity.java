package com.yoyiyi.soleil.module.app.video;

import android.content.Intent;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yoyiyi.soleil.R;
import com.yoyiyi.soleil.bean.app.video.VideoDetail;
import com.yoyiyi.soleil.bean.app.video.VideoDetailComment;
import com.yoyiyi.soleil.event.Event;
import com.yoyiyi.soleil.module.region.BaseRegionActivity;
import com.yoyiyi.soleil.mvp.contract.app.video.VideoDetailContract;
import com.yoyiyi.soleil.mvp.presenter.app.video.VideoDetailPresenter;
import com.yoyiyi.soleil.rx.RxBus;
import com.yoyiyi.soleil.widget.statusbar.StatusBarUtil;

import butterknife.BindView;
import io.reactivex.annotations.Nullable;

/**
 * @author zzq  作者 E-mail:   soleilyoyiyi@gmail.com
 * @date 创建时间：2017/6/14 14:28
 * 描述:视频播放界面
 */

public class VideoDetailActivity extends BaseRegionActivity<VideoDetailPresenter, Nullable> implements VideoDetailContract.View {


    @BindView(R.id.iv_video_preview)
    ImageView mIvVideoPreview;
    @BindView(R.id.tv_av)
    TextView mTvAv;
    @BindView(R.id.tv_player)
    TextView mTvPlayer;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.main_content)
    CoordinatorLayout mMainContent;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    private VideoDetail.DataBean mVideoDetail;
    private VideoDetailComment.DataBean mVideoDetailComment;

    private CollapsingToolbarLayoutState state;


    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_detail1;
    }


    @Override
    public void showVideoDetail(VideoDetail.DataBean videoDetail) {
        mVideoDetail = videoDetail;

    }


    @Override
    public void showVideoDetailComment(VideoDetailComment.DataBean videoDetailComment) {
        mVideoDetailComment = videoDetailComment;
        finishTask();
    }

    @Override
    protected void loadData() {
        mPresenter.getVideoDetailData();
    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }

    @Override
    protected void finishTask() {
        //设置图片
        Glide.with(mContext)
                .load(mVideoDetail.pic)
                .centerCrop()
                .placeholder(R.drawable.bili_default_image_tv)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(mIvVideoPreview);
        super.finishTask();
        initEvent();
    }

    @Override
    protected void initEvent() {
        Event.VideoDetailEvent videoDetailEvent = new Event.VideoDetailEvent();
        videoDetailEvent.videoDetail = mVideoDetail;

        Event.VideoDetailCommentEvent videoDetailCommentEvent = new Event.VideoDetailCommentEvent();
        videoDetailCommentEvent.videoDetailComment = mVideoDetailComment;

        RxBus.INSTANCE.post(videoDetailEvent);
        RxBus.INSTANCE.post(videoDetailCommentEvent);
    }


    @Override
    protected void initTitle() {
        mTitles.add("简介");
        mTitles.add("评论(" + mVideoDetailComment.page.acount + ")");

    }

    @Override
    protected void initFragment() {
        mFragments.add(SummaryFragment.newInstance());
        mFragments.add(CommentFragment.newInstance());

    }

    @Override
    protected void initViewPager() {
        super.initViewPager();
        setCurrentItem(0);
    }

    @Override
    protected void initToolbar() {
        //mToolbar.setNavigationIcon(R.drawable.ic_clip_back_white);
        super.initToolbar();
        mToolbar.setTitle("");
    }

    @Override
    protected void initStatusBar() {
        StatusBarUtil.setTranslucentForCoordinatorLayout(this, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


    /**
     * 设置FAB动画
     *
     * @param target
     */
    private void setViewsTranslation(int target) {
        mFab.setTranslationY(target);
        if (target == 0) {
            mFab.animate().scaleX(1f).scaleY(1f)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
            mFab.setClickable(true);
        } else if (target < 0) {
            mFab.animate().scaleX(0f).scaleY(0f)
                    .setInterpolator(new AccelerateInterpolator())
                    .start();
            mFab.setClickable(false);
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mFab.setOnClickListener(view -> startActivity(new Intent(this, VideoPlayerActivity.class)));
        // initFAB();
        visible(R.id.pw_loading);
        initAppBar();
    }

    private void initFAB() {
        // mFab.setTranslationY(-getResources().getDimension(R.dimen.dp32));
    }

    private void initAppBar() {
        mAppBar.addOnOffsetChangedListener(
                (appBarLayout, verticalOffset) -> setViewsTranslation(verticalOffset));
        mAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset == 0) {
                if (state != CollapsingToolbarLayoutState.EXPANDED) {
                    state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    mTvPlayer.setVisibility(View.GONE);
                    mTvAv.setVisibility(View.VISIBLE);
                }
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                    mTvPlayer.setVisibility(View.VISIBLE);
                    mTvAv.setVisibility(View.GONE);
                }
            } else {
                mTvPlayer.setVisibility(View.GONE);
                mTvAv.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void showError(String msg) {
        super.showError(msg);
        gone(R.id.pw_loading);

    }

    @Override
    public void complete() {
        super.complete();
        gone(R.id.pw_loading);
    }
}


