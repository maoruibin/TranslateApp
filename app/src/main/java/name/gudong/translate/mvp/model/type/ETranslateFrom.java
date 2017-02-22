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

package name.gudong.translate.mvp.model.type;

import name.gudong.translate.mvp.model.ApiBaidu;
import name.gudong.translate.mvp.model.ApiGoogle;
import name.gudong.translate.mvp.model.ApiJinShan;
import name.gudong.translate.mvp.model.ApiYouDao;

/**
 * Created by GuDong on 1/22/16 18:12.
 * Contact with gudong.name@gmail.com.
 *
 * updated by Levine on 2/21/17
 */
public enum  ETranslateFrom {

    BAI_DU(0,"百度","http://api.fanyi.baidu.com/", ApiBaidu.class),
    YOU_DAO(1,"有道","http://fanyi.youdao.com/",ApiYouDao.class),
    JIN_SHAN(2,"金山","http://dict-co.iciba.com/",ApiJinShan.class),
    GOOGLE(3, "谷歌", "http://translate.google.cn/",ApiGoogle.class);


    private int index;
    private String name;
    private String url;
    private Class aqiClass;

    ETranslateFrom(int index,String name,String url, Class aqiClass) {
        this.index = index;
        this.name = name;
        this.url = url;
        this.aqiClass = aqiClass;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Class getAqiClass() {
        return aqiClass;
    }
}
