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

package name.gudong.translate.mvp.presenters;

import android.app.Service;
import android.text.TextUtils;

import com.litesuits.orm.LiteOrm;
import com.orhanobut.logger.Logger;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.gudong.translate.BuildConfig;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
import name.gudong.translate.mvp.model.DownloadService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.AbsResult;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.mvp.views.IClipboardService;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.StringUtils;
import name.gudong.translate.util.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by GuDong on 2/28/16 20:48.
 * Contact with gudong.name@gmail.com.
 */
public class ClipboardPresenter extends BasePresenter<IClipboardService> {
    private static final String KEY_TAG = "clipboard";
    /**
     * 首先说明一个情况，这里的系统粘贴板在监听到粘贴板内容发生变化时
     * 对应的监听方法会被执行多次 也就是说用户的复制操作 会引起这里发送多次数据请求
     * 这是不合理的，所以设置一个缓存用于解决c重复请求问题
     */

    /**
     * 定义一个查询集合，用于缓存当前的查询队列，当队列中存在已经复制的关键字就不会继续去发起查询操作了
     * 注意要在合适的时候清空它
     */
    private List<String> listQuery = new ArrayList<>();

    /**
     * 记录不同 TipView 原始 Result -> 本地 Result 映射
     * 当初始化界面时，会拿网络返回的 Result 去做本地查询，看他有没有本地的收藏，如果有，就把他加入这这个 map
     * 键为原始 Result 值为 本地 Result  可能为空
     */
    private Map<Result,Result> mMapResult = new WeakHashMap<>();

    @Inject
    ClipboardManagerCompat mClipboardWatcher;

    /**
     * 循环展示单词结果
     */
    private List<Result> results;

    private int currentIndex = -1;

    private ClipboardManagerCompat.OnPrimaryClipChangedListener mListener = () -> performClipboardCheck();

    /**
     * 定时显示 Tip 事件源
     */
    Subscription mSubscription;
    /**
     * 显示 Tip 的动作
     */
    Action1 mActionShowTip;


    @Inject
    public ClipboardPresenter(LiteOrm liteOrm, WarpAipService apiService, DownloadService downloadService, Service service) {
        super(liteOrm, apiService,downloadService, service);
        results = mLiteOrm.query(Result.class);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        initCountdownSetting();
        if(SpUtils.isShowIconInNotification(getContext())){
            Utils.showNormalNotification(getContext());
        }
    }

    private void initCountdownSetting(){
        mActionShowTip = (t)->{
            Logger.i("====","time is out show words");
            Result result = getResult();
            if(result == null)return;
            mView.showResult(result,false);
        };
    }

    /**
     * 开启背单词
     * @param interval 时间间隙 单位 分钟
     */
    public void openTipCyclic(long interval,TimeUnit unit){
        if(mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }

        mSubscription = Observable.interval(interval,unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mActionShowTip);
    }

    public void removeTipCyclic(){
        if(mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
            mSubscription = null;
            Logger.i(KEY_TAG,"移除背单词服务");
        }
    }

    public void search(final String content) {
        Logger.i("search 开始查词 "+content);

        mWarpApiService.translate(SpUtils.getTranslateEngineWay(mService), content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((result)->{return result.wrapErrorCode() == 0;})
                .subscribe(new Subscriber<AbsResult>() {
                    @Override
                    public void onCompleted() {
                        //清空缓存
                        listQuery.clear();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof SocketTimeoutException){
                            mView.errorPoint("网络请求超时，请稍后重试。");
                        }else{
                            if(BuildConfig.DEBUG){
                                mView.errorPoint("请求数据异常，您可以试试切换其他引擎。"+e.getMessage());
                                e.printStackTrace();
                            }else{
                                mView.errorPoint("请求数据异常，您可以试试切换其他引擎。");
                            }
                        }
                    }

                    @Override
                    public void onNext(AbsResult result) {
                        mView.showResult(result.getResult(),true);
                    }
                });
    }

    /**
     * 添加粘贴板变化监听方法
     */
    public void addListener() {
        mClipboardWatcher.addPrimaryClipChangedListener(mListener);
    }

    public void initFavoriteStatus(Result result){
        Result localResult= isFavorite(result.getQuery());
        if(localResult!=null){
            mView.initWithFavorite(result);
        }else{
            mView.initWithNotFavorite(result);
        }
    }

    public void clickFavorite(Result result){
        Result localResult= isFavorite(result.getQuery());
        if (localResult!=null) {
            int index = deleteResultFromDb(localResult);
            if (index > 0) {
                mView.initWithNotFavorite(result);
                Logger.i("删除成功");
            } else {
                Logger.i("删除失败");
            }
        }else{
            long index = insertResultToDb(result);
            if (index > 0) {
                mView.initWithFavorite(result);
                Logger.i("插入成功");
            } else {
                Logger.i("插入失败");
            }
        }
    }

    private void performClipboardCheck() {
        CharSequence content = mClipboardWatcher.getText();

        //处理缓存 因为粘贴板的回调操作可能触发多次
        String query = content.toString();
        Logger.i("粘贴板的单词为 "+query);
        if (listQuery.contains(query)) {
            Logger.i("is search in "+query);
            return;
        }
        listQuery.add(query);

        //只有用户在打开了 划词翻译的情况下 划词翻译才能正常工作
        if(!SpUtils.getOpenJITOrNot(mService))return;

        //如果当前界面是 咕咚翻译的主界面 那么也不对粘贴板做监听( Debug 时开启)
        if(!BuildConfig.DEBUG){
            if(SpUtils.getAppFront(mService))return;
        }

        // 检查粘贴板的内容是不是单词 以及是不是为空
        if(!checkInput(content.toString())){
            Logger.i("粘贴板为空");
            return;
        }

        //查询数据
        search(query);
    }

    private boolean checkInput(String input){
        // empty check
        if (TextUtils.isEmpty(input)) {
            Logger.e("剪贴板为空了");
            return false;
        }

        if(StringUtils.isChinese(input)){
            Logger.e(input+" 中包含中文字符");
            return false;
        }

        if(StringUtils.isValidEmailAddress(input)){
            Logger.e(input+" 是一个邮箱");
            return false;
        }

        if(StringUtils.isValidUrl(input)){
            Logger.e(input+" 是一个网址");
            return false;
        }

        if(StringUtils.isValidNumeric(input)){
            Logger.e(input+" 是一串数字");
            return false;
        }

        // length check
        if(StringUtils.isMoreThanOneWord(input)){
            mView.errorPoint("咕咚翻译目前不支持划句或者划短语翻译\n多谢理解");
            return false;
        }

        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        mClipboardWatcher.removePrimaryClipChangedListener(mListener);
    }


    private Result getResult(){
        int index = getResultIndex();
        Logger.i("index is "+index);
        if(index>=0){
            return results.get(index);
        } else{
            return null;
        }
    }

    private int getResultIndex(){
        if(results.isEmpty()){
            return -1;
        }
        currentIndex = currentIndex+1;
        if(currentIndex == results.size()-1){
            currentIndex = -1;
            return results.size()-1;
        }
        return currentIndex;
    }
}