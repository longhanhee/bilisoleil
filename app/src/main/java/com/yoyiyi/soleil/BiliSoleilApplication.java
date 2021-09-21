package com.yoyiyi.soleil;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.facebook.flipper.android.AndroidFlipperClient;
import com.facebook.flipper.android.utils.FlipperUtils;
import com.facebook.flipper.core.FlipperClient;
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin;
import com.facebook.flipper.plugins.inspector.DescriptorMapping;
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin;
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin;
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin;
import com.facebook.soloader.SoLoader;
import com.facebook.stetho.Stetho;
import com.yoyiyi.soleil.di.component.AppComponent;
import com.yoyiyi.soleil.di.component.DaggerAppComponent;
import com.yoyiyi.soleil.di.module.ApiModule;
import com.yoyiyi.soleil.di.module.AppModule;
import com.yoyiyi.soleil.network.helper.OkHttpHelper;
import com.yoyiyi.soleil.utils.AppUtils;
import com.yoyiyi.soleil.utils.CrashHandler;
import com.yoyiyi.soleil.utils.LogUtils;
import com.yoyiyi.soleil.utils.NetworkUtils;
import com.yoyiyi.soleil.utils.PrefsUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zzq  作者 E-mail:   soleilyoyiyi@gmail.com
 * @date 创建时间：2017/4/28 11:27
 * 描述:APP
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
public final class BiliSoleilApplication extends Application {
    private static BiliSoleilApplication mContext;
    private Set<Activity> allActivities;
    private AppComponent mAppComponent;
    public NetworkFlipperPlugin networkFlipperPlugin;

    public NetworkFlipperPlugin getNetworkFlipperPlugin() {
        return networkFlipperPlugin;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.init(this);
        mContext = this;

        SoLoader.init(this, false);

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            final FlipperClient client = AndroidFlipperClient.getInstance(this);
            client.addPlugin(OkHttpHelper.getInstance().getNetworkFlipperPlugin());
            client.addPlugin(new InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()));
            client.start();
        }

//        SoLoader.init(this, false);
//
//        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
//            final FlipperClient client = AndroidFlipperClient.getInstance(this);

//            client.addPlugin(new InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()));
//            client.addPlugin(new DatabasesFlipperPlugin(this));
//            client.addPlugin(new SharedPreferencesFlipperPlugin(this));
////            client.addPlugin(new S1ThreadPlugin());
////            client.addPlugin(new BackStackFlipperPlugin(this));
//            client.start();
//        }

        initNetwork();
        initStetho();
        initCrashHandler();
        initLog();
        initPrefs();
        initComponent();
    }


    /**
     * 增加Activity
     *
     * @param act act
     */
    public void addActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<>();
        }
        allActivities.add(act);
    }

    /**
     * 移除Activity
     *
     * @param act act
     */
    public void removeActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 初始化网络模块组件
     */
    private void initComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .apiModule(new ApiModule())
                .appModule(new AppModule(this))
                .build();

    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    /**
     * 初始化sp
     */
    private void initPrefs() {
        PrefsUtils.init(this, getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

    /**
     * 初始化调试
     */
    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    /**
     * 开启网络监听
     */
    private void initNetwork() {
        NetworkUtils.startNetService(this);
    }

    /**
     * 获取Application
     *
     * @return BiliCopyApplication
     */
    public static BiliSoleilApplication getInstance() {
        return mContext;
    }


    /**
     * 初始化崩溃日志
     */
    private void initCrashHandler() {
        CrashHandler.getInstance().init(this);
    }


    /**
     * 初始化log
     */
    private void initLog() {
        LogUtils.init(this);
    }
}
