package name.gudong.translate.mvp.model.entity.dayline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by GuDong on 10/7/16 12:57.
 * Contact with gudong.name@gmail.com.
 */

public class JinshanDayLineEntity implements IDayLine{

    /**
     * sid : 2363
     * tts : http://news.iciba.com/admin/tts/2016-10-07-day.mp3
     * content : Always let your conscience be your guide. —Pinocchio
     * note : 要凭着你的良心做事。 —《木偶奇遇记》
     * love : 936
     * translation : 词霸小编：这句话让小编想起前一阵的电信诈骗案，不管家庭如何困难，有手有脚就应该靠自己去打拼赚钱，而不是通过诈骗或其他不法手段获取利益哦！小伙伴们，你们是不是也跟小编一样想的呢~~
     * picture : http://cdn.iciba.com/news/word/20161007.jpg
     * picture2 : http://cdn.iciba.com/news/word/big_20161007b.jpg
     * caption : 词霸每日一句
     * dateline : 2016-10-07
     * s_pv : 0
     * sp_pv : 0
     * tags : [{"id":"10","name":"正能量"},{"id":"33","name":"人生感悟"}]
     * fenxiang_img : http://cdn.iciba.com/web/news/longweibo/imag/2016-10-07.jpg
     */

    public String sid;
    public String tts;
    public String content;
    public String note;
    public String love;
    public String translation;
    public String picture;
    public String picture2;
    public String caption;
    public String dateline;
    public String s_pv;
    public String sp_pv;
    public String fenxiang_img;
    /**
     * id : 10
     * name : 正能量
     */

    public List<TagsEntity> tags;

    @Override
    public String tts() {
        return tts;
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public String note() {
        return note;
    }

    @Override
    public String imageThumb() {
        return picture;
    }

    @Override
    public String imageHigh() {
        return picture2;
    }

    @Override
    public String caption() {
        return caption;
    }

    @Override
    public Date date() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateline);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(System.currentTimeMillis());
        }
    }

    @Override
    public String shareImage() {
        return fenxiang_img;
    }

    public static class TagsEntity {
        public String id;
        public String name;

        @Override
        public String toString() {
            return "TagsEntity{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "JinshanDayLineEntity{" +
                "caption='" + caption + '\'' +
                ", sid='" + sid + '\'' +
                ", tts='" + tts + '\'' +
                ", content='" + content + '\'' +
                ", note='" + note + '\'' +
                ", love='" + love + '\'' +
                ", translation='" + translation + '\'' +
                ", picture='" + picture + '\'' +
                ", picture2='" + picture2 + '\'' +
                ", dateline='" + dateline + '\'' +
                ", s_pv='" + s_pv + '\'' +
                ", sp_pv='" + sp_pv + '\'' +
                ", fenxiang_img='" + fenxiang_img + '\'' +
                ", tags=" + tags +
                '}';
    }
}
