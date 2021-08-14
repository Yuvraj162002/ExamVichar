package com.example.android.studentsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.android.studentsapp.IndianActivities.IndianActivity;
import com.example.android.studentsapp.Model.MyLoaders;
import com.example.android.studentsapp.StateActivities.StatesActivity;
import com.example.android.studentsapp.databinding.ActivityBranchBinding;
import com.example.android.studentsapp.databinding.ActivityHomeBinding;
import com.example.android.studentsapp.databinding.ChooseExamDialogBinding;
import com.example.android.studentsapp.databinding.ChooseThemeDialogBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    boolean isGuest;
    ChooseThemeDialogBinding binding;
    ActivityHomeBinding b;
    private GoogleSignInClient googleSignInClient;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPref sharedPref;
    private Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkingIfUserSignedIn();
        sharedPref = new SharedPref(this);
        selectingMode();
        super.onCreate(savedInstanceState);
        b = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        actionBarDrawerToggle = new ActionBarDrawerToggle(this,b.drawerLayout
        ,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        b.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        b.navView.setNavigationItemSelectedListener(this);
    }

    private void selectingMode() {
        if(sharedPref.loadNightModeState() == -1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else if(sharedPref.loadNightModeState() == 1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if(sharedPref.loadNightModeState() == 0){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void checkingIfUserSignedIn() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
        isGuest = getIntent().getBooleanExtra("Guest",false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null && isGuest == false) {
            Intent startIntent = new Intent(HomeActivity.this, RegistrationActivity.class);
            startActivity(startIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return (true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn) {
            mAuth.signOut();
            Intent startIntent = new Intent(HomeActivity.this, RegistrationActivity.class);
            startActivity(startIntent);
            finish();
            return true;
        }

        if(item.getItemId() == R.id.main_set_theme){
            ChooseThemeDialogBinding binding = ChooseThemeDialogBinding.inflate(LayoutInflater.from(HomeActivity.this));

            new MaterialAlertDialogBuilder(HomeActivity.this,R.style.CustomDialogTheme2)
                    .setView(binding.getRoot())
                    .setCancelable(false).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(binding.auto.isChecked()){
                        sharedPref.setNightModeState(0);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }
                    else if(binding.dark.isChecked()){
                        sharedPref.setNightModeState(1);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    else{
                        sharedPref.setNightModeState(-1);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(HomeActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            })
                    .show();

            sharedPreferences = getSharedPreferences("pref",0);
            int mode = sharedPreferences.getInt("modeSP",3);
            editor = sharedPreferences.edit();
            if(mode == 1){
                binding.light.setChecked(true);
            }
            else if(mode == 0){
                binding.dark.setChecked(true);
            }
            else if(mode == -1){
                binding.auto.setChecked(true);
            }
            binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == R.id.light){
                        editor.putInt("modeSP", 1);
                    }else if(checkedId == R.id.dark){
                        editor.putInt("modeSP", 0);
                    }
                    else if(checkedId == R.id.auto){
                        editor.putInt("modeSP", -1);
                    }
                    editor.commit();
                }
            });


            return true;
        }

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void restartApp() {
        Intent i = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(i);
        finish();
    }

    public void moveToIndia(View view) {
        Intent intent = new Intent(HomeActivity.this, IndianActivity.class);
        intent.putExtra("isGuest",isGuest);
        startActivity(intent);
    }

    public void moveToState(View view) {
        Intent intent = new Intent(HomeActivity.this, StatesActivity.class);
        intent.putExtra("isGuest",isGuest);
        startActivity(intent);
    }

    public void moveToCollegeLevel(View view) {
        Intent intent = new Intent(HomeActivity.this,CollegeActivity.class);
        intent.putExtra("isGuest",isGuest);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_home: {
                b.drawerLayout.closeDrawer(GravityCompat.START);
                b.navView.getMenu().getItem(0).setChecked(false);
                break;
            }
            case R.id.nav_about_us:{
                b.drawerLayout.closeDrawer(GravityCompat.START);
                b.navView.getMenu().getItem(1).setChecked(false);
                Intent i = new Intent(HomeActivity.this, AboutUs.class);
                startActivity(i);
                break;
            }
            case R.id.nav_about_app:{
                b.drawerLayout.closeDrawer(GravityCompat.START);
                b.navView.getMenu().getItem(2).setChecked(false);
                Intent i = new Intent(HomeActivity.this, AboutApp.class);
                startActivity(i);
                break;
            }
            case R.id.nav_share:{
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
                break;
            }
            case R.id.nav_contact_us:{
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "karanchh2001@gmail.com"));
                startActivity(intent);
                break;
            }
            case R.id.nav_rate_us:{
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
                }
                catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                }
                break;
            }
            case R.id.nav_feedback:{
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "karanchh2001@gmail.com"));
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

  /*  public void showCustomThemeDialog(View view) {

        ChooseExamDialogBinding binding = ChooseExamDialogBinding.inflate(getLayoutInflater());
        new MaterialAlertDialogBuilder(HomeActivity.this,R.style.CustomDialogTheme)
                .setView(binding.getRoot())
               .show();

    }*/

}