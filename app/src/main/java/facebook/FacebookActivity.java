package facebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.kingwaytek.cpami.bykingTablet.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class FacebookActivity extends Activity {

    private static final List<String> PERMISSIONS = Arrays
            .asList("publish_actions");

    private UiLifecycleHelper uiHelper;

    private LinearLayout layout_frame;
    private ImageView img_share;
    private EditText txt_message;
    private LoginButton btn_fbLogin;

    private ProgressDialog dialog;

    private String filePath;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		/* Print out the key hash FB */
        try {

            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ivan.facebook35test", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());

                Log.d("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        this.setContentView(R.layout.activtiy_facebook);

        this.findView();

        Bundle extra = this.getIntent().getExtras();

        if (extra != null) {

            filePath = extra.getString("FILEPATH");

            bitmap = ImageTools.getBitmap(filePath, 8);

            img_share.setImageBitmap(bitmap);

            uiHelper = new UiLifecycleHelper(this, callback);
            uiHelper.onCreate(savedInstanceState);

            Session session = Session.getActiveSession();

            if (!session.isOpened()) {
                btn_fbLogin.performClick();
            }
        }
    }

    /* Initial views */
    private void findView() {
        layout_frame = (LinearLayout) this.findViewById(R.id.linearLayout);
        img_share = (ImageView) this.findViewById(R.id.imageView1);
        txt_message = (EditText) this.findViewById(R.id.editText1);
        btn_fbLogin = (LoginButton) this.findViewById(R.id.login_button);

        btn_fbLogin.setPublishPermissions(PERMISSIONS);
        btn_fbLogin.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);

        DeviceDisplayObject object = DisplayTools
                .getDeviceDisplayInfomation(this);

        int width = (int) (object.getWidth() * 0.8);

        int height = (int) (object.getHeight() * 0.4);

        LayoutParams params = new LayoutParams(width, height);

        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        layout_frame.setLayoutParams(params);
    }

    /* Cancel share */
    public void facebookShareCancelPressed(View view) {

        this.finish();
    }

    /* Confirm button */
    public void facebookShareConfirmPressed(View view) {

        this.postImage2Facebook();
    }

    /* UiLifecycleHelper callback */
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {

            FacebookActivity.this.onSessionStateChange(session, state,
                    exception);
        }
    };

    /* FB Session state */
    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {

        if (state.isClosed()) {

            Toast.makeText(this, "Facebook 登入失敗", Toast.LENGTH_LONG).show();

            this.finish();
        }
    }

    /** Post image to Facebook */
    private void postImage2Facebook() {

        String common = txt_message.getText().toString();

        File file = new File(filePath);

        Request photoRequest;

        try {

            dialog = ProgressDialog.show(this, "圖片發佈中", "請稍候...");

            photoRequest = Request.newUploadPhotoRequest(
                    Session.getActiveSession(), file,
                    uploadPhotoRequestCallback);

            Bundle params = photoRequest.getParameters();
            params.putString("message", common);
            photoRequest.executeAsync();

        } catch (FileNotFoundException e) {

            dialog.dismiss();

            DisplayTools.showSimpleDialog(this, "找不到您的圖片檔案", "請確認圖片位置");
        }
    }

    Request.Callback uploadPhotoRequestCallback = new Request.Callback() {
        @Override
        public void onCompleted(Response response) {

            if (dialog != null) {
                dialog.dismiss();
            }

            if (response.getError() != null) {

                DisplayTools.showSimpleDialog(FacebookActivity.this, "圖片發佈失敗",
                        "請重新發佈");

            } else {

                Toast.makeText(FacebookActivity.this, "圖片發佈成功",
                        Toast.LENGTH_LONG).show();

                FacebookActivity.this.finish();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        Session session = Session.getActiveSession();

        if (session != null) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    /** Recycle bitmap when destroy */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        uiHelper.onDestroy();

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    /* Back from FB */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
}