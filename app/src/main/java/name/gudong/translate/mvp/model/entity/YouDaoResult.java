/*
 *  Copyright (C) 2015 GuDong <gudong.name@gmail.com>
 *
 *  This file is part of GdTranslate
 *
 *  GdTranslate is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  GdTranslate is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with GdTranslate.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package name.gudong.translate.mvp.model.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

import name.gudong.translate.mvp.model.type.ETranslateFrom;

/**
 * Created by GuDong on 1/21/16 15:04.
 * Contact with gudong.name@gmail.com.
 */
public class YouDaoResult extends AbsResult{

    private String query;
    private List<String>translation;
    private BasicEntity basic;
    private int errorCode;
    private List<WebEntity> web;

    @Override
    public List<String> wrapTranslation() {
        return getTranslation();
    }

    @Override
    public List<String> wrapExplains() {
        if(getBasic() != null){
            return getBasic().getExplains();
        }else{
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public String wrapQuery() {
        return getQuery();
    }

    @Override
    public int wrapErrorCode() {
        return getErrorCode();
    }

    @Override
    public String wrapEnPhonetic() {
        return getBasic().getUkPhonetic();
    }

    @Override
    public String wrapAmPhonetic() {
        return getBasic().getUsPhonetic();
    }

    @Override
    public String wrapEnMp3() {
        return "";
    }

    @Override
    public String wrapAmMp3() {
        return "";
    }

    @Override
    public String translateFrom() {
        return ETranslateFrom.YOU_DAO.name();
    }

    @Override
    public String wrapPhEn() {
        return getBasic().getUkPhonetic();
    }

    @Override
    public String wrapPhAm() {
        return getBasic().getUsPhonetic();
    }

    public class BasicEntity {
        @SerializedName("us-phonetic")
        private String usPhonetic;
        private String phonetic;
        @SerializedName("uk-phonetic")
        private String ukPhonetic;
        private List<String> explains;

        public List<String> getExplains() {
            return explains;
        }

        public void setExplains(List<String> explains) {
            this.explains = explains;
        }

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public String getUkPhonetic() {
            return ukPhonetic;
        }

        public void setUkPhonetic(String ukPhonetic) {
            this.ukPhonetic = ukPhonetic;
        }

        public String getUsPhonetic() {
            return usPhonetic;
        }

        public void setUsPhonetic(String usPhonetic) {
            this.usPhonetic = usPhonetic;
        }
    }

    public class WebEntity {
        private String key;
        private List<String> value;

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public List<String> getValue() {
            return value;
        }
    }


    public void setQuery(String query) {
        this.query = query;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public void setWeb(List<WebEntity> web) {
        this.web = web;
    }

    public String getQuery() {
        return query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public List<WebEntity> getWeb() {
        return web;
    }

    public BasicEntity getBasic() {
        return basic;
    }

    public void setBasic(BasicEntity basic) {
        this.basic = basic;
    }

}
