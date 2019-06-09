package com.example.atg;

import android.os.Bundle;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import static android.content.ContentValues.TAG;


public class SearchFragment extends Fragment {
    View rootView;
    private ArrayList<String> mUrls = new ArrayList<>();
    private ArrayList<Photo> mPhoto = new ArrayList<>();
    Button button;
    EditText editText;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    Retrofit retrofit;
    RequestService requestService;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.search_fragment,container,false);

        recyclerView = rootView.findViewById(R.id.recyclerView_search);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), mUrls);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        retrofit = NetworkClient.getRetrofitClient();
        requestService = retrofit.create(RequestService.class);

        editText=rootView.findViewById(R.id.edit_text_search);
        button=rootView.findViewById(R.id.button_search);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoto.clear();
                mUrls.clear();
                recyclerViewAdapter.notifyDataSetChanged();
                String text=editText.getText().toString();
                Log.e(TAG, "onClick: "+text);
                Call<Model> call = requestService.requestSearch(text);
                call.enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
                        Log.e(TAG, "onClick: NOTHING HAPPENING");
                        if (response.isSuccessful()) {
                            mPhoto = new ArrayList<>((Arrays.asList(response.body().getPhotos().getPhoto())));
                            for (int i = 0; i < mPhoto.size(); i++) {
                                mUrls.add(mPhoto.get(i).getUrl_s());
                                Log.e(TAG, "onClick: NOTHING HAPPENING "+mUrls);
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
        });

        return rootView;
    }
}