package com.example.atg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.atg.R;
import com.example.atg.Reterofit.Model;
import com.example.atg.Reterofit.NetworkClient;
import com.example.atg.Reterofit.Photo;
import com.example.atg.Reterofit.RequestService;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    private ArrayList<String> mUrls = new ArrayList<>();
    private ArrayList<Photo> mPhoto = new ArrayList<>();
    View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment,container,false);
        final RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        final RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), mUrls);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        Retrofit retrofit = NetworkClient.getRetrofitClient();
        final RequestService requestService = retrofit.create(RequestService.class);
        Call<Model> call = requestService.requestGet();
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

        return rootView;
    }
}
