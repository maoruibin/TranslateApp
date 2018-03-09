package name.gudong.translate.util;

import android.os.Build;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.AnswersEvent;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by mao on 2018/2/8.
 */

public class AnswerUtil {
    public static void trackClick(){}

    public static void showMainView(String translateType){
        ContentViewEvent event = new ContentViewEvent();
        event.putContentType(translateType);
        Answers.getInstance().logContentView(event);
    }

    public static void translateSuccess(){
        Answers.getInstance().logCustom(new CustomEvent("translateSuccess"));
    }

    public static void actionShowAbout(){
        Answers.getInstance().logCustom(new CustomEvent("actionShowAbout"));
    }

    /**
     * 显示彩蛋
     */
    public static void showEggs(){
        Answers.getInstance().logCustom(new CustomEvent("showEggs"));
    }

    public static void actionFavorite(String source){
        CustomEvent event = new CustomEvent("actionFavorite");
        event.putCustomAttribute("source",source);
        Answers.getInstance().logCustom(event);
    }

    public static void actionSound(String source){
        CustomEvent event = new CustomEvent("actionSound");
        event.putCustomAttribute("source",source);
        Answers.getInstance().logCustom(event);
    }

    public static void actionSupport(){
        Answers.getInstance().logCustom(new CustomEvent("actionSupport"));
    }

    public static void actionSupportPay(){
        CustomEvent event = new CustomEvent("actionSupportPay");
        event.putCustomAttribute("device",Build.BRAND+"-"+Build.MODEL);
        Answers.getInstance().logCustom(event);
    }

    public static void translateFail(String msg){
        CustomEvent event = new CustomEvent("translateFail");
        event.putCustomAttribute("errorType",msg);
        Answers.getInstance().logCustom(event);
    }
}
