package com.darkeyes.tricks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class SettingsActivity extends AppCompatActivity
        implements OnSharedPreferenceChangeListener {

    private static final String GITHUB_URL = "https://github.com/askfname/dark_tricks";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        prefs = getSharedPreferences(
                "com.darkeyes.tricks_shared",
                Context.MODE_WORLD_READABLE
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.action_bar);

            View githubButton = getSupportActionBar()
                    .getCustomView()
                    .findViewById(R.id.github_button);

            githubButton.setOnClickListener(view ->
                    startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(GITHUB_URL)
                            )
                    )
            );
        }

        updateSystemBars();
    }

    /**
     * 根据系统当前深浅色模式，
     * 同步状态栏和导航栏图标颜色。
     */
    private void updateSystemBars() {
        boolean isDarkMode =
                (getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        getWindow(),
                        getWindow().getDecorView()
                );

        if (controller == null) {
            return;
        }

        /*
         * 浅色模式：
         *   状态栏图标 = 深色
         *   导航栏图标 = 深色
         *
         * 深色模式：
         *   状态栏图标 = 浅色
         *   导航栏图标 = 浅色
         */
        controller.setAppearanceLightStatusBars(!isDarkMode);
        controller.setAppearanceLightNavigationBars(!isDarkMode);
    }

    @Override
    protected void onResume() {
        super.onResume();

        prefs.registerOnSharedPreferenceChangeListener(this);

        // 每次进入 Activity 时重新同步系统栏
        updateSystemBars();
    }

    @Override
    protected void onPause() {
        super.onPause();

        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences,
            String key) {

        Object pref = sharedPreferences.getAll().get(key);

        Intent intent =
                new Intent("com.darkeyes.tricks.PREFERENCES");

        intent.putExtra("preference", key);

        if (pref instanceof Boolean) {
            intent.putExtra(
                    "value",
                    sharedPreferences.getBoolean(key, false)
            );
        } else {
            intent.putExtra(
                    "value",
                    sharedPreferences.getString(
                            key,
                            key.equals("trick_customCarrierText")
                                    ? ""
                                    : "0"
                    )
            );
        }

        sendBroadcast(intent);
    }
}