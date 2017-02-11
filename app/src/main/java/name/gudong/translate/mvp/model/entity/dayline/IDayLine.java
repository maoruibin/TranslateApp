package name.gudong.translate.mvp.model.entity.dayline;

import java.util.Date;

/**
 * Created by GuDong on 10/7/16 12:59.
 * Contact with gudong.name@gmail.com.
 */

public interface IDayLine {
    String tts();
    String content();
    String note();
    String imageThumb();
    String imageHigh();

    /**
     * 来源
     * @return
     */
    String caption();

    /**
     * 日期
     * @return
     */
    Date date();
    String shareImage();
}
