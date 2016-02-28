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

/**
 * Created by GuDong on 1/22/16 18:12.
 * Contact with gudong.name@gmail.com.
 */
public enum  ETranslateFrom {

    BAI_DU("百度","http://api.fanyi.baidu.com/api/trans/vip/translate"),
    YOU_DAO("有道","http://fanyi.youdao.com/openapi.do"),
    JIN_SHAN("金山","http://dict-co.iciba.com/api/dictionary.php");

    private String name;
    private String url;

    ETranslateFrom(String name,String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
