package com.matrix_maeny.attendance.collage.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.matrix_maeny.attendance.R;
import com.matrix_maeny.attendance.databinding.ActivityChooserBinding;
import com.matrix_maeny.attendance.collage.fragments.CreateCollageFragment;

public class ChooserActivity extends AppCompatActivity implements CreateCollageFragment.CreateCollageFragmentListener {

    // binding which holds components
    private ActivityChooserBinding binding;


    // creating global transactions and fragments, because there is a need to remove further

    // fragment transaction for transacting fragments
    private CreateCollageFragment createCollageFragment; // create fragment
//    private JoinCollageFragment joinCollageFragment; // join fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // to create a translucent status of status bar

        initialize();
    }

    private void initialize() {

        binding.caCreateBtn.setOnClickListener(createBtnListener);
    }

    // onclick listener for create btn in the chooser activity
    View.OnClickListener createBtnListener = v -> {

        // we need to show collage creation fragment ..
        // this function works on boolean createCollage (here createCollage = true);
        setCollageFragment(true);
    };


    // onclick listener for join btn in the chooser activity


    // this function replaces fragments, it replaces CreateCollageFragment for true

    // and JoinCollage for false
    private void setCollageFragment(boolean createCollage) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); // initializing transaction manager


        createCollageFragment = new CreateCollageFragment(); // initializing fragments

        // replacing container with fragments
        if (createCollage) {
            transaction.replace(R.id.fragmentContainer, createCollageFragment);
        }

        // committing transaction
        transaction.commit(); // it replaces fragment

        // on fragment is replaced the button should invisible
        transaction.runOnCommit(() -> {
            binding.caChooserLayout.setVisibility(View.GONE); // immediately the buttons should be invisible
        });

    }


    // this function removes fragments when the user presses back button
    private void removeFragments() {

        // we don't know which fragment is selected by the user
        // so removing everything from the container.
        // if we use only one try, one exception may cause another to stop
        // if one fragment is not in the state, it causes exception, so the next line will not execute,
        // so taking two try blocks
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); // taking new transaction manager

        try {
            transaction.remove(createCollageFragment);
            transaction.commit();

            transaction.runOnCommit(() -> {
                binding.caChooserLayout.setVisibility(View.VISIBLE); // immediately the buttons should be invisible
            });
        } catch (Exception ignored) {} // for create collage

    }

    // when user presses back btn in the mobile
    @Override
    public void onBackPressed() {
        if (binding.caChooserLayout.getVisibility() == View.GONE) { // let the first press makes the buttons visible
            removeFragments(); // removing fragments
        } else
            super.onBackPressed(); // second press will end the activity
    }


    @Override
    public void finishActivity() {
        finish();
    }
}