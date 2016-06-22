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

    public static void setMenuOptionsByMenuAction(Menu menu, int action) {
        menu.clear();

        switch (action) {
            case ACTION_ADD:
                menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_add_poi)
                        .setIcon(R.drawable.selector_toolbar_add)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                break;

            case ACTION_SAVE:
                menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_save)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                break;

            case ACTION_EDIT:
                menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_edit)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                break;

            case ACTION_LIST:
                menu.add(Menu.NONE, action, Menu.NONE, R.string.actionbar_path_list)
                        .setIcon(R.drawable.selector_toolbar_list)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                break;

            case ACTION_DELETE:
                menu.add(Menu.NONE, action, Menu.NONE, R.string.poi_delete)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                break;
        }
    }
}
