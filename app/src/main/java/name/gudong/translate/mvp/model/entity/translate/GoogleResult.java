package name.gudong.translate.mvp.model.entity.translate;

import java.util.ArrayList;
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
        if(translation.length > 0){
            translationList.add(translation[0].replace("\"", ""));
        }
        return translationList;
    }

    private String getQuery(){
        String[] translation = translationResult.replace("[", "").split("\",");
        if(translation.length >1){
            return translation[1].replace("\"", "");
        }
        return "";
    }

    @Override
    public List<String> wrapTranslation() {
        return getTranslation();
    }

    @Override
    public List<String> wrapExplains() {
        return getTranslation();
    }

    @Override
    public String wrapQuery() {
        return getQuery();
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
