package name.gudong.translate.manager;

/**
 * Created by GuDong on 10/1/16 22:35.
 * Contact with gudong.name@gmail.com.
 */

public class Event<T>{
    T data;

    public Event() {}

    public Event(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
