package com.kingwaytek.cpami.bykingTablet.app.Infomation;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.UtilDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DetailActivity extends Activity {

    /* Facebook permissions */
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

    /* Information object */
    private InfomationObject object;

    /* Widgets */
    private TextView text_name;
    private TextView text_description;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_infomation_detail);

        this.findViews();

        Bundle extra = this.getIntent().getExtras();

        if (extra != null) {

            object = extra.getParcelable("Info");

            this.fillData();
        }

		/* Print out the key hash FB */
        try {

            PackageInfo info = getPackageManager().getPackageInfo("com.kingwaytek.cpami.bykingTablet",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    /* Initial views */
    private void findViews() {
        text_name = (TextView) this.findViewById(R.id.textView1);
        text_description = (TextView) this.findViewById(R.id.textView2);
    }

    /* Fill data to widgets */
    private void fillData() {
        text_name.setText(object.getName());
        text_description.setText(object.getDescription() + "\n\n");

        ((TextView) this.findViewById(R.id.textView4)).setText(object.getStart());

        ((TextView) this.findViewById(R.id.textView6)).setText(object.getEnd());

        // String address = object.getLocation() + object.getAdd();
        String address = object.getAdd();

        String trimAddress = address.replace(" ", "");

        ((TextView) this.findViewById(R.id.textView8)).setText(trimAddress);

    }

    /* Share to Facebook button pressed */
    public void shareToFacebook(View view) {

        Session.openActiveSession(this, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {

                if (session.isOpened()) {

                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {

                            if (user != null) {
                                DetailActivity.this.startPublish();
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    private void startPublish() {
        if (object == null) {
            return;
        }

        Session session = Session.getActiveSession();

        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this,
                        PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }
        }

        String sahre_name = object.getName();
        String share_description = object.getOrg();
        String share_link = object.getWebsite();

        Bundle postParams = new Bundle();
        postParams.putString("name", sahre_name);
        postParams.putString("description", share_description);
        postParams.putString("picture", share_link);

        if (share_link.length() < 1) {
            postParams.putString("link", "https://www.google.com.tw/#q=" + sahre_name);
        }

        WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(DetailActivity.this, Session.getActiveSession(),
                postParams)).setOnCompleteListener(new OnCompleteListener() {

            @Override
            public void onComplete(Bundle values, FacebookException error) {

                if (error == null) {
                    final String postId = values.getString("post_id");

                    if (postId != null) {
                        DetailActivity.this.showAlert("發佈成功！", "Facebok 分享完成！");
                    } else {
                        // User clicked the Cancel button
                        Log.d("Facebook", "share cancel by user");
                    }
                } else if (error instanceof FacebookOperationCanceledException) {
                    // User clicked the "x" button
                    Log.d("Facebook", "share cancel by user");
                } else {
                    // Generic, ex: network error
                    DetailActivity.this.showAlert("發佈失敗！", "請檢查網路狀態並稍後再試");
                }
            }

        }).build();

        feedDialog.show();
    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    /* Alert */
    private void showAlert(String title, String message) {

        UtilDialog uit = new UtilDialog(DetailActivity.this);
        uit.showDialog_route_plan_choice(title, message, "確定", null);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (this.getIntent().getStringExtra("GCM") != null) {
                Intent intent = new Intent();
                intent.setClass(this, InformationActivity.class);
                startActivity(intent);
                finish();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}