package com.example.jesta.view.fragments.jestas.addition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentAddJestaBinding;
import com.example.jesta.interfaces.ITabsNavigationHelper;
import com.example.jesta.view.adapters.ViewPagerAdapter;
import com.example.jesta.viewmodel.CreateJestaViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;


public class AddJestaFragment extends Fragment implements ITabsNavigationHelper {

    // region members

    private FragmentAddJestaBinding _binding;
    private final String[] titles = new String[5];
    private CreateJestaViewModel _createJestaViewModel;

    // endregion

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_jesta, container, false);
        _createJestaViewModel = new ViewModelProvider(this).get(CreateJestaViewModel.class);
        _createJestaViewModel.setNavigationHelper(this);
        init();
        return _binding.getRoot();
    }

    @Override
    public void onDestroy() {
        _createJestaViewModel.setNavigationHelper(null);
        super.onDestroy();
    }

    private void init(){
        initTabs();
    }

    private void initTabs(){
        initTitles();
        ArrayList<Fragment> fragments = new ArrayList<>();
        // the order is important for rtl support
        fragments.add(new WhatFragment());
        fragments.add(new WhenFragment());
        fragments.add(new WhereFragment());
        fragments.add(new PaymentFragment());
        fragments.add(new SummaryFragment());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getParentFragmentManager(), getLifecycle(),fragments);
        _binding.viewPager.setAdapter(adapter);
        new TabLayoutMediator(_binding.tabLayout, _binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }).attach();
    }

    private void initTitles(){
        titles[0] = getString(R.string.tab_what);
        titles[1] = getString(R.string.tab_when);
        titles[2] = getString(R.string.tab_where);
        titles[3] = getString(R.string.tab_payment);
        titles[4] = getString(R.string.tab_summary);

    }

    @Override
    public void moveTab(int tabPosition) {
        _binding.tabLayout.getTabAt(tabPosition).select();
    }
}