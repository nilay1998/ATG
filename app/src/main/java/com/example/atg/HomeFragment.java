package com.example.atg;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.atg.R;
import com.example.atg.Reterofit.Model;
import com.example.atg.Reterofit.NetworkClient;
import com.example.atg.Reterofit.Photo;
import com.example.atg.Reterofit.RequestService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.support.constraint.Constraints.TAG;

public class HomeFragment extends Fragment {

    private ArrayList<String> mUrls =new ArrayList<>();
    private ArrayList<Photo> mPhoto =new ArrayList<>();
    View rootView;
    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView recyclerView;
    boolean isAlreadyLoaded=false;
    Boolean isScrolling=false;
    ProgressBar progressBar;
    int page=2;
    int max_page=3;


    public void showLoader() {

        if (getView() != null) {
            View progressBar = getView().findViewById(R.id.progress_bar);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public void hideLoader() {

        if (getView() != null) {
            View progressBar = getView().findViewById(R.id.progress_bar);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.home_fragment,container,false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), mUrls);
        recyclerView.setAdapter(recyclerViewAdapter);
        final RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        progressBar=rootView.findViewById(R.id.progress_bar);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling=true;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();

                if(isScrolling && (visibleItemCount +firstVisibleItemPosition ==totalItemCount))
                {
                    isScrolling=false;
                    if(page<=max_page)
                    {
                        Log.e(TAG, "onScrolled: "+page );
                        showLoader();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Retrofit retrofit = NetworkClient.getRetrofitClient();
                                final RequestService requestService = retrofit.create(RequestService.class);
                                Call<Model> call = requestService.requestGet(String.valueOf(page));
                                call.enqueue(new Callback<Model>() {
                                    @Override
                                    public void onResponse(Call<Model> call, Response<Model> response) {
                                        if (response.isSuccessful()) {
                                            mPhoto = new ArrayList<>((Arrays.asList(response.body().getPhotos().getPhoto())));
                                            for (int i = 0; i < mPhoto.size(); i++) {
                                                mUrls.add(mPhoto.get(i).getUrl_s());
                                            }
                                            recyclerViewAdapter.notifyDataSetChanged();
                                            hideLoader();
                                            Toast.makeText(getContext(), "Page "+page+" loaded", Toast.LENGTH_SHORT).show();
                                            page++;
                                        } else {
                                            Toast.makeText(getActivity(), "Server returned an error " + response.code(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Model> call, Throwable t) {
                                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("Error", t.getMessage());
                                    }
                                });
                            }
                        },1000);
                    }
                }
            }
        });

        if(savedInstanceState==null && isAlreadyLoaded==false)
        {
            isAlreadyLoaded=true;
            Retrofit retrofit = NetworkClient.getRetrofitClient();
            final RequestService requestService = retrofit.create(RequestService.class);
            Call<Model> call = requestService.requestGet(String.valueOf(1));
            call.enqueue(new Callback<Model>() {
                @Override
                public void onResponse(Call<Model> call, Response<Model> response) {
                    if (response.isSuccessful()) {
                        mPhoto = new ArrayList<>((Arrays.asList(response.body().getPhotos().getPhoto())));
                        for (int i = 0; i < mPhoto.size(); i++) {
                            mUrls.add(mPhoto.get(i).getUrl_s());
                        }
                        recyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Server returned an error " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Model> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Error", t.getMessage());
                }
            });
        }

        else if (savedInstanceState != null ) {
            //Restore the fragment's state here
            Log.e(TAG, "onCreateView: VIEW CREATED");
            mUrls.clear();
            for(int i=0;i<savedInstanceState.getStringArrayList("ArrayList").size();i++)
            {
                String val=savedInstanceState.getStringArrayList("ArrayList").get(i);
                mUrls.add(val);
                Log.i(TAG, "onActivityCreated: "+ val);
            }
            int x=savedInstanceState.getInt("abc");
            Log.e(TAG, "onActivityCreated: "+x);
            recyclerViewAdapter.notifyDataSetChanged();
        }
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState: CHANGING STATE");

        for(int i=0;i<mUrls.size();i++)
        {
            String val=mUrls.get(i);
            Log.i(TAG, "onActivityCreated: "+ val);
        }
//        mUrls.add("HAHAHA");
        outState.putStringArrayList("ArrayList",mUrls);
//        outState.putInt("abc",1);
        super.onSaveInstanceState(outState);
    }
}
