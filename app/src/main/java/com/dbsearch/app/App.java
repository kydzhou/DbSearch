package com.dbsearch.app;

import android.app.Application;

import com.dbsearch.app.BuildConfig;
import com.dbsearch.app.module.AppModule;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.Bmob;
import dagger.ObjectGraph;

public class App extends Application{
    private ObjectGraph objectGraph;
    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
        Bmob.initialize(this, BuildConfig.BMOB_KEY);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }
}
