package com.kingwaytek.cpami.bykingTablet.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.ListView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.TypedArrayUtil;

/**
 * Class for menu of this application.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class IconListView extends ListView {

    public IconListView(Context context) {
        super(context);

        init(context, null);
    }

    public IconListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public IconListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, attrs);
    }

    /**
     * Initialize the adapter for this view.
     * 
     * @param context
     *            The current context.
     * @param attrs
     *            The attributes for the view.
     */
    private void init(Context context, AttributeSet attrs) {
        Resources res = context.getResources();
        String namespace = res.getString(R.string.app_namespace);

        int itemLayout = 0;
        int[] iconId = null;
        String[] text = null;

        if (attrs != null) {
            // get layout
            itemLayout = attrs.getAttributeResourceValue(namespace, "item_layout", 0);
            
            // get resource IDs of icons
            int resId = attrs.getAttributeResourceValue(namespace, "icons", -1);

            iconId = resId > 0
                    ? TypedArrayUtil.getResourceIds(res.obtainTypedArray(resId)) : null;

            // get texts
            resId = attrs.getAttributeResourceValue(namespace, "texts", -1);

            text = resId > 0 ? res.getStringArray(resId) : null;
        }

        // create adapter
        setAdapter(new IconListItemAdapter(context, itemLayout, iconId, text));
    }
}
