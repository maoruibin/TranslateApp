package name.gudong.translate.mvp.model.entity.translate;

import com.litesuits.orm.db.annotation.Table;

/**
 * author  : ruibin1 (ruibin1@staff.weibo.com)
 * create  : 2018/2/3 - 下午6:45.
 */

@Table("tb_hist_words")
public class HistoryResult extends Result {
    public static HistoryResult toResult(Result entity) {
        HistoryResult result = new HistoryResult();
        result.setId(entity.getId());
        result.setQuery(entity.getQuery());
        result.setAmMp3(entity.getAmMp3());
        result.setEnMp3(entity.getEnMp3());
        result.setCreate_time(entity.getCreate_time());
        result.setUpdate_time(entity.getUpdate_time());
        result.setExplains(entity.getExplains());
        result.setMp3FileName(entity.getMp3FileName());
        result.setPhAm(entity.getPhAm());
        result.setPhEn(entity.getPhEn());
        result.setTranslation(entity.getTranslation());
        result.setUkPhonetic(entity.getUkPhonetic());
        result.setUsPhonetic(entity.getUsPhonetic());
        return result;
    }
}
