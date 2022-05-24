package com.example.jesta.view.fragments.jestas.addition;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentWhatBinding;
import com.example.jesta.model.enteties.Category;
import com.example.jesta.viewmodel.CreateJestaViewModel;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.Collectors;

import okio.Okio;
import okio.Source;

public class WhatFragment extends Fragment {

    // region Members

    private FragmentWhatBinding _binding;
    private CreateJestaViewModel _createJestaViewModel;
    private ArrayAdapter<String> parentCategoryAdapter;
    private ArrayAdapter<String> subCategoryAdapter;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_what, container, false);
        _createJestaViewModel = new ViewModelProvider(this).get(CreateJestaViewModel.class);
        init();

        return _binding.getRoot();
    }

    private void init() {
        initSpinners();
        initBinding();
        initListeners();
        initObservers();
    }

    private void initSpinners() {
        parentCategoryAdapter = new ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                _createJestaViewModel.getCategories().keySet().stream().map(Category::getName).collect(Collectors.toList()));

        _binding.categorySpinner.setAdapter(parentCategoryAdapter);
        if (_createJestaViewModel.get_selectedParentCategory().getValue() != null) {
            int position = parentCategoryAdapter.getPosition(_createJestaViewModel.get_selectedParentCategory().getValue().getName());
            _binding.categorySpinner.setSelection(position);
        }

        ArrayAdapter<CharSequence> peopleAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.people_amount, android.R.layout.simple_spinner_item);
        peopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _binding.peopleAmountSpinner.setAdapter(peopleAdapter);
        _binding.peopleAmountSpinner.setSelection(_createJestaViewModel.get_numOfPeople().getValue());

    }

    private void initBinding() {
        _binding.setViewModel(_createJestaViewModel);
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initListeners() {
        _binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (_createJestaViewModel.getCategoryByName((String) adapterView.getSelectedItem()) != null) {
                    Category selectedParent = _createJestaViewModel.getCategoryByName((String) adapterView.getSelectedItem());
                    _createJestaViewModel.set_selectedParentCategory(selectedParent);

                    if (_createJestaViewModel.getCategories().get(selectedParent) != null &&
                            _createJestaViewModel.getCategories().get(selectedParent).size() > 0) {
                        subCategoryAdapter = new ArrayAdapter(requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                _createJestaViewModel.getCategories().get(selectedParent).stream().map(Category::getName).collect(Collectors.toList()));
                        _binding.subcategorySpinner.setAdapter(subCategoryAdapter);
                        _binding.subcategorySpinnerCard.setVisibility(View.VISIBLE);
                    } else {
                        _binding.subcategorySpinnerCard.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        _binding.subcategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _createJestaViewModel.set_selectedSubCategory(
                        _createJestaViewModel.getCategoryByName((String) parent.getSelectedItem())
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        _binding.firstImage.setOnClickListener(v ->

        {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhoto.setType("image/*");
            photo1Listener.launch(pickPhoto);
        });

    }

    private void initObservers() {
        _createJestaViewModel.get_selectedParentCategory().observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                int position = parentCategoryAdapter.getPosition(category.getName());
                _binding.categorySpinner.setSelection(position);
            }
        });
    }

    // region helpers

    private final ActivityResultLauncher<Intent> photo1Listener = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    activityResult(result, _binding.firstImage);
                }
            }
    );

    private void activityResult(ActivityResult result, ImageView imageView) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            Uri uri = data != null ? data.getData() : null;
            Picasso.with(imageView.getContext()).load(uri).fit().into(imageView);
            InputStream inputStream = null;
            Source source = null;
            try {
                inputStream = getContext().getContentResolver().openInputStream(uri);
                source = Okio.source(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (imageView == _binding.firstImage) {
                _createJestaViewModel.setImage1(new Pair<Uri, Source>(uri, source));
            }
            } else {
                Log.e("WhatFragment", "get activity result for unknown imageview ");
            }
        }
    }

    // endregion


