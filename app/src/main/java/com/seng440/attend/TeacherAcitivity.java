package com.seng440.attend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class TeacherAcitivity extends AppCompatActivity {

    private BottomNavigationView mTeacherNav;
    private FrameLayout mTeacherFrame;
    private ClassesFragment classesFragment;
    private RollFragment rollFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_acitivity);

        classesFragment = new ClassesFragment();
        rollFragment = new RollFragment();
        mapFragment= new MapFragment();
        mTeacherNav = (BottomNavigationView) findViewById(R.id.teacher_nav);
        mTeacherFrame = (FrameLayout) findViewById(R.id.teacher_frame);
        mTeacherNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_classes:
                        setFragment(classesFragment);
                        return true;
                    case R.id.nav_roll:
                        setFragment(rollFragment);
                        return true;
                    case R.id.nav_map:
                        setFragment(mapFragment);
                        return true;
                    default:
                        return false;

                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.teacher_frame, fragment);
        fragmentTransaction.commit();
    }
}