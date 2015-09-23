package com.lazycat.android.popularmovies.app.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.lazycat.android.popularmovies.app.R;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Intent getParentActivityIntent() {
        // add the clear top flag - which checks if the parent (main)
        // activity is already running and avoids recreating it
        return super.getParentActivityIntent()
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
