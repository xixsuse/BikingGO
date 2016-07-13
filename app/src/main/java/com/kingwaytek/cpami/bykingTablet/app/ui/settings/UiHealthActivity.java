package com.kingwaytek.cpami.bykingTablet.app.ui.settings;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.SettingManager;

/**
 * 健康管理頁面
 *
 * @author Vincent (2016/7/13)
 */
public class UiHealthActivity extends BaseActivity {

    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;

    private static final int SEEK_TYPE_AGE = 0;
    private static final int SEEK_TYPE_HEIGHT = 1;
    private static final int SEEK_TYPE_WEIGHT = 2;

    private static final int MINI_VALUE_AGE = 7;
    private static final int MINI_VALUE_HEIGHT = 80;
    private static final int MINI_VALUE_WEIGHT = 20;

    private RadioGroup radioGroup;
    private RadioButton radio_male;
    private RadioButton radio_female;
    private SeekBar seekBar_age;
    private SeekBar seekBar_height;
    private SeekBar seekBar_weight;
    private TextView text_yearsOld;
    private TextView text_cm;
    private TextView text_kg;

    @Override
    protected void init() {
        setDefaultValue();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.preference_health);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_health;
    }

    @Override
    protected void findViews() {
        radioGroup = (RadioGroup) findViewById(R.id.radio_genderGroup);
        radio_male = (RadioButton) findViewById(R.id.radio_male);
        radio_female = (RadioButton) findViewById(R.id.radio_female);
        seekBar_age = (SeekBar) findViewById(R.id.seekBar_age);
        seekBar_height = (SeekBar) findViewById(R.id.seekBar_height);
        seekBar_weight = (SeekBar) findViewById(R.id.seekBar_weight);
        text_yearsOld = (TextView) findViewById(R.id.text_yearsOld);
        text_cm = (TextView) findViewById(R.id.text_cm);
        text_kg = (TextView) findViewById(R.id.text_kg);
    }

    @Override
    protected void setListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radio_male.getId())
                    SettingManager.setUserSex(GENDER_MALE);
                else if (checkedId == radio_female.getId())
                    SettingManager.setUserSex(GENDER_FEMALE);
            }
        });

        seekBar_age.setOnSeekBarChangeListener(getSeekBarListener(SEEK_TYPE_AGE));
        seekBar_height.setOnSeekBarChangeListener(getSeekBarListener(SEEK_TYPE_HEIGHT));
        seekBar_weight.setOnSeekBarChangeListener(getSeekBarListener(SEEK_TYPE_WEIGHT));
    }

    private void setDefaultValue() {
        int gender = SettingManager.getUserSex();

        radio_male.setChecked(gender == GENDER_MALE);
        radio_female.setChecked(gender == GENDER_FEMALE);

        seekBar_age.setProgress(SettingManager.getUserAge());
        seekBar_height.setProgress(SettingManager.getUserHeight());
        seekBar_weight.setProgress(SettingManager.getUserWeight());

        setAgeText(SettingManager.getUserAge());
        setHeightText(SettingManager.getUserHeight());
        setWeightText(SettingManager.getUserWeight());
    }

    private void setAgeText(int age) {
        text_yearsOld.setText(getString(R.string.health_years_old, age));
    }

    private void setHeightText(int height) {
        text_cm.setText(getString(R.string.health_centimeter, height));
    }

    private void setWeightText(int weight) {
        text_kg.setText(getString(R.string.health_kilogram, weight));
    }

    private SeekBar.OnSeekBarChangeListener getSeekBarListener(final int seekBarType) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBarType) {
                    case SEEK_TYPE_AGE:
                        if (progress < MINI_VALUE_AGE)
                            seekBar_age.setProgress(MINI_VALUE_AGE);
                        else
                            setAgeText(progress);
                        break;

                    case SEEK_TYPE_HEIGHT:
                        if (progress < MINI_VALUE_HEIGHT)
                            seekBar_height.setProgress(MINI_VALUE_HEIGHT);
                        else
                            setHeightText(progress);
                        break;

                    case SEEK_TYPE_WEIGHT:
                        if (progress < MINI_VALUE_WEIGHT)
                            seekBar_weight.setProgress(MINI_VALUE_WEIGHT);
                        else
                            setWeightText(progress);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch (seekBarType) {
                    case SEEK_TYPE_AGE:
                        SettingManager.setUserAge(seekBar.getProgress());
                        break;

                    case SEEK_TYPE_HEIGHT:
                        SettingManager.setUserHeight(seekBar.getProgress());
                        break;

                    case SEEK_TYPE_WEIGHT:
                        SettingManager.setUserWeight(seekBar.getProgress());
                        break;
                }
            }
        };
    }
}