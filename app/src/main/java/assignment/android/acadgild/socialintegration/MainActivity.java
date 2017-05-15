package assignment.android.acadgild.socialintegration;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/*Open Android Studio and Create a new Project.Created project SocialIntegration.
  Before moving ahead to the android project,create a project on Google Developers Console
Log in to Google Plus account and go to this link and click on create project.
Now fill the details and click on create.
Now we need a configuration file for our android app.
Select your app we just created on developer console.
 And write the package name of your android studio project we created. Finally choose country and click on continue.

* */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {
private SignInButton signInButton;//Button designed for google signin
   // GoogleSignInOptions is options used to configure the GOOGLE_SIGN_IN_API.
    private GoogleSignInOptions googleSignInOptions;
    //GoogleApiClient is used with a variety of static methods. Some of these methods require that GoogleApiClient be connected, some will queue up calls before GoogleApiClient is connected; check the specific API documentation to determine whether you need to be connected.
    private GoogleApiClient googleApiClient;
    private int SIGN_IN=101;
    private TextView txtViewResult;
    //FaceBook
    CallbackManager callbackManager;
    private LoginButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_main);
        txtViewResult=(TextView)findViewById(R.id.txtViewResult);
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
// basic profile are included in DEFAULT_SIGN_IN.
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        signInButton=(SignInButton)findViewById(R.id.signin_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(googleSignInOptions.getScopeArray());
        //Initializing google api client
        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
        signInButton.setOnClickListener(this);
        //Facebook
        loginButton = (LoginButton)findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        //create a callback to handle the results of the login attempts and register it with the CallbackManager.
        // Custom callbacks should implement FacebookCallback. The interface has methods to handle each possible outcome of a login attempt:
        //If the login attempt is successful, onSuccess is called.
        //If the user cancels the login attempt, onCancel is called.
        //If an error occurs, onError is called.
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // Toast.makeText(FacebookActivity.this, "Login Successfull!"+loginResult.getAccessToken().getToken(), Toast.LENGTH_LONG).show();
                GraphRequest request=GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object , GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                    System.out.println("ERROR");
                                } else {
                                    System.out.println("Success");
                                    try {
                                        ///Profile profile = Profile.getCurrentProfile();

                                        String jsonresult = String.valueOf(object);
                                        System.out.println("JSON Result"+jsonresult);
                                        String firstname = object.getString("first_name");
                                        String lastname=object.getString("last_name");
                                        String gender=object.getString("gender");
                                        String email = object.getString("email");
                                        String birthday=object.getString("birthday");
                                        String userId=loginResult.getAccessToken().getUserId();
                                        String authToken=loginResult.getAccessToken().getToken();
                                        Profile profile = Profile.getCurrentProfile();
                                       // String id = profile.getId();
                                       // String link = profile.getLinkUri().toString();
                                        Uri profilePicture=Profile.getCurrentProfile().getProfilePictureUri(200, 200);
                                        txtViewResult.setText("Facebook Details:"+"\n"+"UserId:"+userId+"\n"+"Authenication Token:"+authToken+"\n\n" +
                                                ""+"FirstName:" +firstname+"\n"+"Last Name:"+lastname+"\n"+"Gender:"+gender+"\n"
                                                +"Birthday:"+birthday+"\n"+"Email:"+email+"\n"+"Profile Picture Path:"+profilePicture);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                            }
                        }) ;

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login cancelled", Toast.LENGTH_LONG).show();



            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();

            }
        });
    }
    protected void facebookSDKInitialize() {/**
     * This function initializes the Facebook SDK, the behavior of Facebook SDK functions are
     * undetermined if this function is not called. It should be called as early as possible.
     * @param applicationContext The application context
     */
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();// initialize your instance of CallbackManager using the CallbackManager.Factory.create method.
    }
    private void signOut(){
        LoginManager.getInstance().logOut();
    }
    @Override
    public void onClick(View view) {
        if(view==signInButton)
        {
        signIn();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    //This function will option signing intent
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        //Starting intent for result
        startActivityForResult(signInIntent, SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
            }

    }
    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            //Displaying name and email
            txtViewResult.setText("Google Account Details:"+"\n"+"Name:"+acct.getDisplayName()+"\n"+"Email:"+acct.getEmail()+"\n"+"Id:"+acct.getId()+"\n"
                    +"\n"+"PhotoUrl:"+acct.getPhotoUrl());


            } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            }
    }
}
