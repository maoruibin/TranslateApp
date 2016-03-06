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
 * Created by chenying on 16/1/21.
 */
public class JinShanResult  extends AbsResult{


    /**
     * word_name : go
     * is_CRI : 1
     * exchange : {"word_pl":["goes"],"word_past":["went"],"word_done":["gone"],"word_ing":["going"],"word_third":["goes"],"word_er":"","word_est":""}
     * symbols : [{"ph_en":"gəʊ","ph_am":"goʊ","ph_other":"","ph_en_mp3":"http://res.iciba.com/resource/amp3/0/0/34/d1/34d1f91fb2e514b8576fab1a75a89a6b.mp3","ph_am_mp3":"http://res.iciba.com/resource/amp3/1/0/34/d1/34d1f91fb2e514b8576fab1a75a89a6b.mp3","ph_tts_mp3":"http://res-tts.iciba.com/3/4/d/34d1f91fb2e514b8576fab1a75a89a6b.mp3","parts":[{"part":"vi.","means":["走","离开","去做","进行"]},{"part":"vt.","means":["变得","发出\u2026声音","成为","处于\u2026状态"]},{"part":"n.","means":["轮到的顺序","精力","干劲","尝试"]}]}]
     * items : [""]
     */

    private String word_name;
    private int is_CRI;
    /**
     * word_pl : ["goes"]
     * word_past : ["went"]
     * word_done : ["gone"]
     * word_ing : ["going"]
     * word_third : ["goes"]
     * word_er :
     * word_est :
     */

//    private ExchangeEntity exchange;
    /**
     * ph_en : gəʊ
     * ph_am : goʊ
     * ph_other :
     * ph_en_mp3 : http://res.iciba.com/resource/amp3/0/0/34/d1/34d1f91fb2e514b8576fab1a75a89a6b.mp3
     * ph_am_mp3 : http://res.iciba.com/resource/amp3/1/0/34/d1/34d1f91fb2e514b8576fab1a75a89a6b.mp3
     * ph_tts_mp3 : http://res-tts.iciba.com/3/4/d/34d1f91fb2e514b8576fab1a75a89a6b.mp3
     * parts : [{"part":"vi.","means":["走","离开","去做","进行"]},{"part":"vt.","means":["变得","发出\u2026声音","成为","处于\u2026状态"]},{"part":"n.","means":["轮到的顺序","精力","干劲","尝试"]}]
     */

    private List<SymbolsEntity> symbols;
    private List<String> items;

    public void setWord_name(String word_name) {
        this.word_name = word_name;
    }

    public void setIs_CRI(int is_CRI) {
        this.is_CRI = is_CRI;
    }

//    public void setExchange(ExchangeEntity exchange) {
//        this.exchange = exchange;
//    }

    public void setSymbols(List<SymbolsEntity> symbols) {
        this.symbols = symbols;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getWord_name() {
        return word_name;
    }

    public int getIs_CRI() {
        return is_CRI;
    }

//    public ExchangeEntity getExchange() {
//        return exchange;
//    }

    public List<SymbolsEntity> getSymbols() {
        return symbols;
    }

    public List<String> getItems() {
        return items;
    }

    public static class ExchangeEntity {
        private String word_er;
        private String word_est;
        private List<String> word_pl;
        private List<String> word_past;
        private List<String> word_done;
        private List<String> word_ing;
        private List<String> word_third;

        public void setWord_er(String word_er) {
            this.word_er = word_er;
        }

        public void setWord_est(String word_est) {
            this.word_est = word_est;
        }

        public void setWord_pl(List<String> word_pl) {
            this.word_pl = word_pl;
        }

        public void setWord_past(List<String> word_past) {
            this.word_past = word_past;
        }

        public void setWord_done(List<String> word_done) {
            this.word_done = word_done;
        }

        public void setWord_ing(List<String> word_ing) {
            this.word_ing = word_ing;
        }

        public void setWord_third(List<String> word_third) {
            this.word_third = word_third;
        }

        public String getWord_er() {
            return word_er;
        }

        public String getWord_est() {
            return word_est;
        }

        public List<String> getWord_pl() {
            return word_pl;
        }

        public List<String> getWord_past() {
            return word_past;
        }

        public List<String> getWord_done() {
            return word_done;
        }

        public List<String> getWord_ing() {
            return word_ing;
        }

        public List<String> getWord_third() {
            return word_third;
        }
    }

    public static class SymbolsEntity {
        private String ph_en;
        private String ph_am;
        private String ph_other;
        private String ph_en_mp3;
        private String ph_am_mp3;
        private String ph_tts_mp3;
        /**
         * part : vi.
         * means : ["走","离开","去做","进行"]
         */

        private List<PartsEntity> parts;

        public void setPh_en(String ph_en) {
            this.ph_en = ph_en;
        }

        public void setPh_am(String ph_am) {
            this.ph_am = ph_am;
        }

        public void setPh_other(String ph_other) {
            this.ph_other = ph_other;
        }

        public void setPh_en_mp3(String ph_en_mp3) {
            this.ph_en_mp3 = ph_en_mp3;
        }

        public void setPh_am_mp3(String ph_am_mp3) {
            this.ph_am_mp3 = ph_am_mp3;
        }

        public void setPh_tts_mp3(String ph_tts_mp3) {
            this.ph_tts_mp3 = ph_tts_mp3;
        }

        public void setParts(List<PartsEntity> parts) {
            this.parts = parts;
        }

        public String getPh_en() {
            return ph_en;
        }

        public String getPh_am() {
            return ph_am;
        }

        public String getPh_other() {
            return ph_other;
        }

        public String getPh_en_mp3() {
            return ph_en_mp3;
        }

        public String getPh_am_mp3() {
            return ph_am_mp3;
        }

        public String getPh_tts_mp3() {
            return ph_tts_mp3;
        }

        public List<PartsEntity> getParts() {
            return parts;
        }

        public static class PartsEntity {
            private String part;
            private List<String> means;

            public void setPart(String part) {
                this.part = part;
            }

            public void setMeans(List<String> means) {
                this.means = means;
            }

            public String getPart() {
                return part;
            }

            public List<String> getMeans() {
                return means;
            }
        }
    }


    @Override
    public List<String> wrapTranslation() {
        return null;
    }

    @Override
    public List<String> wrapExplains() {
        List<String>explains = new ArrayList<>();
        Observable.from(getSymbols())
                .first()
                .map(new Func1<SymbolsEntity, List<SymbolsEntity.PartsEntity>>() {
                    @Override
                    public List<SymbolsEntity.PartsEntity> call(SymbolsEntity symbolEntity) {
                        return symbolEntity.getParts();
                    }
                })
                .filter(new Func1<List<SymbolsEntity.PartsEntity>, Boolean>() {
                    @Override
                    public Boolean call(List<SymbolsEntity.PartsEntity> partsEntities) {
                        return partsEntities!=null;
                    }
                })
                .flatMap(new Func1<List<SymbolsEntity.PartsEntity>, Observable<SymbolsEntity.PartsEntity>>() {
                    @Override
                    public Observable<SymbolsEntity.PartsEntity> call(List<SymbolsEntity.PartsEntity> partEntities) {
                        return Observable.from(partEntities);
                    }
                })
                .subscribe(new Action1<SymbolsEntity.PartsEntity>() {
                    @Override
                    public void call(SymbolsEntity.PartsEntity partEntity) {
                        StringBuilder sb = new StringBuilder(partEntity.getPart());
                        sb.append(",");
                        if(!partEntity.getMeans().isEmpty()){
                            sb.append(partEntity.getMeans().get(0));
                        }
                        Logger.i(sb.toString());
                        explains.add(sb.toString());
                    }
                });
        return explains;
    }

    @Override
    public String wrapQuery() {
        return getWord_name();
    }

    @Override
    public int wrapErrorCode() {
        return 0;
    }

    @Override
    public String wrapEnPhonetic() {
        final String[] mp3 = {""};
        Observable.from(getSymbols())
                .first()
                .subscribe(new Action1<SymbolsEntity>() {
                    @Override
                    public void call(SymbolsEntity symbolEntity) {
                        mp3[0] = symbolEntity.getPh_en();
                    }
                });
        return mp3[0];
    }

    @Override
    public String wrapAmPhonetic() {
        final String[] mp3 = {""};
        Observable.from(getSymbols())
                .first()
                .subscribe(new Action1<SymbolsEntity>() {
                    @Override
                    public void call(SymbolsEntity symbolEntity) {
                        mp3[0] = symbolEntity.getPh_am();
                    }
                });
        return mp3[0];
    }

    @Override
    public String wrapEnMp3() {
        final String[] mp3 = {""};
        Observable.from(getSymbols())
                .first()
                .subscribe(new Action1<SymbolsEntity>() {
                    @Override
                    public void call(SymbolsEntity symbolEntity) {
                        mp3[0] = symbolEntity.ph_en_mp3;
                    }
                });
        return mp3[0];
    }

    @Override
    public String wrapAmMp3() {
        final String[] mp3 = {""};
        Observable.from(getSymbols())
                .first()
                .subscribe(new Action1<SymbolsEntity>() {
                    @Override
                    public void call(SymbolsEntity symbolEntity) {
                        mp3[0] = symbolEntity.ph_am_mp3;
                    }
                });
        return mp3[0];
    }

    @Override
    public String translateFrom() {
        return ETranslateFrom.JIN_SHAN.name();
    }

    @Override
    public String wrapPhEn() {
        final String[] phEn = {""};
        Observable.from(getSymbols())
                .first()
                .subscribe(new Action1<SymbolsEntity>() {
                    @Override
                    public void call(SymbolsEntity symbolEntity) {
                        phEn[0] = symbolEntity.ph_en;
                    }
                });
        return phEn[0];
    }

    @Override
    public String wrapPhAm() {
        final String[] phAm = {""};
        Observable.from(getSymbols())
                .first()
                .subscribe(new Action1<SymbolsEntity>() {
                    @Override
                    public void call(SymbolsEntity symbolEntity) {
                        phAm[0] = symbolEntity.ph_am;
                    }
                });
        return phAm[0];
    }
}
