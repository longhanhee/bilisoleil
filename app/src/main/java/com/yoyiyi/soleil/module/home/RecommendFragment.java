package com.yoyiyi.soleil.module.home;

import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import android.widget.ImageView;

import com.annimon.stream.Stream;
import com.yoyiyi.soleil.R;
import com.yoyiyi.soleil.adapter.home.RecommendAdapter;
import com.yoyiyi.soleil.base.BaseRefreshFragment;
import com.yoyiyi.soleil.bean.recommend.MulRecommend;
import com.yoyiyi.soleil.bean.recommend.Recommend;
import com.yoyiyi.soleil.module.recommend.AllStationRankActivity;
import com.yoyiyi.soleil.mvp.contract.home.RecommendContract;
import com.yoyiyi.soleil.mvp.presenter.home.RecommendPresenter;
import com.yoyiyi.soleil.utils.AppUtils;
import com.yoyiyi.soleil.utils.EmptyUtils;
import com.yoyiyi.soleil.widget.divider.VerticalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;

/**
 * @author zzq  作者 E-mail:   soleilyoyiyi@gmail.com
 * @date 创建时间：2017/5/23 14:23
 * 描述:推荐
 */

public class RecommendFragment extends BaseRefreshFragment<RecommendPresenter, MulRecommend>
        implements RecommendContract.View {

    @BindView(R.id.iv_rank)
    ImageView mIvRank;

    private RecommendAdapter mAdapter;

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_recommend;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected void lazyLoadData() {
        mPresenter.getRecommendData();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mIvRank.setOnClickListener(view-> startActivity(new Intent(getActivity(), AllStationRankActivity.class)));
    }

    @Override
    protected void initRecyclerView() {
        mAdapter = new RecommendAdapter(mList);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mAdapter.setSpanSizeLookup((gridLayoutManager, i) -> mList.get(i).spanSize);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
        //添加分割条
        VerticalDividerItemDecoration build = new VerticalDividerItemDecoration.Builder(getActivity())
                .color(AppUtils.getColor(R.color.transparent))
                // .color(AppUtils.getColor(R.color.colorPrimary))
                .sizeResId(R.dimen.dp10)
                .showLastDivider()
                .build();
        mRecycler.addItemDecoration(build);
    }

    @Override
    public void showRecommend(List<Recommend> recommend) {
        Stream.of(recommend)
                .forEach(recommendBean -> {
                    if (EmptyUtils.isNotEmpty(recommendBean.banner_item)) {
                        mList.add(new MulRecommend(MulRecommend.TYPR_HEADER, MulRecommend.HEADER_SPAN_SIZE, recommendBean.banner_item));
                    } else {
                        mList.add(new MulRecommend(MulRecommend.TYPE_ITEM, MulRecommend.ITEM_SPAN_SIZE, recommendBean));
                    }
                });
        finishTask();
    }

    @Override
    protected void finishTask() {
        mAdapter.notifyDataSetChanged();
    }

}
