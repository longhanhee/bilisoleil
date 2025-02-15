package com.yoyiyi.soleil.rx;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;


/**
 * @author zzq  作者 E-mail:   soleilyoyiyi@gmail.com
 * @date 创建时间：2017/4/7 15:50
 * 描述:RxBus类
 */
public enum RxBus {
    INSTANCE;

    private final FlowableProcessor<Object> bus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Flowable的数据发射给观察者
    RxBus() {
        bus = PublishProcessor.create().toSerialized();
    }

    // 提供了一个新的事件 发射数据
    public void post(Object o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Flowable<T> toFlowable(Class<T> eventType) {
        return bus.ofType(eventType);


    }

    //封装默认订阅
    public <T> Disposable toDefaultFlowable(Class<T> eventType, Consumer<T> act) {
        return bus.ofType(eventType).compose(RxUtils.<T>rxSchedulerHelper()).subscribe(act);
    }
}
