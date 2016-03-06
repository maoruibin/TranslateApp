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

import java.util.List;

/**
 * Created by GuDong on 1/21/16 17:05.
 * Contact with gudong.name@gmail.com.
 */
public interface IResult {
    /**
     * 长句翻译结果
     * @return
     */
    List<String> wrapTranslation();

    /**
     * 单词解释
     * @return
     */
    List<String> wrapExplains();

    /**
     * 查询关键字
     * @return
     */
    String wrapQuery();

    /**
     * 查询结果错误码
     * @return
     */
    int wrapErrorCode();

    /**
     * 英式音标
     * @return
     */
    String wrapEnPhonetic();

    /**
     * 美式音标
     * @return
     */
    String wrapAmPhonetic();

    /**
     * 英式发音 MP3
     * @return
     */
    String wrapEnMp3();

    /**
     * 美式发音 MP3
     * @return
     */
    String wrapAmMp3();

    /**
     * 翻译来源
     * @return
     */
    String translateFrom();

    /**
     * 英式音标
     * @return
     */
    String wrapPhEn();

    /**
     * 美式音标
     * @return
     */
    String wrapPhAm();

}
