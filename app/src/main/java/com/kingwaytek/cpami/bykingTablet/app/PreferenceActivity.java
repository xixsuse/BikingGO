package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.VersionUpdate.Update;

/**
 * All preferences were moved to SettingManager.
 *
 * @author Vincent (2016/04/14).
 */
public class PreferenceActivity extends Activity implements OnItemClickListener {

    private Class<?>[] destinationList = new Class<?>[] {
            OperationSetting.class, NaviSetting.class, HealthManager.class,
            Update.class, About.class, MapDownloadActivity.class
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.preferencelist);

        GridView gridView = (GridView) this.findViewById(R.id.gridView1);

        PreferenceAdapter adapter = new PreferenceAdapter(this);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        Intent intent = new Intent();

        Class<?> destinationActivity = destinationList[arg2];

        intent.setClass(this, destinationActivity);

        this.startActivityForResult(intent, 600);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -10){
            Intent intent = new Intent();
            intent.putExtra("FINISH", 1);
            setResult(-10, intent);
            finish();
        }

        if (resultCode == RESULT_OK) {

        } else if (resultCode == RESULT_FIRST_USER) {
            setResult(RESULT_FIRST_USER);
            finish();
        }
    }
}