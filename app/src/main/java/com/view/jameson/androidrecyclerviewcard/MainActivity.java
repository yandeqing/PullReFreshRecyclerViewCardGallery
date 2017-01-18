package com.view.jameson.androidrecyclerviewcard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.view.jameson.androidrecyclerviewcard.util.BlurBitmapUtils;
import com.view.jameson.androidrecyclerviewcard.util.ViewSwitchUtils;
import com.view.jameson.library.CardScaleHelper;

import java.util.ArrayList;
import java.util.List;

import static com.view.jameson.androidrecyclerviewcard.R.id.recyclerView;

public class MainActivity extends Activity {

    private PullToReshHorizontalRecycleView mRecyclerView;
    private ImageView mBlurView;
    private List<Integer> mList = new ArrayList<>();
    private CardScaleHelper mCardScaleHelper = null;
    private Runnable mBlurRunnable;
    private int mLastPos = -1;
    private CardAdapter adapter;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean debug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    int limit = 1;
    int start = 0;

    private void init() {
        for (int i = 0; i < 2; i++) {
            mList.add(R.drawable.pic4);
            mList.add(R.drawable.pic5);
            mList.add(R.drawable.pic6);
        }

        mRecyclerView = (PullToReshHorizontalRecycleView) findViewById(recyclerView);
        mRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
//        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CardAdapter(mList);
        mRecyclerView.setAdapter(adapter);
        // mRecyclerView绑定scale效果
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(1);
        mCardScaleHelper.attachToRecyclerView(mRecyclerView.getRefreshableView());

        initBlurBackground();
        final ILoadingLayout loadingLayoutProxy = mRecyclerView.getLoadingLayoutProxy();
        loadingLayoutProxy.setPullLabel("刷新中");
        mRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                Log.i(TAG, "【MainActivity.onPullDownToRefresh()】【refreshView=" + refreshView + "】");
                start = 0;
                start += limit;
                mList.clear();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                        mRecyclerView.setRefreshing(false);
                        mRecyclerView.onRefreshComplete();
                    }
                }, 3000);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                Log.i(TAG, "【MainActivity.onPullUpToRefresh()】【refreshView=" + refreshView + "】");
                start += limit;
//                loadData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                        mRecyclerView.setRefreshing(false);
                        mRecyclerView.onRefreshComplete();
                    }
                }, 3000);
            }
        });


    }

    private void loadData() {
        for (int i = start; i < limit + start; i++) {
            mList.add(R.drawable.pic4);
            mList.add(R.drawable.pic5);
            mList.add(R.drawable.pic6);
        }
        adapter.notifyDataSetChanged();
    }


    private void initBlurBackground() {
        mBlurView = (ImageView) findViewById(R.id.blurView);
        mRecyclerView.getRefreshableView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange();
                }
            }
        });

        notifyBackgroundChange();
    }

    private void notifyBackgroundChange() {
        if (mLastPos == mCardScaleHelper.getCurrentItemPos()) return;
        mLastPos = mCardScaleHelper.getCurrentItemPos();
        final int resId = mList.get(mCardScaleHelper.getCurrentItemPos());
        mBlurView.removeCallbacks(mBlurRunnable);
        mBlurRunnable = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
                ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), bitmap, 15));
            }
        };
        mBlurView.postDelayed(mBlurRunnable, 500);
    }

}
