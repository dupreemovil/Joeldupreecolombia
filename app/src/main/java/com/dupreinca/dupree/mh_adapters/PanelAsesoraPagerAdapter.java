package com.dupreinca.dupree.mh_adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dupreinca.dupree.mh_fragments_menu.panel_asesoras.tabs.FaltantesAsesoraFragment;
import com.dupreinca.dupree.mh_fragments_menu.panel_asesoras.tabs.TrackingFragment;

/**
 * Created by cloudemotion on 5/8/17.
 */

public class PanelAsesoraPagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG=PanelAsesoraPagerAdapter.class.getName();
    private final int numPages=2;
    public static final int PAGE_TRACKING=0;
    public static final int PAGE_FALTANTES_Y_CONF=1;

    private TrackingFragment trackingFragment;
    private FaltantesAsesoraFragment faltantesAsesoraFragment;

    public PanelAsesoraPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case PAGE_TRACKING:
                trackingFragment = TrackingFragment.newInstance();
                return trackingFragment;
            case PAGE_FALTANTES_Y_CONF:
                faltantesAsesoraFragment = FaltantesAsesoraFragment.newInstance();
                return faltantesAsesoraFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return numPages;
    }

    public TrackingFragment getTrackingFragment() {
        return trackingFragment;
    }

    public FaltantesAsesoraFragment getFaltantesAsesoraFragment() {
        return faltantesAsesoraFragment;
    }
}
