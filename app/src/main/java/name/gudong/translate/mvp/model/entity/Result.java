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

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by GuDong on 1/22/16 10:11.
 * Contact with gudong.name@gmail.com.
 */
@Table("tb_words")
public class Result implements Serializable{
    public static final String COL_ID = "_id";
    public static final String COL_QUERY = "query";
    public static final String COL_TRANSLATION= "translation";
    public static final String COL_EXPLAINS= "explains";
    public static final String COL_US_PHONETIC= "us_phonetic";
    public static final String COL_UK_PHONETIC= "uk_phonetic";
    public static final String COL_PH_EN_MP3= "ph_en_mp3";
    public static final String COL_PH_AM_MP3= "ph_am_mp3";
    public static final String COL_TRANSLATE_FROM= "translate_from";
    public static final String COL_PH_EN =  "ph_en";
    public static final String COL_PH_AM= "ph_am";


    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column(COL_ID)
    protected long id;

    @Column(COL_QUERY)
    protected String query;
    @Column(COL_TRANSLATION)
    protected List<String> translation;
    @Column(COL_EXPLAINS)
    protected List<String> explains;
    @Column(COL_US_PHONETIC)
    protected String usPhonetic;
    @Column(COL_UK_PHONETIC)
    protected String ukPhonetic;
    @Column(COL_PH_EN_MP3)
    protected String enMp3;
    @Column(COL_PH_AM_MP3)
    protected String amMp3;
    @Column(COL_TRANSLATE_FROM)
    protected String translateFrom;
    @Column(COL_PH_EN)
    protected String phEn;
    @Column(COL_PH_AM)
    protected String phAm;

    public Result() {
    }

    public Result(IResult mIResult) {
        query = mIResult.wrapQuery();
        translation = mIResult.wrapTranslation();
        explains = mIResult.wrapExplains();
        usPhonetic = mIResult.wrapAmPhonetic();
        ukPhonetic = mIResult.wrapEnPhonetic();
        enMp3 = mIResult.wrapEnMp3();
        amMp3 = mIResult.wrapAmMp3();
        translateFrom = mIResult.translateFrom();
        phAm = mIResult.wrapPhAm();
        phEn = mIResult.wrapPhEn();
    }

    public String getAmMp3() {
        return amMp3;
    }

    public void setAmMp3(String amMp3) {
        this.amMp3 = amMp3;
    }

    public String getEnMp3() {
        return enMp3;
    }

    public void setEnMp3(String enMp3) {
        this.enMp3 = enMp3;
    }

    public List<String> getExplains() {
        return explains;
    }

    public void setExplains(List<String> explains) {
        this.explains = explains;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
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

    public String getPhAm() {
        return phAm;
    }

    public void setPhAm(String phAm) {
        this.phAm = phAm;
    }

    public String getPhEn() {
        return phEn;
    }

    public void setPhEn(String phEn) {
        this.phEn = phEn;
    }
}
