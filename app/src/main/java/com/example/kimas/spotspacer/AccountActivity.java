package com.example.kimas.spotspacer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccountActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private View v;
    private TextView mStatusTextView;
    private ImageView imgProfilePic;
    private ProgressDialog mProgressDialog;
    private TextView spok;
    private TextView thanksGiving;
    public IBackendlessDB db;

    public static final String SPOTSPACER_PREFERENCES = "spotSpacer_Prefs";
    public static final String loginStatus = "loginStatus";
    public static final String userName = "userName";
    public static final String userAvatar = "userAvatar";
    public static final String userTags = "userTags";
    public static final String userThanks = "userThanks";
    public static final String userId = "userId";
    public static final String custId = "custId";
    public static final String gOrFb = "gOrFb";
    SharedPreferences sharedpreferences;


    private CallbackManager callbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_account);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        db = new BackendlessDB(this);


        // Views
        v = findViewById(R.id.contentAcc);
        mStatusTextView = (TextView) v.findViewById(R.id.textUserName);
        imgProfilePic = (ImageView) findViewById(R.id.imageAvatar);
        spok = (TextView) v.findViewById(R.id.spottedNum);
        thanksGiving = (TextView) v.findViewById(R.id.thanksNum);
        sharedpreferences = getSharedPreferences(SPOTSPACER_PREFERENCES, Context.MODE_PRIVATE);
        mStatusTextView.setText(sharedpreferences.getString(userName, "User"));
        Picasso.with(this)
                .load(sharedpreferences.getString(userAvatar, "avatar"))
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(imgProfilePic);
        spok.setText(" " + sharedpreferences.getInt(userTags, 0));

        thanksGiving.setText(" " + sharedpreferences.getInt(userThanks, 0));
        // Button listeners GOOGLE
        findViewById(R.id.sign_in_google).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        // FACEBOOK STUFF
        //printHashKey();
        callbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
        if (sharedpreferences.getBoolean(loginStatus, false)) {
            db.getUserData(sharedpreferences.getString(custId, ""));
        }

    }

    //FACEBOOK SIGN IN
    //===================================================================================
    private void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.kimas.spotspacer",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash========>", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    public void fbSignInButton(View view) {
        if (isNetworkAvailable()) {
            showProgressDialog();
            LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button_fb);
            mButtonLogin.setReadPermissions("public_profile", "email");
            mButtonLogin.registerCallback(callbackManager, mCallBack);
        } else {
            Toast.makeText(this, R.string.internetError, Toast.LENGTH_SHORT).show();
        }
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
//            mStatusTextView.setText(constructWelcomeMessage(profile));
            hideProgressDialog();
        }


        @Override
        public void onCancel() {
            hideProgressDialog();
        }

        @Override
        public void onError(FacebookException e) {
            hideProgressDialog();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                mStatusTextView.setText(constructWelcomeMessage(currentProfile));
                if (currentProfile == null) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(loginStatus, false);
                    editor.putString(userName, getString(R.string.userInfoName));
                    editor.putString(userAvatar, "none");
                    mStatusTextView.setText(R.string.userInfoName);
                    spok.setText(" 0");
                    thanksGiving.setText(" 0");
                    Picasso.with(AccountActivity.this).load(R.drawable.avatar).into(imgProfilePic);
                    editor.apply();
                    hideProgressDialog();
                    updateUI(false);
                }
            }
        };
    }

    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            Log.d("dasd", profile.getName());
            stringBuffer.append(profile.getName());
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(loginStatus, true);
            editor.putString(userName, profile.getName());
            if (profile.getLinkUri() != null)
                editor.putString(userAvatar, profile.getProfilePictureUri(100, 100).toString());
            editor.putString(userId, profile.getId());
            editor.putString(gOrFb, "fb");
            editor.commit();
            db.checkUserData("customId = '" + profile.getId() + "'");
            Picasso.with(AccountActivity.this)
                    .load(profile.getProfilePictureUri(100, 100).toString())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imgProfilePic);
            updateUI(true);
        }
        return stringBuffer.toString();
    }

    //GOOGLE SIGN IN
    //===================================================================================
    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {

            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "" + result.isSuccess());

        if (result.isSuccess()) {


            SharedPreferences.Editor editor = sharedpreferences.edit();
            GoogleSignInAccount acct = result.getSignInAccount();
            String avatarUrl = "avatar";
            if (acct.getPhotoUrl() != null)
                avatarUrl = acct.getPhotoUrl().toString();
            editor.putBoolean(loginStatus, true);
            editor.putString(userName, acct.getDisplayName());
            editor.putString(userAvatar, avatarUrl);
            editor.putString(userId, acct.getId());
            editor.putString(gOrFb, "g");
            editor.commit();
            db.checkUserData("userId = '" + acct.getId() + "'");
            Log.d(TAG, "Login as:" + acct.getDisplayName());
            mStatusTextView.setText(acct.getDisplayName());
            Picasso.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imgProfilePic);
            updateUI(true);
        } else if (sharedpreferences.getBoolean(loginStatus, false)) {
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]

                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //PROGRESS
    //===================================================================================
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //UI UPDATE
    //===================================================================================
    private void updateUI(boolean signedIn) {
        if (signedIn && sharedpreferences.getString(gOrFb, " ").equals("g")) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else if (signedIn && sharedpreferences.getString(gOrFb, " ").equals("fb")) {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_google).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.orT).setVisibility(View.GONE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            findViewById(R.id.sign_in_google).setVisibility(View.VISIBLE);
            findViewById(R.id.orT).setVisibility(View.VISIBLE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(loginStatus, false);
            editor.putString(userName, getString(R.string.userInfoName));
            editor.putString(userAvatar, "none");
            mStatusTextView.setText(R.string.userInfoName);
            editor.putString(gOrFb, "none");
            spok.setText(" 0");
            thanksGiving.setText(" 0");
            Picasso.with(this).load(R.drawable.avatar).into(imgProfilePic);
            editor.apply();
        }
    }

    //BUTTONS
    //===================================================================================
    @Override
    public void onClick(View v) {
        if (isNetworkAvailable()) {
            switch (v.getId()) {
                case R.id.sign_in_google:

                    signIn();
                    break;
                case R.id.sign_out_button:
                    signOut();
                    break;
            }
        } else
            Toast.makeText(this, R.string.internetError, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    //USER INFO METHODS
    //===================================================================================
    public void saveUserInfo(String infoID) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(custId, infoID);
        editor.apply();
    }

    public void saveNewestUser() {
        UserData userNew = new UserData();
        if (sharedpreferences.getString(gOrFb, "fb").equals("g"))
            userNew.setUserId(sharedpreferences.getString(userId, ""));
        if (sharedpreferences.getString(gOrFb, "g").equals("fb"))
            userNew.setCustomId(sharedpreferences.getString(userId, ""));
        userNew.setUserName(sharedpreferences.getString(userName, ""));
        userNew.setUserSpots(0);
        userNew.setUserThanks(0);
        userNew.setUserUrl(sharedpreferences.getString(userAvatar, ""));
        db.setNewUserData(userNew);
    }

    public void updateCurrentUser(UserData userfellow) {
        if (sharedpreferences.getBoolean(loginStatus, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(userTags, userfellow.getUserSpots());
            editor.putInt(userThanks, userfellow.getUserThanks());
            editor.putString(custId, userfellow.getObjectId());
            editor.apply();
            spok.setText(" " + userfellow.getUserSpots());
            thanksGiving.setText(" " + userfellow.getUserThanks());
        }
    }

}