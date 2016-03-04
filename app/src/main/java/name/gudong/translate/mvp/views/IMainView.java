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

package name.gudong.translate.mvp.views;

import android.view.Menu;

import name.gudong.translate.mvp.model.entity.AbsResult;
import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;
import name.gudong.translate.mvp.model.type.ETranslateFrom;

/**
 * Created by GuDong on 2/28/16 00:16.
 * Contact with gudong.name@gmail.com.
 */
public interface IMainView extends IBaseView {

    void onInitSearchText(String text);

    void onPrepareTranslate();

    /**
     * 清空翻译结果对应容器中所有的view
     */
    void onClearResultViews();

    void appendBottomView(AbsResult result);

    void onError(Throwable e);

    void addExplainItem(String explain);

    void initTranslateEngineSetting(Menu menu, ETranslateFrom way);

    void initDurationTimeSetting(Menu menu, EDurationTipTime way);

    void initIntervalTimeSetting(Menu menu,EIntervalTipTime way);

    /**
     * 初始化是否背单词的设置
     * @param menu
     * @param isOpen
     */
    void initReciteSetting(Menu menu, boolean isOpen);

    /**
     * 初始化是否开启划词翻译
     * @param menu
     * @param isOpen
     */
    void initJITSetting(Menu menu, boolean isOpen);
}
