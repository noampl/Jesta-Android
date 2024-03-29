package com.example.jesta.view.fragments.jestas.addition;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.jesta.R;
import com.example.jesta.databinding.FragmentPaymentBinding;
import com.example.jesta.type.PaymentType;
import com.example.jesta.viewmodel.CreateJestaViewModel;


public class PaymentFragment extends Fragment {

    // region Members

    private FragmentPaymentBinding _binding;
    private CreateJestaViewModel _createJestaViewModel;

    // endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false);
        _createJestaViewModel = new ViewModelProvider(this).get(CreateJestaViewModel.class);

        init();
        return _binding.getRoot();
    }

    private void init(){
        initBinding();
        initListeners();
    }

    private void initBinding(){
        _binding.setViewModel(_createJestaViewModel);
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initListeners(){
        _binding.payForItRadio.setOnClickListener(view -> {
            _binding.amountCard.setVisibility(View.VISIBLE);
            _createJestaViewModel.set_paymentType(PaymentType.CASH);
        });
        _binding.moveItForwardRadio.setOnClickListener(view -> {
            _binding.amountCard.setVisibility(View.GONE);
            _binding.amountEditTxt.setText("");
            _createJestaViewModel.set_amount(0);
            _createJestaViewModel.set_paymentType(PaymentType.FREE);
        });
    }
}