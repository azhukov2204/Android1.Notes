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
import com.google.android.gms.tasks.Task;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKScope;
import com.vk.sdk.api.account.AccountService;
import com.vk.sdk.api.account.dto.AccountUserSettings;
import com.vk.sdk.api.users.UsersService;
import com.vk.sdk.api.users.dto.UsersFields;
import com.vk.sdk.api.users.dto.UsersUserXtrCounters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if (AuthFragment.this.getArguments() != null) {
                AuthFragment.this.getArguments().putBoolean(IS_SIGNOUT_REQUIRED_BUNDLE_KEY, false);
            }
        });
        VK.logout();
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
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getContext()), googleSignInOptions);

        view.findViewById(R.id.googleSignIn).setOnClickListener(v -> signInWithGoogle());
        view.findViewById(R.id.vkSignIn).setOnClickListener(v -> signInWithVK());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isSignOutRequired = false;
        if (getArguments() != null) {
            isSignOutRequired = getArguments().getBoolean(IS_SIGNOUT_REQUIRED_BUNDLE_KEY);
        }


        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getContext()));

        if (isSignOutRequired) {
            signOut();
        } else {
            if (googleSignInAccount != null) {
                startNotesWithAuthData(googleSignInAccount);
            } else if (VK.isLoggedIn()) {
                mainActivity.startNotesWithAuthDataVK();
            }
        }

    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithVK() {
        Set<VKScope> vkScopeSet = new HashSet<>();
        vkScopeSet.add(VKScope.EMAIL);
        VK.login(requireActivity(), vkScopeSet);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    startNotesWithAuthData(googleSignInAccount);
                }
            } catch (ApiException e) {
                Log.w(LOG_TAG, "GoogleSignInResult:failed code= " + e.getStatusCode());
            }
        }
    }


    private void startNotesWithAuthData(GoogleSignInAccount googleSignInAccount) {
        mainActivity.setUserData(true, googleSignInAccount.getEmail(), googleSignInAccount.getDisplayName(), googleSignInAccount.getEmail(), googleSignInAccount.getPhotoUrl());
        mainActivity.openNotesFragment();
    }
}