package com.example.jesta.view.fragments.jestas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesta.R;
import com.example.jesta.common.enums.FiledType;
import com.example.jesta.databinding.FragmentJestaDetailsBinding;
import com.example.jesta.interfaces.IDialogConsumerHelper;
import com.example.jesta.viewmodel.JestaDetailsViewModel;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStoreModule_SchemaVersionFactory;

import okio._Base64Kt;

public class JestaDetailsFragment extends Fragment {

    // region Members

    private FragmentJestaDetailsBinding _binding;
    private JestaDetailsViewModel _jestaDetailsViewModel;
    private String _jestaId;
    private final IDialogConsumerHelper messageConsumer = new IDialogConsumerHelper() {
        @Override
        public void consume(String val) {
            _jestaDetailsViewModel.set_comment(val);
//            _jestaDetailsViewModel.suggestHelp(_jestaId); // TODO does needed here?
        }
    };

    // endregion

    // region LifeCycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_jesta_details,container,false);
        _jestaDetailsViewModel = new ViewModelProvider(this).get(JestaDetailsViewModel.class);
        _jestaId = JestaDetailsFragmentArgs.fromBundle(getArguments()).getJestaId();
        _jestaDetailsViewModel.getDetails(_jestaId);
        init();

        return _binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _jestaDetailsViewModel.set_comment("");
        _jestaDetailsViewModel.set_isSuggestHelp(false);
    }

    // endregion

    // region Private Methods

    private void init(){
        initObserver();
        initListeners();
        _binding.setIsSuggestHelp(_jestaDetailsViewModel.get_isSuggestHelp());
        _binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void initObserver(){
        _jestaDetailsViewModel.get_jestaDetails().observe(getViewLifecycleOwner(),favor ->{
            _binding.setJestaDetails(favor);
        });
        _jestaDetailsViewModel.get_myLocation().observe(getViewLifecycleOwner(), latLng ->
                _binding.setMyLocation(latLng));
    }

    private void initListeners(){
        _binding.suggestHelp.setOnClickListener(v-> {
                    if (_jestaDetailsViewModel.get_isSuggestHelp().getValue() != null) {
                        if (!_jestaDetailsViewModel.get_isSuggestHelp().getValue()) {
                            _jestaDetailsViewModel.suggestHelp(_jestaId);
                        } else {
                            new AlertDialog.Builder(requireContext()).setMessage(R.string.disable_offer)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // TODO cancel transaction
                                            dialogInterface.dismiss();
                                        }                                        ;
                                    }
                            ).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create().show();
                        }
                    };
                });

        _binding.sendMsg.setOnClickListener(v->{
            _jestaDetailsViewModel.set_dialogConsumerHelper(messageConsumer);
            JestaDetailsFragmentDirections.ActionJestaDetailsFragmentToOneInputDialogFragment action =
                    JestaDetailsFragmentDirections.actionJestaDetailsFragmentToOneInputDialogFragment(null,getString(R.string.send_msg),getString(R.string.send_msg));
            action.setFiledType(FiledType.NAME.ordinal());
            Navigation.findNavController(requireActivity(),R.id.main_container).navigate(action);
        });
    }

    // endregion
}