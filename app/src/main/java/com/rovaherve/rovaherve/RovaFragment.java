package com.rovaherve.rovaherve;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RovaFragment extends Fragment implements View.OnClickListener {
    public RovaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        final ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setTitle("ROVA");
        }

        View fragment = inflater.inflate(R.layout.fragment_rova, container, false);
        fragment.findViewById(R.id.button).setOnClickListener(this);
//         fragment.findViewById(R.id.forgotPassword).setOnClickListener(this);
        return fragment;
    }

    public void onClick(View v) {
        final MainActivity parent = (MainActivity) requireActivity();
        Toast.makeText(parent, "BUTTON 1 CLICKED", Toast.LENGTH_SHORT).show();
//        if (v.getId() == R.id.button) {
//            Toast.makeText(parent, "BUTTON 1 CLICKED", Toast.LENGTH_SHORT).show();
//            return;
//        }
    }
//
//        // if (v.getId() == R.id.forgotPassword) {
//        //    parent.showFragment(LoginActivity.FRAGMENT_RESET, null);
//            // Toast.makeText(parent, R.string.service_not_unavailable, Toast.LENGTH_LONG).show();
//            // return;
//        // }
//
//        // EditText loginInput = parent.findViewById(R.id.editLogin);
//        // EditText passwordInput = parent.findViewById(R.id.editPassword);
//
//        // final String login = loginInput.getText().toString().trim();
//        // if (login.isEmpty()) {
//        //    loginInput.setError(getText(R.string.login_required));
//        //    return;
//        // }
//        // final String password = passwordInput.getText().toString().trim();
//        // if (password.isEmpty()) {
//            // passwordInput.setError(getText(R.string.password_required));
//            // return;
//        // }
//
//        // final Button signIn = parent.findViewById(R.id.signIn);
//        // signIn.setEnabled(false);
//    }

}