package com.kingwaytek.cpami.bykingTablet.app.track;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackEngine.TrackRecordingStatus;
import com.kingwaytek.cpami.bykingTablet.hardware.BatteryNotifier;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.TrackPoint;

public class TrackRecord extends Activity implements OnClickListener {

    private TrackEngine tEngine;
    private Track insTrack;

    private Button btnStart;
    private Button btnStop;
    private EditText etName;
    private EditText etDescription;
    private TextView tvName;
    private TextView tvDescription;
    private RatingBar DifficultyRatingBar;
    private int difficulty;
    private TextView trackRecordName;
    private TextView trackRecordNameEdit;
    private TextView trackRecordNameDisp;
    private TextView trackRecordNameDescription;
    private TextView tvRecordName;
    private TextView trackDescription;
    private TextView trackDescription2;
    private RatingBar ratingbar;
    private RatingBar ratingbar2;
    private static final int ID_NAME_DSIP = 1;

    /**
     * Called when this Activity started inheritance of Activity
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int intScreenX = dm.widthPixels;
        int intScreenY = dm.heightPixels;

        if (intScreenX == 320 && intScreenY == 427) {
            setContentView(R.layout.track_record_320_427);
        } else {
            setContentView(R.layout.track_record);
        }

        // Log.i("TrackRecord.java","intScreenX="+intScreenX+"  intScreenY="+intScreenY
        // );

        // btnStart = (Button) findViewById(R.id.track_record_start_button);
        // btnStop = (Button) findViewById(R.id.track_record_stop_button);
        // etName = (EditText) findViewById(R.id.track_record_name_edit);
        // etDescription = (EditText)
        // findViewById(R.id.track_record_description_edit);
        // tvName = (TextView) findViewById(R.id.track_record_name_disp);
        // tvDescription = (TextView)
        // findViewById(R.id.track_record_description_disp);

        // trackRecordName = (TextView) findViewById(R.id.track_record_name);
        // trackRecordNameEdit = (TextView)
        // findViewById(R.id.track_record_name_edit);
        // trackRecordNameDisp = (TextView)
        // findViewById(R.id.track_record_name_disp);
        // trackRecordNameDescription = (TextView)
        // findViewById(R.id.track_record_description);

        if (intScreenX == 320 && intScreenY == 427) {
            Log.i("TrackRecord.java", "intScreenX==320&&intScreenY==427");
            btnStart = (Button) findViewById(R.id.track_record_start_button);
            btnStop = (Button) findViewById(R.id.track_record_stop_button);
            etName = (EditText) findViewById(R.id.track_record_name_edit_320_427);
            etDescription = (EditText) findViewById(R.id.track_record_description_edit);
            tvName = (TextView) findViewById(R.id.track_record_name_disp_320_427);
            tvDescription = (TextView) findViewById(R.id.track_record_description_disp);
            trackDescription = (TextView) findViewById(R.id.track_record_description);
            trackDescription2 = (TextView) findViewById(R.id.track_record_description_2);
            ratingbar = (RatingBar) findViewById(R.id.ratingbar);
            ratingbar2 = (RatingBar) findViewById(R.id.ratingbar2);

            etName.setVisibility(TextView.VISIBLE);
            tvName.setVisibility(TextView.VISIBLE);
            trackDescription.setVisibility(TextView.VISIBLE);

            tvDescription.setId(ID_NAME_DSIP);

            // trackRecordName = (TextView)
            // findViewById(R.id.track_record_name);

            // trackRecordName.setLayoutParams( new
            // RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT ,
            // ViewGroup.LayoutParams.WRAP_CONTENT ));
            // trackRecordName.setText("軌跡名稱");
            // trackRecordName.setTextSize(R.dimen.textsize);
            // trackRecordName.setTextColor(R.color.yellow);
            // trackRecordName.setGravity(Gravity.CENTER_VERTICAL);
            // etName.setLayoutParams( new
            // RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT ,
            // ViewGroup.LayoutParams.WRAP_CONTENT ));
            //
            // tvName.setLayoutParams( new
            // RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,
            // ViewGroup.LayoutParams.WRAP_CONTENT ));
            // tvDescription.setLayoutParams( new
            // RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT ,
            // ViewGroup.LayoutParams.WRAP_CONTENT ));

        } else {
            Log.i("TrackRecord.java", "!=intScreenX==320&&intScreenY==427");
            btnStart = (Button) findViewById(R.id.track_record_start_button);
            btnStop = (Button) findViewById(R.id.track_record_stop_button);
            etName = (EditText) findViewById(R.id.track_record_name_edit);
            etDescription = (EditText) findViewById(R.id.track_record_description_edit);
            tvName = (TextView) findViewById(R.id.track_record_name_disp);
            tvDescription = (TextView) findViewById(R.id.track_record_description_disp);
            trackDescription = (TextView) findViewById(R.id.track_record_description);
            trackDescription2 = (TextView) findViewById(R.id.track_record_description_2);
            ratingbar = (RatingBar) findViewById(R.id.ratingbar);
            ratingbar2 = (RatingBar) findViewById(R.id.ratingbar2);

            etName.setVisibility(TextView.VISIBLE);
            tvName.setVisibility(TextView.VISIBLE);
            trackDescription.setVisibility(TextView.VISIBLE);

            tvDescription.setId(ID_NAME_DSIP);
        }

		/*
		 * btnStart = (Button) findViewById(R.id.track_record_start_button);
		 * btnStop = (Button) findViewById(R.id.track_record_stop_button);
		 * etName = (EditText) findViewById(R.id.track_record_name_edit);
		 * etDescription = (EditText)
		 * findViewById(R.id.track_record_description_edit); tvName = (TextView)
		 * findViewById(R.id.track_record_name_disp); tvDescription = (TextView)
		 * findViewById(R.id.track_record_description_disp); tvRecordName =
		 * (TextView) findViewById(R.id.track_record_name);
		 *
		 * etName.setVisibility(TextView.VISIBLE);
		 * tvRecordName.setVisibility(TextView.VISIBLE);
		 * tvName.setVisibility(TextView.VISIBLE);
		 */

        // imgDifficulty = new ImageView[5];
        // imgDifficulty[0] = (ImageView)
        // findViewById(R.id.track_record_diff_image1);
        // imgDifficulty[1] = (ImageView)
        // findViewById(R.id.track_record_diff_image2);
        // imgDifficulty[2] = (ImageView)
        // findViewById(R.id.track_record_diff_image3);
        // imgDifficulty[3] = (ImageView)
        // findViewById(R.id.track_record_diff_image4);
        // imgDifficulty[4] = (ImageView)
        // findViewById(R.id.track_record_diff_image5);
        DifficultyRatingBar = (RatingBar) findViewById(R.id.ratingbar);
        DifficultyExchange();

        difficulty = 1;
        tEngine = TrackEngine.getInstance();
        tEngine.InitializeGPS(this);
        insTrack = tEngine.getTrack();
    }

    @Override
    public void onResume() {
        super.onResume();

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        // for (ImageView imgdiff : imgDifficulty) {
        // imgdiff.setOnClickListener(this);
        // }

        if (tEngine.getRecordingStatus().equals(TrackRecordingStatus.STOPED)) {
            btnStart.setVisibility(Button.VISIBLE);
            btnStop.setVisibility(Button.GONE);
            tvName.setVisibility(TextView.GONE);
            tvDescription.setVisibility(TextView.GONE);
            etName.setVisibility(EditText.VISIBLE);
            etDescription.setVisibility(EditText.VISIBLE);
            trackDescription.setVisibility(TextView.VISIBLE);
            trackDescription2.setVisibility(TextView.GONE);
            ratingbar.setVisibility(TextView.VISIBLE);
            ratingbar2.setVisibility(TextView.GONE);

        } else {
            tvName.setText(insTrack.getName());
            tvDescription.setText(insTrack.getDescription());
            btnStart.setVisibility(Button.GONE);
            btnStop.setVisibility(Button.VISIBLE);
            tvName.setVisibility(TextView.VISIBLE);
            tvDescription.setVisibility(TextView.VISIBLE);
            etName.setVisibility(EditText.GONE);
            etDescription.setVisibility(EditText.GONE);
            trackDescription.setVisibility(TextView.GONE);
            trackDescription2.setVisibility(TextView.GONE);
            ratingbar.setVisibility(TextView.GONE);
            ratingbar2.setVisibility(TextView.GONE);

            // RelativeLayout layout=new RelativeLayout(this);
            // tvDescription.setId(ID_NAME_DSIP);
            // RelativeLayout.LayoutParams lp3 = new
            // RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            // ViewGroup.LayoutParams.WRAP_CONTENT);
            // lp3.addRule(RelativeLayout.BELOW,ID_NAME_DSIP );
            // trackDescription.setLayoutParams(lp3);
            // layout.addView(trackDescription);
        }
    }

    @Override
    public void onClick(View v) {
        // // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.track_record_start_button:
                btnStart_Click(v);
                break;
            case R.id.track_record_stop_button:
                btnStop_Click(v);
                break;
            // case R.id.track_record_diff_image1:
            // DifficultyExchange(0);
            // break;
            // case R.id.track_record_diff_image2:
            // DifficultyExchange(1);
            // break;
            // case R.id.track_record_diff_image3:
            // DifficultyExchange(2);
            // break;
            // case R.id.track_record_diff_image4:
            // DifficultyExchange(3);
            // break;
            // case R.id.track_record_diff_image5:
            // DifficultyExchange(4);
            // break;
            default:
                break;
        }
    }

    private void DifficultyExchange() {
        // private void DifficultyExchange(int arg) {
        // for (int i = imgDifficulty.length - 1; i > arg; i--) {
        // imgDifficulty[i].setImageResource(R.drawable.rate_star_big_off);
        // }
        // for (int i = 0; i <= arg; i++) {
        // imgDifficulty[i].setImageResource(R.drawable.rate_star_big_on);
        // }
        // difficulty = arg + 1;
        DifficultyRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                difficulty = (int) rating;
            }
        });
    }

    protected void btnStart_Click(Object sender) {

        if (sender == null || !sender.getClass().getName().equals("android.widget.Button")) {
            throw new IllegalArgumentException("sender is not valid.");
        }
        Log.i("TrackRecord", "Start Button Clicked. sender is : " + sender.getClass().getName());

        // When battery is low, do not record.
        BatteryNotifier bNote = BatteryNotifier.getInstance();
        if (bNote == null || bNote.getBatteryLevel() < 10) {

            UtilDialog uit = new UtilDialog(TrackRecord.this);
            uit.showDialog_route_plan_choice(getString(R.string.track_record_battery_low_prompt_text), null,
                    getString(R.string.dialog_close_button_text), null);

            return;
        }

        if (etName.getText().toString().trim().equals("")) {

            UtilDialog uit = new UtilDialog(TrackRecord.this);
            uit.showDialog_route_plan_choice(getString(R.string.track_record_name_input_prompt_text), null,
                    getString(R.string.dialog_close_button_text), null);

            return;
        }
        Time tmt = new Time();
        tmt.setToNow();
        insTrack = new Track(this);
        insTrack.setName(etName.getText().toString() + " " + tmt.year + "/" + (tmt.month + 1) + "/" + tmt.monthDay
                + "/" + tmt.hour + ":" + tmt.minute + ":" + tmt.second);
        insTrack.setDifficulty(difficulty);
        insTrack.setDescription("困難度:" + difficulty + "顆星。" + etDescription.getText().toString());
        insTrack.setCreateTime();

        tEngine.setTrack(insTrack);
        tEngine.Start();

        btnStart.setVisibility(Button.GONE);
        btnStop.setVisibility(Button.VISIBLE);
        Toast.makeText(this, getString(R.string.track_record_in_progress_text), Toast.LENGTH_LONG).show();
        finish();
    }

    protected void btnStop_Click(Object sender) {
        if (sender == null || !sender.getClass().getName().equals("android.widget.Button")) {
            throw new IllegalArgumentException("sender is not valid.");
        }
        Log.i("TrackRecord", "Stop Button Clicked. sender is : " + sender.getClass().getName());

        // if (etName.getText().toString().trim().equals("")) {
        // AlertDialogUtil.showMsgWithConfirm(this, "請輸入軌跡名稱", "關閉");
        // return;
        // }

        tEngine.Stop();
        // AlertDialogUtil.showMsgWithConfirm(this, "軌跡錄製已完成", "關閉");

        if (TrackPoint.getTrackPoints(this, insTrack.getID()).getCount() < 2) {
            Track.Erase(this, insTrack.getID());

            UtilDialog uit = new UtilDialog(TrackRecord.this) {
                @Override
                public void click_btn_1() {
                    btnStart.setVisibility(Button.VISIBLE);
                    btnStop.setVisibility(Button.GONE);
                    super.click_btn_1();
                    finish();

                }
            };
            uit.showDialog_route_plan_choice("軌跡", "此軌跡為空值，無法存檔!!", "確定", null);

        } else {
            btnStart.setVisibility(Button.VISIBLE);
            btnStop.setVisibility(Button.GONE);
            finish();
        }
    }
}