package com.scanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.scanner.tokendex.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScanResultActivity extends Activity {
    private static final int DEFAULT_IMAGE_RESOURCE = R.drawable.drawable_crumbs_of_sadness;

    private ImageView scanResultImage;
    private LinearLayout scanResultData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_result_view);
        scanResultImage = (ImageView) findViewById(R.id.scan_result_image);
        scanResultData = (LinearLayout) findViewById(R.id.scan_result_data);
        populateScanResult(getExtrasFromIntent(getIntent()));
    }

    private void resetResultData() {
        scanResultData.removeAllViews();
        scanResultImage.setImageResource(DEFAULT_IMAGE_RESOURCE);
    }

    private static Map<String, String> getExtrasFromIntent(Intent intent) {
        Map<String, String> extras = new LinkedHashMap<String, String>();
        for (String key : intent.getExtras().keySet()) {
            extras.put(key, intent.getStringExtra(key));
        }
        return extras;
    }

    private boolean populateScanResult(Map<String, String> scanResultMap) {
        LinearLayout row;
        resetResultData();
        if (!scanResultMap.isEmpty()) {
            for (Map.Entry<String, String> entry : scanResultMap.entrySet()) {
                if (entry.getKey().contains("TokenName")) {
                    setResultImage(entry.getValue());
                } else {
                    row = new LinearLayout(scanResultData.getContext());
                    addResultCell(row, entry.getKey());
                    addResultCell(row, entry.getValue());
                    scanResultData.addView(row);
                }
            }
        }
        return !scanResultMap.isEmpty();
    }

    private boolean setResultImage(String tokenName) {
        int resourceIdentifier = getResources().getIdentifier(getResourceNameFromTokenName(tokenName), "drawable", getPackageName());
        if (resourceIdentifier != 0) {
            scanResultImage.setImageResource(resourceIdentifier);
        }
        return resourceIdentifier != 0;
    }

    private static void addResultCell(ViewGroup viewGroup, String text) {
        TextView cell = new TextView(viewGroup.getContext());
        cell.setPadding(5, 5, 5, 5);
        cell.setTextColor(Color.BLACK);
        cell.setText(text);
        viewGroup.addView(cell);
    }

    private static String getResourceNameFromTokenName(String rawTokenName) {
        return "drawable_" + rawTokenName.toLowerCase().trim().replaceAll(" ", "_").replaceAll("\\W", "");
    }
}
