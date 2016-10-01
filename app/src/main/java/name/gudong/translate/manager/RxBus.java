package name.gudong.translate.manager;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by GuDong on 1/20/16 17:54.
 * Contact with 1252768410@qq.com.
 */
public class RxBus<T, R> {
    private final Subject<T, R> rxBus;

    private RxBus() {
        rxBus = new SerializedSubject(PublishSubject.<T>create());
    }

    private static class SingletonHolder {
        private static RxBus instance = new RxBus();
    }

    public static RxBus getInstance() {
        return SingletonHolder.instance;
    }

    public void send(T msg) {
        rxBus.onNext(msg);
    }

    public Observable<R> toObservable() {
        return rxBus.asObservable().onBackpressureBuffer();
    }

    /**
     * check the observers has subscribe or not DeadEvent https://github.com/square/otto/blob/master/otto/src/main/java/com/squareup/otto/DeadEvent.java
     *
     * @return
     */
    public boolean hasObservers() {
        return rxBus.hasObservers();
    }
}
