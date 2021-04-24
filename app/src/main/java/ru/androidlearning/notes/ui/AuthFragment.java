package ru.androidlearning.notes.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import ru.androidlearning.notes.MainActivity;
import ru.androidlearning.notes.R;

public class AuthFragment extends Fragment {

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 40404;
    private static final String LOG_TAG = "[AuthFragment]";
    private MainActivity mainActivity;
    private static final String IS_SIGNOUT_REQUIRED_BUNDLE_KEY = "isSignOutRequired";


    public static AuthFragment newInstance(boolean isSignOutRequired) {
        AuthFragment fragment = new AuthFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_SIGNOUT_REQUIRED_BUNDLE_KEY, isSignOutRequired);
        fragment.setArguments(args);
        return fragment;
    }

    private void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(task -> AuthFragment.this.getArguments().putBoolean(IS_SIGNOUT_REQUIRED_BUNDLE_KEY, false));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getContext(), googleSignInOptions);

        view.findViewById(R.id.googleSignIn).setOnClickListener(v -> signInWithGoogle());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isSignOutRequired = getArguments().getBoolean(IS_SIGNOUT_REQUIRED_BUNDLE_KEY);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if (googleSignInAccount != null) {
            if (!isSignOutRequired) {
                startNotesWithAuthData(googleSignInAccount);
            } else {
                signOut();
            }
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                startNotesWithAuthData(googleSignInAccount);
            } catch (ApiException e) {
                Log.w(LOG_TAG, "GoogleSignInResult:failed code= " + e.getStatusCode());
            }
        }

    }

    private void startNotesWithAuthData(GoogleSignInAccount googleSignInAccount) {
        mainActivity.setUserData(true, googleSignInAccount.getDisplayName(), googleSignInAccount.getEmail());
        mainActivity.openNotesFragment();

    }
}