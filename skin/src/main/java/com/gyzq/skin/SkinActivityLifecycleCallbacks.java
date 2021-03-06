package com.gyzq.skin;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;

import com.gyzq.skin.display.ScreenAdaptationUtil;
import com.gyzq.skin.font.FontScaleUtil;
import com.gyzq.skin.language.LanguageManager;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author: liujie
 * @date: 2021/6/4 下午5:46
 * @description: 监听所有activity，拦截activity的布局创建
 */
public class SkinActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    /**
     * 保存activity，对应的布局工厂，联动生命周期
     */
    private final HashMap<Activity, SkinLayoutInflaterFactory> mSkinLayoutInflaterFactoryHashMap = new HashMap<>();

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        //初始化语言设置
        LanguageManager.getInstance().attachBaseContext(activity);
        //初始化字体大小缩放
        FontScaleUtil.applyFontScale(activity, SkinManager.getInstance().getFontScale());
        //屏幕适配
        ScreenAdaptationUtil.getInstance().setCustomDensity(activity, activity.getApplication());
        System.out.println("density"+activity.getResources().getDisplayMetrics().density);
        System.out.println("densityDpi"+activity.getResources().getDisplayMetrics().densityDpi);
        System.out.println("heightPixels"+activity.getResources().getDisplayMetrics().heightPixels);
        System.out.println("widthPixels"+activity.getResources().getDisplayMetrics().widthPixels);
        System.out.println("scaledDensity"+activity.getResources().getDisplayMetrics().scaledDensity);
        System.out.println("fontScale"+activity.getResources().getConfiguration().fontScale);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        try {
            //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
            //如设置过抛出异常
            //设置layoutInflater mFactorySet变量为false
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory(activity);
        // 设置自定义Factory2
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        SkinManager.getInstance().addObserver(skinLayoutInflaterFactory);
        mSkinLayoutInflaterFactoryHashMap.put(activity, skinLayoutInflaterFactory);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        //通过activity删除布局工厂
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = mSkinLayoutInflaterFactoryHashMap.remove(activity);
        if (skinLayoutInflaterFactory != null) {
            skinLayoutInflaterFactory.destroyView();
        }
        //解绑注册
        SkinManager.getInstance().deleteObserver(skinLayoutInflaterFactory);
    }
}
