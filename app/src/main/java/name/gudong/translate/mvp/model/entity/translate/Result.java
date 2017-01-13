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

package name.gudong.translate.mvp.model.entity.translate;

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
    public static final String COL_MARK_DONE_ONCE= "make_done_once";
    public static final String COL_MARK_DONE_ONCE_TIME= "make_done_once_time";

    public static final String COL_CREATE_TIME= "create_time";
    public static final String COL_UPDATE_TIME= "update_time";
    //update 2016/09/10
    public static final String COL_MP3_FILE_NAME= "mp3_file_name";


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

    @Column(COL_MARK_DONE_ONCE)
    protected boolean make_done_once;
    @Column(COL_MARK_DONE_ONCE_TIME)
    protected long make_done_once_time;

    @Column(COL_CREATE_TIME)
    protected long create_time;
    @Column(COL_UPDATE_TIME)
    protected long update_time;

    protected String mp3FileName;

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
        mp3FileName = mIResult.wrapMp3Name();
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

    public String getMp3FileName() {
        return mp3FileName;
    }

    public void setMp3FileName(String mp3FileName) {
        this.mp3FileName = mp3FileName;
    }

    public boolean isMake_done_once() {
        return make_done_once;
    }

    public void setMake_done_once(boolean make_done_once) {
        this.make_done_once = make_done_once;
    }

    public long getMake_done_once_time() {
        return make_done_once_time;
    }

    public void setMake_done_once_time(long make_done_once_time) {
        this.make_done_once_time = make_done_once_time;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        return query != null ? query.equals(result.query) : result.query == null;

    }

    @Override
    public int hashCode() {
        return query != null ? query.hashCode() : 0;
    }
}
