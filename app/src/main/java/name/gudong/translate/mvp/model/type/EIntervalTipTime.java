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
 * Created by GuDong on 2/22/16 21:41.
 * Contact with gudong.name@gmail.com.
 */
public enum EIntervalTipTime {
    ONE_MINUTE(1),
    THREE_MINUTE(3),
    FIVE_MINUTE(5),
    TEN_MINUTE(10),
    THIRTY_MINUTE(30);

    private int mIntervalTime;

    EIntervalTipTime(int time) {
        this.mIntervalTime = time;
    }

    public int getIntervalTime() {
        return mIntervalTime;
    }
}
