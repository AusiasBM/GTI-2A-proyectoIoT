package com.example.proyecto2a.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.proyecto2a.R;
import com.example.proyecto2a.casos_uso.SliderAdapter;
import com.example.proyecto2a.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager slideViewPager;
    private TabLayout dotsTabLayout;

    private SliderAdapter sliderAdapter;
    private Button btBack;
    private Button btNext;
    private int currentPage;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        dotsTabLayout = findViewById(R.id.dots);
        slideViewPager = findViewById(R.id.viewPager);
        btBack = findViewById(R.id.btBack);
        btNext = findViewById(R.id.btNext);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("id");
        sliderAdapter = new SliderAdapter(this);
        slideViewPager.setAdapter(sliderAdapter);
        dotsTabLayout.setupWithViewPager(slideViewPager);
        setUpDots();

        slideViewPager.addOnPageChangeListener(viewListener);

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                if(currentPage == (sliderAdapter.getCount()-1)){
                    //Salimos del tutorial
                    DocumentReference user = db.collection("usuarios").document(Uid);
                    user.update("nuevo", false);
                    finish();
                }else{
                    slideViewPager.setCurrentItem(currentPage + 1);
                }
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideViewPager.setCurrentItem(currentPage - 1);
            }
        });
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            if(currentPage == 0){
                btNext.setEnabled(true);

                btBack.setEnabled(false);
                btBack.setVisibility(View.GONE);
            }else if (position == sliderAdapter.getCount() -1){
                btNext.setEnabled(true);

                btBack.setEnabled(true);

                btBack.setVisibility(View.VISIBLE);
            }else{
                btNext.setEnabled(true);
                btBack.setEnabled(true);
                btBack.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private void setUpDots(){
        ViewGroup tabStrip = (ViewGroup) dotsTabLayout.getChildAt(0);
        for (int i = 0; i<tabStrip.getChildCount(); i++){
            View tabView = tabStrip.getChildAt(i);
            if(tabView != null){
                int paddingStart = tabView.getPaddingStart();
                int paddingTop = tabView.getPaddingTop();
                int paddingEnd = tabView.getPaddingEnd();
                int paddingBottom = tabView.getPaddingBottom();
                ViewCompat.setBackground(tabView, AppCompatResources.getDrawable(tabView.getContext(), R.drawable.tab_color));
                ViewCompat.setPaddingRelative(tabView, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }
        }
    }
}
