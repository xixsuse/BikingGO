package com.kingwaytek.cpami.bykingTablet.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.view.IconListView;

/**
 * 由於Android的Activity stack僅適用於依序往返的流程中。<br/>
 * 例如： A->B->C->D，從D開始依序按返回鍵，則流程為D->C->B->A。<br/>
 * 
 * 若假設從B開始為設定流程某參數的流程，則於D流程確定或放棄設定後，應返回A。<br/>
 * 基於這樣的情況下，覆寫onActivityResult()方法來達成。<br/>
 * 
 * 當onActivityResult()被呼叫時，resultCode為RESULT_FIRST_USER，代表此流程已收到結果，
 * 可於onActivityResult()中作處理，以getExtra()取的所需資料。否則應執行super.onActivityResult()。<br/>
 * <br/>
 * 要設定回傳內容時，必須以setResult(RESULT_FIRST_USER)設定結果，並以putExtraXXX()儲存回傳資料。<br/>
 * <br/>
 * You must override getMenuResource() and onItemClick()methods if you want to
 * show menu and receive events when items on menu clicked.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public abstract class FlowNodeActivity extends Activity {

    /** Result key of specified activity for backing to directly */
    public static final String RESULT_EXTRA_BACK_TO = "RESULT_EXTRA_BACK_TO";

    /** Result code for backing to specified activity directly */
    public static final int RESULT_BACK_TO = 2;

    public View menu;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // set customized title bar
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//        setContentView(R.layout.title_bar);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

//        setTitle(getString(R.string.title_default));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setTitle(int titleId) {
        ((TextView) findViewById(R.id.title_text)).setText(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        ((TextView) findViewById(R.id.title_text)).setText(title);
    }

    @Override
    public void setTitleColor(int textColor) {
        ((TextView) findViewById(R.id.title_text)).setTextColor(textColor);
    }

    /**
     * Show or hide customized menu if keyCode equals to KeyEvent.KEYCODE_MENU,
     * or propagate it instead.
     * 
     * @param keyCode
     *            The value in event.getKeyCode().
     * @param event
     *            Description of the key event.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (menu == null) {
                int resId = getMenuResource();

                if (resId > 0) {
                    LayoutInflater inflater =
                            (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                    View menuLayout = inflater.inflate(resId, null);

                    menuLayout.setAnimation(
                            AnimationUtils.loadAnimation(this, R.anim.show_menu));

                    ((IconListView) menuLayout.findViewById(R.id.list)).
                            setOnItemClickListener(new OnItemClickListener() {

                        public void onItemClick(
                                AdapterView<?> parent,
                                View view,
                                int position,
                                long id) {

                            onMenuItemClick(parent, view, position, id);
                        }
                    });

                    addContentView(menuLayout, new RelativeLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));

                    menu = menuLayout;
                }
            } else {
             // remove menu
//            	if(menu.getParent()!=null){
                ((ViewGroup) menu.getParent()).removeView(menu);
//            	}
                menu = null;
            }

            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && menu != null) {
            // remove menu
            ((ViewGroup) menu.getParent()).removeView(menu);
            menu = null;
            
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * If resultCode is not RESULT_CANCELED or RESULT_OK, call finish() to
     * destroy self.<br/>
     * Override this method if you want to deal with the result.
     * 
     * @param requestCode
     *            The integer request code originally supplied to
     *            startActivityForResult(), allowing you to identify who this
     *            result came from.
     * 
     * @param resultCode
     *            The integer result code returned by the child activity through
     *            its setResult().
     * 
     * @param data
     *            An Intent, which can return result data to the caller (various
     *            data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_BACK_TO:
                Class<?> clazz = (Class<?>) data.getExtras().get(RESULT_EXTRA_BACK_TO);

                if (clazz == null || !clazz.equals(getClass())) {
                    getIntent().putExtras(data);
                    backTo(clazz);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Returns the resource ID of menu. It will be called when "menu" button
     * clicked.
     * 
     * @return The resource ID.
     */
    public int getMenuResource() {
        return 0;
    }

    /**
     * Returns the text of title.
     * 
     * @return The text of title.
     */
    public CharSequence getTitleText() {
        return ((TextView) findViewById(R.id.title_text)).getText();
    }

    /**
     * Returns the icon of title.
     * 
     * @return The icon of title.
     */
//    public ImageView getTitleIcon() {
//        return ((ImageView) findViewById(R.id.title_icon));
//    }

    /**
     * Sets a bitmap as the content of title icon.
     * 
     * @param bm
     *            The bitmap to set.
     */
//    public void setTitleIcon(Bitmap bm) {
//        ((ImageView) findViewById(R.id.title_icon)).setImageBitmap(bm);
//    }

    /**
     * Sets a drawable as the content of title icon.
     * 
     * @param drawable
     *            The drawable to set.
     */
//    public void setTitleIcon(Drawable drawable) {
//        ((ImageView) findViewById(R.id.title_icon)).setImageDrawable(drawable);
//    }

    /**
     * Sets a drawable as the content of title icon.
     * 
     * @param resId
     *            The resource identifier of the the drawable.
     */
//    public void setTitleIcon(int resId) {
//        ((ImageView) findViewById(R.id.title_icon)).setImageResource(resId);
//    }

    /**
     * Launch a new activity by calling startActivity(). If clearTop is set to
     * true, current activity will be the root of activity stack.
     * 當不需要取得下一活動的結果時，呼叫此方法來啟動下一活動
     * 
     * @param clazz
     *            The component class of activity that is to be used for the
     *            intent.
     * @param clearTop
     *            set true to add flag of Intent.FLAG_ACTIVITY_CLEAR_TOP.
     */
    public void goTo(Class<?> clazz, boolean clearTop) {
        Intent intent = new Intent(this, clazz);

        if (clearTop)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    /**
     * Launch an activity which you need to get a result when it is finished by
     * calling startActivityForResult(). If clearTop is set to true, current
     * activity will be the root of activity stack.<br/>
     * 當需要取得下一活動的結果時，呼叫此方法來啟動下一活動
     * 
     * @param clazz
     *            The component class of activity that is to be used for the
     *            intent.
     * @param clearTop
     *            set true to add flag of Intent.FLAG_ACTIVITY_CLEAR_TOP.
     * @param requestCode
     *            The code to identify the request.
     */
    public void goToForResult(Class<?> clazz, boolean clearTop, int requestCode) {
        Intent intent = new Intent(this, clazz);

        if (clearTop)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivityForResult(intent, requestCode);
    }

    /**
     * Back to the specified activity.
     * 
     * @param clazz
     *            class of activity to back to.
     */
    public void backTo(Class<?> clazz) {
        getIntent().putExtra(RESULT_EXTRA_BACK_TO, clazz);
        setResult(RESULT_BACK_TO);
        finish();
    }

    /**
     * Override this method to receive click event on menu item.
     */
    public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((ViewGroup) menu.getParent()).removeView(menu);
        menu = null;
    }

	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(contextMenu, view, menuInfo);
		
	}

	public boolean onContextItemSelected(MenuItem item) {
			return super.onContextItemSelected(item);
	}
}