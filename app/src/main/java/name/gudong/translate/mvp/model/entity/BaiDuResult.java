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

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import name.gudong.translate.mvp.model.type.ETranslateFrom;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by chenying on 16/1/27.
 */
public class BaiDuResult extends AbsResult{
    /**
     * from : en
     * to : zh
     * trans_result : [{"src":"apple","dst":"苹果"}]
     */

    private String from;
    private String to;
    /**
     * src : apple
     * dst : 苹果
     */

    private List<TransResultEntity> trans_result;

    @Override
    public List<String> wrapTranslation() {
        return null;
    }

    @Override
    public List<String> wrapExplains() {
        List<String>explains = new ArrayList<>();
        Observable.from(getTrans_result())
                .filter(new Func1<TransResultEntity, Boolean>() {
                    @Override
                    public Boolean call(TransResultEntity transResultEntity) {
                        return transResultEntity != null;
                    }
                })
                .subscribe(new Action1<TransResultEntity>() {
                    @Override
                    public void call(TransResultEntity transResultEntity) {
                        String result = transResultEntity.getDst();
                        Logger.i(result);
                        explains.add(result);
                    }
                });
        return explains;
    }

    @Override
    public String wrapQuery() {
        final String[] query = {""};
        Observable.from(getTrans_result())
                .first()
                .filter(new Func1<TransResultEntity, Boolean>() {
                    @Override
                    public Boolean call(TransResultEntity transResultEntity) {
                        return transResultEntity != null;
                    }
                })
                .subscribe(new Action1<TransResultEntity>() {
                    @Override
                    public void call(TransResultEntity transResultEntity) {
                        query[0] = transResultEntity.getSrc();
                    }
                });
        return query[0];
    }

    @Override
    public int wrapErrorCode() {
        return 0;
    }

    @Override
    public String wrapEnPhonetic() {
        return "";
    }

    @Override
    public String wrapAmPhonetic() {
        return "";
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
        return ETranslateFrom.BAI_DU.name();
    }

    @Override
    public String wrapPhEn() {
        return null;
    }

    @Override
    public String wrapPhAm() {
        return null;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTrans_result(List<TransResultEntity> trans_result) {
        this.trans_result = trans_result;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<TransResultEntity> getTrans_result() {
        return trans_result;
    }

    public static class TransResultEntity {
        private String src;
        private String dst;

        public void setSrc(String src) {
            this.src = src;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }

        public String getSrc() {
            return src;
        }

        public String getDst() {
            return dst;
        }
    }
}
