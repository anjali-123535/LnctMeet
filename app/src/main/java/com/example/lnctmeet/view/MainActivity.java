package com.example.lnctmeet.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lnctmeet.R;
import com.example.lnctmeet.adapters.CategoryFragmentPagerAdapter;
import com.example.lnctmeet.model.Post;
import com.example.lnctmeet.preferences.UserSessionManager;
import com.example.lnctmeet.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
   //RetrofitService service;
   UserSessionManager userSessionManager;
   HashMap<String,String>details;
   String name;
    RecyclerView recyclerView;
    Query q1;
    ViewPager viewPager;
    TabLayout tablayout;
    Toolbar toolbar;
    private static final String TAG_NAME = MainActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //service= ApiClient.getClient().create(RetrofitService.class);
        setUpUIViews();
        if (userSessionManager.checkLogin())
            finish();
        //setting toolbar in place of actionbar
        setSupportActionBar(toolbar);
        details = userSessionManager.getUserDetails();
        name = details.get(UserSessionManager.KEY_NAME);
        toolbar.setTitle(name);
        tablayout.setupWithViewPager(viewPager);
        // Set gravity for tab bar
        tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // Set category fragment pager adapter
        CategoryFragmentPagerAdapter pagerAdapter =
                new CategoryFragmentPagerAdapter(this, getSupportFragmentManager());
        // Set the pager adapter onto the view pager
        viewPager.setAdapter(pagerAdapter);
    }
   public String sendData(){
        return userSessionManager.getUserDetails().get(UserSessionManager.KEY_LOGIN);
    }
    void setUpUIViews()
    {
      //  recyclerView = findViewById(R.id.recycler);
        userSessionManager=new UserSessionManager(this);
        recyclerView = findViewById(R.id.recycler_view);
        viewPager = findViewById(R.id.view_pager);
        // Give the TabLayout the ViewPager
        tablayout = findViewById(R.id.tabs);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                userSessionManager.logoutUser();
                break;
            case R.id.action_bookmark:
                startActivity(new Intent(MainActivity.this,SavedActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}