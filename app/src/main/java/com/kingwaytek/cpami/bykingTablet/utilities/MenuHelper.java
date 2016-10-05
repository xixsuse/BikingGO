package com.kingwaytek.cpami.bykingTablet.utilities;

import android.view.Menu;
import android.view.MenuItem;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.ActionbarMenu;

/**
 * 除了在 UiMainMapActivity之外的 MenuItem都在這邊做設定！
 *
 * @author Vincent (2016/5/31)
 */
public class MenuHelper implements ActionbarMenu {

    public static void setMenuOptionsByMenuAction(Menu menu, int... actions) {
        menu.clear();

        for (int action : actions) {
            switch (action) {
                case ACTION_SWITCH:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_switch_button)
                            .setIcon(R.drawable.selector_toolbar_switch)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_AROUND:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_around_button)
                            .setIcon(R.drawable.selector_toolbar_around)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_ADD:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_add_poi)
                            .setIcon(R.drawable.selector_toolbar_add)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_SAVE:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_save)
                            .setIcon(R.drawable.selector_toolbar_save)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_EDIT:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_edit)
                            .setIcon(R.drawable.selector_poi_edit)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    break;

                case ACTION_LIST:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_path_list)
                            .setIcon(R.drawable.selector_toolbar_list)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_DELETE:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.poi_delete)
                            .setIcon(R.drawable.selector_delete)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_SHARE:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.poi_share)
                            .setIcon(android.R.drawable.ic_menu_share)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_UPLOAD:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_upload)
                            .setIcon(R.drawable.selector_toolbar_upload)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_FB_SHARE:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.poi_fb_share)
                            .setIcon(R.drawable.selector_toolbar_facebook)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_INFO:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_info)
                            .setIcon(R.drawable.selector_toolbar_info)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_MORE:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_more)
                            .setIcon(R.drawable.selector_toolbar_more)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_LIKE:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_like)
                            .setIcon(R.drawable.selector_toolbar_like)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;

                case ACTION_SEND:
                    menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_send)
                            .setIcon(R.drawable.selector_toolbar_send)
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    break;
            }
        }
    }
}
