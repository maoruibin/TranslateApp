package name.gudong.translate.ui;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;

import me.drakeet.support.about.Card;
import me.drakeet.support.about.CardViewBinder;

/**
 * Created by GuDong on 2017/10/25 08:26.
 * Contact with gudong.name@gmail.com.
 */

public class LinkCardViewBinder extends CardViewBinder {
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Card card) {
//       super.onBindViewHolder(holder, card);
        String content = card.content.toString();
        if(content.equals("link_log")){
            String s1 = "<p>查看历史日志</p><br><a href=\"https://github.com/maoruibin/TranslateApp/blob/master/doc/Changelog.md\" title=\"历史日志\">链接</a>";
            Spanned text = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                text = Html.fromHtml(s1, Html.FROM_HTML_MODE_COMPACT);
            } else {
                text = Html.fromHtml(s1);
            }
            holder.content.setText(text);
            holder.content.setMovementMethod(LinkMovementMethod.getInstance());
        }else{
            super.onBindViewHolder(holder, card);
        }
    }
}
