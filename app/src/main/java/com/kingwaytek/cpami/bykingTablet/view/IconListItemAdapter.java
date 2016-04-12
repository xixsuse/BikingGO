package com.kingwaytek.cpami.bykingTablet.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

/**
 * Adapter for list item with icon. Using R.layout.icon_list_item for layout.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class IconListItemAdapter extends BaseAdapter {

    private Context ctx;
    private int itemLayout;
    private int[] icons;
    private String[] texts;

    /**
     * Create new instance of IconListItemAdapter.
     * 
     * @param ctx
     *            The context.
     * @param itemLayout
     *            Resource ID of layout for list item.
     * @param icons
     *            The resource ID of icons for items. (Set 0 for none)
     * @param texts
     *            The texts for items.
     */
    public IconListItemAdapter(Context ctx, int itemLayout, int[] icons, String[] texts) {
        this.ctx = ctx;
        this.itemLayout = itemLayout;
        this.icons = icons;
        this.texts = texts;
    }

    /**
     * {@inheritDoc}
     */
    public int getCount() {
        int iconLen = icons == null ? 0 : icons.length;
        int textLen = texts == null ? 0 : texts.length;

        return iconLen > textLen ? textLen : textLen;
    }

    /**
     * {@inheritDoc}
     */
    public Object getItem(int position) {
        return texts == null || position > texts.length - 1 ? "" : texts[position];
    }

    /**
     * {@inheritDoc}
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(itemLayout, null);
        }

        if (icons != null && position <= icons.length - 1 && icons[position] > 0) {
            ((ImageView) convertView.findViewById(R.id.li_icon)).setImageDrawable(
                    ctx.getResources().getDrawable(icons[position]));
        }

        if (texts != null && position <= texts.length - 1 && texts[position] != null) {
            ((TextView) convertView.findViewById(R.id.li_text)).setText(texts[position]);
        }

        return convertView;
    }

    /**
     * Returns the layout for list items.
     * 
     * @return The resource ID of layout.
     */
    public int getItemLayout() {
        return itemLayout;
    }

    /**
     * Set the layout for list items.
     * 
     * @param itemLayout
     *            The resource ID of layout to set.
     */
    public void setItemLayout(int itemLayout) {
        this.itemLayout = itemLayout;
    }

    /**
     * Returns resource ID of icons for list items.
     * 
     * @return Resource ID of icons
     */
    public int[] getIcons() {
        return icons;
    }

    /**
     * Set resource ID of icons for list items.
     * 
     * @param icons
     *            The Resource ID of icons to set
     */
    public void setIcons(int[] icons) {
        this.icons = icons;
    }

    /**
     * Returns texts for list items.
     * 
     * @return The texts.
     */
    public String[] getTexts() {
        return texts;
    }

    /**
     * Set texts for list items.
     * 
     * @param texts
     *            The texts to set
     */
    public void setTexts(String[] texts) {
        this.texts = texts;
    }
}
