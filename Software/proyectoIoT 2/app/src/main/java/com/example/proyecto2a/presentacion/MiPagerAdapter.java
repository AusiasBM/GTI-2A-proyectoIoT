package com.example.proyecto2a.presentacion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MiPagerAdapter extends FragmentStateAdapter {

    private String ubicacion, idUser;
    public MiPagerAdapter(FragmentActivity activity, String ubicacion, String idUser){
        super(activity);
        this.ubicacion=ubicacion;
        this.idUser=idUser;
    }
    @Override
    public int getItemCount() {
        return 2;
    }
    @Override @NonNull
    public Fragment createFragment(int position) {

        switch (position) {
            case 0: Fragment fr=new Tab1();
                Bundle args = new Bundle();
                args.putString("CID", ubicacion);
                args.putString("idUser", idUser);
                fr.setArguments(args);
                return fr;
            case 1: Fragment frr=new Tab2();
                Bundle arrgs = new Bundle();
                arrgs.putString("CID", ubicacion);
                arrgs.putString("idUser", idUser);
                frr.setArguments(arrgs);
                return frr;
        }
        return null;
    }
}