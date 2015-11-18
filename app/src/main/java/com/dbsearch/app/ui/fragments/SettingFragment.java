package com.dbsearch.app.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.jenzz.materialpreference.CheckBoxPreference;
import com.jenzz.materialpreference.Preference;
import com.jenzz.materialpreference.SwitchPreference;
import com.dbsearch.app.R;
import com.dbsearch.app.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingFragment extends BaseFragment{

    public static final String PREFERENCE_FILE_NAME = "note.settings";

    private  List<String> accountItems = new ArrayList<>();

    private PreferenceUtils preferenceUtils;

    public static SettingFragment newInstance(){
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils = PreferenceUtils.getInstance(getActivity());
        getPreferenceManager().setSharedPreferencesName(PREFERENCE_FILE_NAME);
        initFeedbackPreference();
    }

    public SettingFragment() {
        super();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,  android.preference.Preference preference) {
        if (preference == null)
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        String key = preference.getKey();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    private void initFeedbackPreference(){
        Uri uri = Uri.parse("mailto:lgpszu@163.com");
        final Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (infos == null || infos.size() <= 0){
            return;
        }

    }
    private void giveFavor(){
        try{
            Uri uri = Uri.parse("market://details?id="+ getActivity().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            e.printStackTrace();
        }
    }
}
