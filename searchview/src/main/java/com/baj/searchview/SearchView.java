package com.baj.searchview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2018/5/5.
 * 搜索框
 */

public class SearchView extends LinearLayout implements TextWatcher {

    // 输入框
    private EditText etSearch;
    // 删除图标
    private ImageView ivClear;
    // 匹配方法
    private SearchWay mSearchWay;
    // 改变后的文字
    private String searchText = "";
    // 等待线程
    private WaitThread waitThread;
    //延时搜索时间，默认200ms
    private int waitTime = 200;
    //当前延时时间
    private int curTime;

    // 异步处理
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // 更新回调接口
            if (msg.what == 0){
                waitThread = null;
                // 匹配结果回调
                List searchList = new ArrayList();
                List list = mSearchWay.getData();
                if (list != null){
                    for (Object obj : list) {
                        if (mSearchWay.matchItem(obj, searchText)){
                            searchList.add(obj);
                        }
                    }
                    mSearchWay.update(searchList);
                }
            }
            super.handleMessage(msg);
        }
    };


    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // 加载布局文件
        View view = LayoutInflater.from(context).inflate(R.layout.view_search,null);
        // 获取控件
        etSearch = view.findViewById(R.id.et_search);
        ivClear = view.findViewById(R.id.iv_clear);
        // 设置清空按钮的触发器
        ivClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
            }
        });
        // 读取属性
        TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.SearchView);
        String str;
        // 文字大小
        float textSize = typed.getDimension(R.styleable.SearchView_sv_textSize,15);
        etSearch.setTextSize(textSize);
        // 搜索框文字
        str = typed.getString(R.styleable.SearchView_sv_text);
        if (str != null){
            etSearch.setText(str);
        }
        // 提示文字
        str = typed.getString(R.styleable.SearchView_sv_hint);
        if (str != null){
            etSearch.setHint(str);
        }
        // 是否隐藏搜索图标
        boolean hideImg = typed.getBoolean(R.styleable.SearchView_sv_hideImg, false);
        if (hideImg){
            view.findViewById(R.id.iv_search).setVisibility(View.GONE);
        }
        // 设置文字颜色
        etSearch.setTextColor(Color.parseColor("#333333"));
        etSearch.setHintTextColor(Color.parseColor("#999999"));
        // 回收资源
        typed.recycle();
        // 文字改变监听
        etSearch.addTextChangedListener(this);
        // 把布局添加到当前控件中
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, params);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        // 显示或隐藏删除图标
        if (editable.toString().isEmpty()){
            ivClear.setVisibility(GONE);
        }else {
            ivClear.setVisibility(VISIBLE);
        }
        if (mSearchWay != null){
            if (waitThread == null){
                waitThread = new WaitThread();
                waitThread.start();
            }else {
                // 搜索框的文字发生变化就重置等待时间
                if (!searchText.equals(editable.toString())){
                    curTime = 0;
                }
            }
        }
        searchText = editable.toString();
    }

    /**
     * 获取搜索框的文字
     */
    public String getText(){
        return etSearch.getText().toString();
    }

    // region 延时
    /**
     * 设置延时时间
     * @param waitTime 毫秒，精度为100ms
     */
    public void setWaitTime(int waitTime){
        this.waitTime = waitTime;
    }

    private class WaitThread extends Thread{
        @Override
        public void run() {
            // 等待延时
            for (curTime = 0; curTime < waitTime; curTime += 100) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mHandler.sendEmptyMessage(0);
        }
    }
    // endregion

    // region SearchWay

    public void setSearchWay(SearchWay way){
        this.mSearchWay = way;
    }

    public SearchWay getSearchWay(){
        return mSearchWay;
    }

    /**
     * 用于匹配项
     */
    public static abstract class SearchWay<T> {
        /**
         * @return 数据源
         */
        public abstract List<T> getData();

        /**
         * @return item中是否含有s
         */
        public abstract boolean matchItem(T item, String s);

        /**
         * 更新列表
         * @param resultList 匹配的数据，重新加载列表
         */
        public abstract void update(List<T> resultList);
    }

    // endregion
}
