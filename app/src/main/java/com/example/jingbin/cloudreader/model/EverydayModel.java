package com.example.jingbin.cloudreader.model;

import com.example.jingbin.cloudreader.bean.AndroidBean;
import com.example.jingbin.cloudreader.bean.FrontpageBean;
import com.example.jingbin.cloudreader.bean.GankIoDayBean;
import com.example.jingbin.cloudreader.http.HttpUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jingbin on 2016/12/1.
 * 每日推荐model
 */

public class EverydayModel {

    private String year = "2016";
    private String month = "11";
    private String day = "24";

    public EverydayModel(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public interface HomeImpl {
        void loadSuccess(Object object);

        void loadFailed();

        void addSubscription(Subscription subscription);
    }

    /**
     * 轮播图
     */
    public void showBanncerPage(final HomeImpl listener) {
        Subscription subscription = HttpUtils.getInstance().getDongTingServer().getFrontpage()
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<FrontpageBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.loadFailed();
                    }

                    @Override
                    public void onNext(FrontpageBean frontpageBean) {
                        listener.loadSuccess(frontpageBean);
                    }
                });
        listener.addSubscription(subscription);
    }

    /**
     * 显示RecyclerView数据
     */
    public void showRecyclerViewData(final HomeImpl listener) {
        Func1<GankIoDayBean, Observable<List<List<AndroidBean>>>> func1 = new Func1<GankIoDayBean, Observable<List<List<AndroidBean>>>>() {
            @Override
            public Observable<List<List<AndroidBean>>> call(GankIoDayBean gankIoDayBean) {

                List<List<AndroidBean>> lists = new ArrayList<>();

                GankIoDayBean.ResultsBean results = gankIoDayBean.getResults();

                // Android
                if (results.getAndroid() != null && results.getAndroid().size() > 0) {
                    lists.add(results.getAndroid());
                } else {
                    lists.add(new ArrayList<AndroidBean>());
                }
                // 福利
                if (results.getWelfare() != null && results.getWelfare().size() > 0) {
                    lists.add(results.getWelfare());
                } else {
                    lists.add(new ArrayList<AndroidBean>());
                }
                // ios
                if (results.getiOS() != null && results.getiOS().size() > 0) {
                    lists.add(results.getiOS());
                } else {
                    lists.add(new ArrayList<AndroidBean>());
                }
                // 拓展资源
//                if (results.getResource() != null && results.getResource().size() > 0) {
//                    lists.add(results.getResource());
//                } else {
//                    lists.add(new ArrayList<AndroidBean>());
//                }
                // 瞎推荐
//                if (results.getRecommend() != null && results.getRecommend().size() > 0) {
//                    lists.add(results.getRecommend());
//                } else {
//                    lists.add(new ArrayList<AndroidBean>());
//                }
                // 休息视频
//                if (results.getRestMovie() != null && results.getRestMovie().size() > 0) {
//                    lists.add(results.getRestMovie());
//                } else {
//                    lists.add(new ArrayList<AndroidBean>());
//                }
                // 前端
//                if (results.getFront() != null && results.getFront().size() > 0) {
//                    lists.add(results.getFront());
//                } else {
//                    lists.add(new ArrayList<AndroidBean>());
//                }
                // App
//                if (results.getApp() != null && results.getApp().size() > 0) {
//                    lists.add(results.getApp());
//                } else {
//                    lists.add(new ArrayList<AndroidBean>());
//                }
                return Observable.just(lists);
            }
        };

        Observer<List<List<AndroidBean>>> observer = new Observer<List<List<AndroidBean>>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                listener.loadFailed();
            }

            @Override
            public void onNext(List<List<AndroidBean>> lists) {
                listener.loadSuccess(lists);
            }
        };

        Subscription subscription = HttpUtils.getInstance().getGankIOServer().getGankIoDay("2016", "11", "24")
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .flatMap(func1)
                .subscribe(observer);
        listener.addSubscription(subscription);
    }
}
