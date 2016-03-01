package name.gudong.translate.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * thanks to https://github.com/l123456789jy/Lazy
 */
public class InputMethodUtils {
	/**
	 * 为给定的编辑器开启软键盘
	 * @param context 
	 * @param editText 给定的编辑器
	 */
	public static void openSoftKeyboard(Context context, EditText editText){
		editText.requestFocus();
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
				Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
		ViewUtil.setEditTextSelectionToEnd(editText);
	}
	
	/**
	 * 关闭软键盘
	 * @param activity
	 */
	public static void closeSoftKeyboard(Activity activity){
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
				Context.INPUT_METHOD_SERVICE);
		//如果软键盘已经开启
		if(inputMethodManager.isActive()){
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * 切换软键盘的状态
	 * @param context
	 */
	public static void toggleSoftKeyboardState(Context context){
		((InputMethodManager) context.getSystemService(
				Context.INPUT_METHOD_SERVICE)).toggleSoftInput(
				InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
	}
}