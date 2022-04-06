package com.example.jesta.view.fragments.utilities;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.jesta.R;
import com.example.jesta.common.Consts;
import com.example.jesta.databinding.FragmentContactBinding;
import com.example.jesta.databinding.FragmentTermOfUseBinding;
import com.example.jesta.viewmodel.ContactViewModel;
import com.example.jesta.viewmodel.LoginRegisterViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    //region members

    private FragmentContactBinding _binding;
    private ContactViewModel _contactViewModel;
    private LoginRegisterViewModel _loginRegisterViewModel;

    //end region

    //region LifeCycle

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater,R.layout.fragment_contact, container, false);
        init();
        return _binding.getRoot();
    }

    //end region

    //region Private Methods

    private void init() {
        _binding.contactFragmentSendMessageBtn.setOnClickListener((v)->{
            sendMessage();
        });
    }

    private void sendMessage(){

        //Validates user's input
        String firstName = _binding.contactFragmentFirstName.getText().toString();
        if(firstName.equals("")){
            _binding.contactFragmentFirstName.setError(getString(R.string.contact_first_name_error));
            return;
        }

        String lastName = _binding.contactFragmentLastName.getText().toString();
        if(lastName.equals("")){
            _binding.contactFragmentLastName.setError(getString(R.string.contact_last_name_error));
            return;
        }

        String email = _binding.contactFragmentEmail.getText().toString();
        if(email == null || email.equals("") || !Pattern.matches(Consts.EMAIL_VALIDATOR, email)){
            _binding.contactFragmentEmail.setError(getString(R.string.email_validation_error));
            return;
        }

        String subject = _binding.contactFragmentSubject.getText().toString();
        if(subject.equals("")){
            _binding.contactFragmentSubject.setError(getString(R.string.contact_subject_error));
            return;
        }

        String message = _binding.contactFragmentMessage.getText().toString();
        if(message.equals("") || message.length() <= 8){
            _binding.contactFragmentMessage.setError(getString(R.string.contact_message_error));
            return;
        }

        _contactViewModel.sendMessage(firstName, lastName, email, subject, message);

    }
}