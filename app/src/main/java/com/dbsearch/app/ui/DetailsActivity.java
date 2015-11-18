package com.dbsearch.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbsearch.app.R;
import com.dbsearch.app.model.ClassModel;
import com.dbsearch.app.model.ClassModelParcelable;
import com.dbsearch.app.module.DataModule;

import net.tsz.afinal.FinalDb;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

public class DetailsActivity extends BaseActivity{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.label_title_text)
    TextView labelTitleText;

    @InjectView(R.id.label_author_text)
    TextView labelAuthorText;

    @InjectView(R.id.label_content_text)
    TextView labContentText;

    @InjectView(R.id.class_pic)
    ImageView imgClass;

    @Inject
    FinalDb finalDb;

    private MenuItem doneMenuItem;

    private ClassModel classModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent(getIntent());
        initToolbar();
        initEditText();
//        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_details;
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new DataModule());
    }

    public void onEventMainThread(ClassModel classModel) {
    }

    private void parseIntent(Intent intent){
        if (intent != null){
            ClassModelParcelable classModelParcelable=(ClassModelParcelable)intent.getParcelableExtra("ClassModel");
            classModel=classModelParcelable.getClassModel();
//            classModel = (ClassModel)intent.getSerializableExtra("ClassModel");
        }
    }

    private void initToolbar(){
        super.initToolbar(toolbar);
        toolbar.setTitle(R.string.details);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (doneMenuItem.isVisible()) {
                    return;
                }
                finish();
            }
        });
    }

    private void initEditText(){

        labelTitleText.setText(classModel.getTitle());
        labelAuthorText.setText(classModel.getAuthor());
        labContentText.setText(classModel.getSummary());
        imgClass.setImageBitmap(classModel.getPic());


//        labelEditText.setOnFocusChangeListener(new SimpleOnFocusChangeListener());
//        contentEditText.setOnFocusChangeListener(new SimpleOnFocusChangeListener());
//
//        labelEditText.addTextChangedListener(new SimpleTextWatcher());
//        contentEditText.addTextChangedListener(new SimpleTextWatcher());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        doneMenuItem = menu.getItem(0);
        doneMenuItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (doneMenuItem.isVisible()){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void hideKeyBoard(EditText editText){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
