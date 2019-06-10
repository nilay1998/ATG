package com.example.atg;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atg.Reterofit.Model;
import com.example.atg.Reterofit.NetworkClient;
import com.example.atg.Reterofit.Photo;
import com.example.atg.Reterofit.Photos;
import com.example.atg.Reterofit.RequestService;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    Fragment homeFragment;
    Fragment searchFragmemt;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    Boolean isConnection=false;

    BroadcastReceiver br=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            NetworkInfo info = (NetworkInfo) extras
                    .getParcelable("networkInfo");
            NetworkInfo.State state = info.getState();
            Log.d("TEST Internet", info.toString() + " "
                    + state.toString());
            if (state == NetworkInfo.State.CONNECTED) {
                isConnection=true;
                //Toast.makeText(getApplicationContext(), "Internet connection is on", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getApplicationContext(), "Internet connection is Off", Toast.LENGTH_LONG).show();
                Snackbar.make(findViewById(R.id.container),"NO INTERNET",Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkInternetConnection();
                            }
                        }).show();
            }
        }
    };

    private void checkInternetConnection() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver( br, intentFilter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //selectedFragment =homeFragment;
                    fragmentManager.beginTransaction().replace(R.id.fragment_container,
                            homeFragment).commit();
                    break;
                case R.id.navigation_search:
//                    selectedFragment = searchFragmemt;
                    fragmentManager.beginTransaction().replace(R.id.fragment_container,
                            searchFragmemt).commit();

                    break;
            }

//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    selectedFragment).commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInternetConnection();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        homeFragment=new HomeFragment();
        searchFragmemt=new SearchFragment();

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    homeFragment).commit();
        }

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            if(searchFragmemt!=null && searchFragmemt.isVisible())
            {
                searchFragmemt=fragmentManager.getFragment(savedInstanceState,"search");
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        searchFragmemt).commit();
            }
            else if(homeFragment!=null && homeFragment.isVisible())
            {
                homeFragment=fragmentManager.getFragment(savedInstanceState,"home");
                fragmentManager.beginTransaction().replace(R.id.fragment_container,
                        homeFragment).commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        if(searchFragmemt.isVisible())
            fragmentManager.putFragment(outState, "search", searchFragmemt);
        else if(homeFragment.isVisible())
            fragmentManager.putFragment(outState, "home", homeFragment);
    }
}
