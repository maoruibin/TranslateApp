package name.gudong.translate.mvp.model.entity.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.gudong.translate.mvp.model.type.ETranslateFrom;

/**
 * Created on 2017/2/21
 *
 * @author Levine
 */

public class GoogleResult extends AbsResult{

    /**
     * [[["谷歌","google",,,1]],,"en"]
     */
    private String translationResult;

    private List<String> getTranslation(){
        List<String> translationList = new ArrayList<>(1);
        String[] translation = translationResult.replace("[", "").split("\",");
        translationList.add(translation[0].replace("\"", ""));
        return translationList;
    }

    @Override
    public List<String> wrapTranslation() {
        return getTranslation();
    }

    @Override
    public List<String> wrapExplains() {
        return Collections.emptyList();
    }

    @Override
    public String wrapQuery() {
        return translationResult;
    }

    @Override
    public int wrapErrorCode() {
        return 0;
    }

    @Override
    public String wrapEnPhonetic() {
        return null;
    }

    @Override
    public String wrapAmPhonetic() {
        return null;
    }

    @Override
    public String wrapEnMp3() {
        return null;
    }

    @Override
    public String wrapAmMp3() {
        return null;
    }

    @Override
    public String translateFrom() {
        return ETranslateFrom.GOOGLE.name();
    }

    @Override
    public String wrapPhEn() {
        return null;
    }

    @Override
    public String wrapPhAm() {
        return null;
    }

    @Override
    public String wrapMp3Name() {
        return null;
    }

    public String getTranslationResult() {
        return translationResult;
    }

    public void setTranslationResult(String translationResult) {
        this.translationResult = translationResult;
    }
}
