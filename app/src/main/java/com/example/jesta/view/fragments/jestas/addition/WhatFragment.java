package com.example.jesta.view.fragments.jestas.addition;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentWhatBinding;
import com.example.jesta.viewmodel.CreateJestaViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class WhatFragment extends Fragment {

   // region Members

    private FragmentWhatBinding _binding;
    private CreateJestaViewModel _createJestaViewModel;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_what,container,false);
        _createJestaViewModel = new ViewModelProvider(this).get(CreateJestaViewModel.class);
        init();

        return _binding.getRoot();
    }

    private void init(){
        initSpinners();
        initBinding();
        initListeners();
    }

    private void initSpinners(){
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _binding.categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> peopleAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.people_amount, android.R.layout.simple_spinner_item);
        peopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _binding.peopleAmountSpinner.setAdapter(peopleAdapter);
    }

    private void initBinding(){
        _binding.setViewModel(_createJestaViewModel);
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    public void initListeners(){
        _binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                _createJestaViewModel.setCategorySelected(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        _binding.peopleAmountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                _createJestaViewModel.setOnAmountSpinnerSelected(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        _binding.firstImage.setOnClickListener(v->{
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.setType("image/*");
            photo1Listener.launch(pickPhoto);
        });
        _binding.secondImage.setOnClickListener(v->{
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.setType("image/*");
            photo2Listener.launch(pickPhoto);
        });
        _binding.thirdImage.setOnClickListener(v->{
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.setType("image/*");
            photo3Listener.launch(pickPhoto);
        });
    }

    // region helpers

    private final ActivityResultLauncher<Intent> photo1Listener = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                   activityResult(result, _binding.firstImage);
                   if (result.getResultCode() == Activity.RESULT_OK){
                       _binding.secondImage.setImageResource(R.drawable.ic_baseline_add_24);
                       _binding.secondImage.setClickable(true);
                   }
                }
            }
    );

    private final ActivityResultLauncher<Intent> photo2Listener = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    activityResult(result, _binding.secondImage);
                    if (result.getResultCode() == Activity.RESULT_OK){
                        _binding.thirdImage.setImageResource(R.drawable.ic_baseline_add_24);
                        _binding.thirdImage.setClickable(true);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> photo3Listener = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    activityResult(result, _binding.thirdImage);
                }
            }
    );

    private void activityResult(ActivityResult result, ImageView imageView){
        if(result.getResultCode() == Activity.RESULT_OK){
            Intent data = result.getData();
            Uri uri = data != null ? data.getData() : null;
            Picasso.with(imageView.getContext()).load(uri).fit().into(imageView);
        }
    }

    // endregion


}