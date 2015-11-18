package com.dbsearch.app.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.dbsearch.app.R;
import com.dbsearch.app.adpater.BaseRecyclerViewAdapter;
import com.dbsearch.app.adpater.ClassAdapter;
import com.dbsearch.app.adpater.DrawerListAdapter;
import com.dbsearch.app.adpater.SimpleListAdapter;
import com.dbsearch.app.model.ClassModel;
import com.dbsearch.app.model.ClassModelParcelable;
import com.dbsearch.app.model.ClassType;
import com.dbsearch.app.module.DataModule;
import com.dbsearch.app.utils.ClassConfig;
import com.dbsearch.app.utils.JsonUtils;
import com.dbsearch.app.utils.PreferenceUtils;

import net.tsz.afinal.FinalDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

//    @InjectView(R.id.refresher)
//    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.recyclerView)
    RecyclerView recyclerView;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.left_drawer_listview)
    ListView mDrawerMenuListView;

    @InjectView(R.id.left_drawer)
    View drawerRootView;

    private ActionBarDrawerToggle mDrawerToggle;

    private SearchView searchView;

    private ClassAdapter recyclerAdapter;

    private int mCurrentClassType;

    private  List<String> classTypelist;

    private String sOAtuhReturn="";

    private String httpUrl = "";

    private String sLast="";

    private List<ClassModel> classList=new ArrayList<ClassModel>();

    private boolean isSearching=false;

    private final int REFRESH_ADAPTER=0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initDrawerView();
        initRecyclerView();
        EventBus.getDefault().register(this);
        new Thread() {
            public void run() {
                while (true) {
                    try {
                        getSearchResult();
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void onEvent(Integer event) {
    }

    @Override
    public void onStart() {
        super.onStart();
        setMenuListViewGravity(Gravity.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new DataModule());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        if (toolbar != null){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openOrCloseDrawer();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //searchItem.expandActionView();
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ComponentName componentName = getComponentName();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint(getString(R.string.search_by_name));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if (s.equals("") || s == null) {
                    isSearching = false;
                } else {
                    isSearching = true;
                }

                switch (mCurrentClassType) {
                    case ClassConfig.CLASS_BOOK_TYPE:
                        httpUrl = getString(R.string.apibook);
                        break;
                    case ClassConfig.CLASS_MUSIC_TYPE:
                        httpUrl = getString(R.string.apimusic);
                        break;
                    case ClassConfig.CLASS_MOVIE_TYPE:
                        httpUrl = getString(R.string.apimovie);
                        break;
                    default:
                        break;
                }
                httpUrl += java.net.URLEncoder.encode(s) + "&count=5";
                return true;
            }
        });
        return true;
    }

    private void getSearchResult()
    {
        if(!isSearching||sLast==httpUrl)
        {
            return;
        }
        sLast=httpUrl;
        StringBuilder resultData = new StringBuilder("");
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            InputStreamReader isr = new InputStreamReader(urlConn.getInputStream(),"utf-8");

            BufferedReader buffer = new BufferedReader(isr);
            String inputLine = null;

            while ((inputLine = buffer.readLine()) != null) {
                resultData.append(inputLine);
                resultData.append("\n");
            }
            buffer.close();
            isr.close();
            urlConn.disconnect();
            sOAtuhReturn = resultData.toString();

            switch (mCurrentClassType) {
                case ClassConfig.CLASS_BOOK_TYPE:
                    try {
                        classList.clear();
                        JSONArray bookArray = new JSONObject(sOAtuhReturn).getJSONArray("books");
                        for (int i = 0; i < bookArray.length(); i++) {
                            JSONObject bookObject = bookArray.getJSONObject(i);
                            ClassModel classModel=new ClassModel();

                            String sTitle=bookObject.getString("title");
                            if(sTitle.length()>10)
                                sTitle=sTitle.substring(0,10)+"...";
                            classModel.setTitle(sTitle);

                            String sAuthor="";
                            for (int aNum=0;aNum<bookObject.getJSONArray("author").length();aNum++)
                            {
                                if(sAuthor.length()>10) {
                                    sAuthor += "....";
                                    break;
                                }
                                sAuthor+=bookObject.getJSONArray("author").getString(aNum)+",";
                            }
                            if(sAuthor.length()>0) {
                                sAuthor = sAuthor.substring(0, sAuthor.length() - 1);
                            }

                            classModel.setAuthor(sAuthor);
                            classModel.setSummary(bookObject.getString("summary"));
                            classModel.setPrice(bookObject.getString("price"));
                            classModel.setPic(getBitmapFromURL(new JSONObject(bookObject.getString("images")).getString("small")));
                            classList.add(classModel);
                        }
                    } catch (JSONException e) {
                        classList.clear();
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case ClassConfig.CLASS_MUSIC_TYPE:
                    try {
                        classList.clear();
                        JSONArray musicArray = new JSONObject(sOAtuhReturn).getJSONArray("musics");
                        for (int i = 0; i < musicArray.length(); i++) {
                            JSONObject musicObject = musicArray.getJSONObject(i);
                            ClassModel classModel=new ClassModel();

                            String sTitle=musicObject.getString("title");
                            if(sTitle.length()>10)
                                sTitle=sTitle.substring(0,10)+"...";
                            classModel.setTitle(sTitle);

                            String sAuthor="";
                            for (int aNum=0;aNum<musicObject.getJSONArray("author").length();aNum++)
                            {
                                if(sAuthor.length()>10) {
                                    sAuthor += "....";
                                    break;
                                }
                                sAuthor+=new JSONObject(musicObject.getJSONArray("author").getString(aNum)).getString("name")+",";
                            }
                            if(sAuthor.length()>0) {
                                sAuthor = sAuthor.substring(0, sAuthor.length() - 1);
                            }

                            classModel.setAuthor(sAuthor);
                            classModel.setSummary(new JSONObject(musicObject.getString("attrs")).getJSONArray("tracks").getString(0));
                            classModel.setPic(getBitmapFromURL(musicObject.getString("image")));
                            classList.add(classModel);
                        }
                    } catch (JSONException e) {
                        classList.clear();
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                case ClassConfig.CLASS_MOVIE_TYPE:
                    try {
                        classList.clear();
                        JSONArray movieArray = new JSONObject(sOAtuhReturn).getJSONArray("subjects");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);
                            ClassModel classModel=new ClassModel();

                            String sTitle=movieObject.getString("title");
                            if(sTitle.length()>10)
                                sTitle=sTitle.substring(0,10)+"...";
                            classModel.setTitle(sTitle);

                            String sAuthor="";
                            for (int aNum=0;aNum<movieObject.getJSONArray("casts").length();aNum++)
                            {
                                if(sAuthor.length()>10) {
                                    sAuthor += "....";
                                    break;
                                }
                                sAuthor+=new JSONObject(movieObject.getJSONArray("casts").getString(aNum)).getString("name")+",";
                            }
                            if(sAuthor.length()>0) {
                                sAuthor = sAuthor.substring(0, sAuthor.length() - 1);
                            }

                            classModel.setAuthor(sAuthor);
                            //classModel.setSummary(movieObject.getString("summary"));
                            classModel.setPic(getBitmapFromURL(new JSONObject(movieObject.getString("images")).getString("small")));
                            classList.add(classModel);
                        }
                    } catch (JSONException e) {
                        classList.clear();
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

            handler.sendEmptyMessage(REFRESH_ADAPTER);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_ADAPTER:
                    recyclerAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    protected Bitmap getBitmapFromURL(String sUrl) throws IOException {
        if("".equals(sUrl))
        {
            return null;
        }
        URL url = new URL(sUrl);
        URLConnection conn = url.openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        Bitmap bm = BitmapFactory.decodeStream(bis);
        bis.close();
        is.close();
        return bm;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Intent intent;
        switch (item.getItemId()){
            case R.id.about:
                return true;
            case android.R.id.home:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(drawerRootView)){
            mDrawerLayout.closeDrawer(drawerRootView);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initToolbar(){
        super.initToolbar(toolbar);
    }

    private void initDrawerListView(){
            classTypelist = Arrays.asList(getResources().getStringArray(R.array.drawer_content));
        SimpleListAdapter adapter = new DrawerListAdapter(this, classTypelist);
        mDrawerMenuListView.setAdapter(adapter);
        mDrawerMenuListView.setItemChecked(mCurrentClassType, true);
        toolbar.setTitle(classTypelist.get(mCurrentClassType));
    }

    private void initDrawerView() {
        initDrawerListView();
        mDrawerMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerMenuListView.setItemChecked(position, true);
                openOrCloseDrawer();
                mCurrentClassType = position;
                changeToSelectNoteType(mCurrentClassType);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                toolbar.setTitle(R.string.app_name);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                toolbar.setTitle(classTypelist.get(mCurrentClassType));
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setScrimColor(getColor(R.color.drawer_scrim_color));
    }

    private void initRecyclerView(){
        initItemLayout();
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new ClassAdapter(classList);
        recyclerAdapter.setOnInViewClickListener(R.id.notes_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<ClassModel>() {
                    @Override
                    public void OnClickListener(View parentV, View v, Integer position, ClassModel values) {
                        super.OnClickListener(parentV, v, position, values);
                        startClassActivity(classList.get(position));
                    }
                });
//        recyclerAdapter.setOnInViewClickListener(R.id.note_more,
//                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<Note>() {
//                    @Override
//                    public void OnClickListener(View parentV, View v, Integer position, Note values) {
//                        super.OnClickListener(parentV, v, position, values);
//                        showPopupMenu(v, values);
//                    }
//                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        recyclerView.setAdapter(recyclerAdapter);

//        refreshLayout.setColorSchemeColors(getColorPrimary());
//        refreshLayout.setOnRefreshListener(this);
    }

    private void changeToSelectNoteType(int type){
        switch (type)
        {
            case ClassConfig.CLASS_BOOK_TYPE:
                recyclerAdapter.setList(classList);
                break;
            case ClassConfig.CLASS_MUSIC_TYPE:
                recyclerAdapter.setList(classList);
                break;
            case ClassConfig.CLASS_MOVIE_TYPE:
                recyclerAdapter.setList(classList);
                break;
            default:
                break;
        }
    }

    private void openOrCloseDrawer() {
        if (mDrawerLayout.isDrawerOpen(drawerRootView)) {
            mDrawerLayout.closeDrawer(drawerRootView);
        } else {
            mDrawerLayout.openDrawer(drawerRootView);
        }
    }


    private void startClassActivity(ClassModel classModel){
        Intent intent = new Intent(this, DetailsActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("ClassModel",classModel);
//        EventBus.getDefault().postSticky(value);
        ClassModelParcelable classModelParcelable=new ClassModelParcelable(classModel);
        intent.putExtra("ClassModel", classModelParcelable);
        startActivity(intent);
    }

    private void setMenuListViewGravity(int gravity){
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerRootView.getLayoutParams();
        params.gravity = gravity;
        drawerRootView.setLayoutParams(params);
    }

    private void initItemLayout(){
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onRefresh() {
//        refreshLayout.setRefreshing(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // ��õ�ǰ�õ������View��һ������¾���EditText������������ǹ켣�����ʵ�尸�����ƶ����㣩
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // ���EditText���¼�����������
                return false;
            } else {
                return true;
            }
        }
        // ������㲻��EditText����ԣ������������ͼ�ջ����꣬��һ�����㲻��EditView�ϣ����û��ù켣��ѡ�������Ľ���
        return false;
    }

    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
