package info.vhowto.oinkbrewmobile;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import info.vhowto.oinkbrewmobile.activities.ConfigurationListActivity;
import info.vhowto.oinkbrewmobile.activities.SettingsActivity;

public class DrawerHelper implements Drawer.OnDrawerItemClickListener {

    private Activity activity;
    private AccountHeader header = null;
    private Drawer drawer = null;

    public Drawer createDrawer(Activity activity, Toolbar toolbar)
    {
        this.activity = activity;

        header = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this.activity)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(header)
                .withSelectedItem(-1)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_configurations).withIcon(GoogleMaterial.Icon.gmd_polymer).withIdentifier(DrawerItems.CONFIGURATION).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_brew_pis).withIcon(GoogleMaterial.Icon.gmd_developer_board).withIdentifier(DrawerItems.BREWPIS).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_devices).withIcon(GoogleMaterial.Icon.gmd_settings_input_component).withIdentifier(DrawerItems.DEVICES).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_settings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(DrawerItems.SETTINGS).withSelectable(false)
                )
                .withOnDrawerItemClickListener(this)
                .build();

        return drawer;
    }

    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
    {
        if (drawerItem != null) {
            Intent intent = null;

            if (drawerItem.getIdentifier() == DrawerItems.CONFIGURATION && !(activity instanceof ConfigurationListActivity)) {
                Snackbar.make(view, "You clicked Configurations", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if (drawerItem.getIdentifier() == DrawerItems.BREWPIS) {
                Snackbar.make(view, "You clicked BrewPis", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if (drawerItem.getIdentifier() == DrawerItems.DEVICES) {
                Snackbar.make(view, "You clicked Devices", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if (drawerItem.getIdentifier() == DrawerItems.SETTINGS && !(activity instanceof SettingsActivity)) {
                intent = new Intent(activity, SettingsActivity.class);
            }

            if (intent != null) {
                activity.startActivity(intent);
            }
        }

        return false;
    }
}
