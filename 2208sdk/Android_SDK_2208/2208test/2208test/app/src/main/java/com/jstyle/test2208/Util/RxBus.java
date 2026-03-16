package com.jstyle.test2208.Util;


import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


/**
 * Created by zhuge on 2017/1/19 0019.
 */
public class RxBus {
    private static RxBus ourInstance = new RxBus();
    private final Subject<Object> bus;
    public static RxBus getInstance() {
        return ourInstance;
    }
    private final FlowableProcessor<Object> mBus;
    private RxBus() {
        mBus= PublishProcessor.create().toSerialized();
        bus=PublishSubject.create().toSerialized();
    }

    public void post(Object object){
        bus.onNext(object);
    }

    public <T> Observable<T> toObservable(Class<T> eventType){
        return bus.ofType(eventType);
    }
    public <T> Flowable<T> toFlowable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }
}
