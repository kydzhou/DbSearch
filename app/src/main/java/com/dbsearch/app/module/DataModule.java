package com.dbsearch.app.module;

import android.content.Context;

import com.dbsearch.app.ui.DetailsActivity;
import com.dbsearch.app.ui.MainActivity;

import net.tsz.afinal.FinalDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                MainActivity.class,
                DetailsActivity.class,
        },
        addsTo = AppModule.class,
        library = true
)
public class DataModule {

    @Provides @Singleton
    FinalDb.DaoConfig provideDaoConfig(Context context) {
        FinalDb.DaoConfig config = new FinalDb.DaoConfig();
        config.setDbName("notes.db");
        config.setDbVersion(1);
        config.setDebug(true);
        config.setContext(context);
        return config;
    }

    @Provides @Singleton
    FinalDb provideFinalDb(FinalDb.DaoConfig config) {
        return FinalDb.create(config);
    }
}
