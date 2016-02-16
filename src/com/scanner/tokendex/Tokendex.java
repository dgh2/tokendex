package com.scanner.tokendex;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class Tokendex extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.scanner_view);
    }
}
