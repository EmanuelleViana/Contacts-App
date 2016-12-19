package com.emanuelle.easycontacts.ui;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emanuelle.easycontacts.*;
import com.emanuelle.easycontacts.R;
import com.facebook.*;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;

public class DataFacebookActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    ProfilePictureView profilePicture;
    private String nome="",
            id="",
            email="",
            genero="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(com.emanuelle.easycontacts.R.layout.activity_data_facebook);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

    @Override
    public void onResume(){
        super.onResume();
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login);

        final TextView idTv = (TextView) findViewById(R.id.tvId);
        final TextView idE = (TextView) findViewById(R.id.tvEmail);
        final TextView idN = (TextView) findViewById(R.id.tvNome);
        final TextView idG = (TextView) findViewById(R.id.tvGenre);


        setTitle("Perfil");
        profilePicture = (ProfilePictureView) findViewById(R.id.fbImg);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_birthday"));

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        nome = myPrefs.getString("nome", "nothing");
        id = myPrefs.getString("id", "nothing");
        genero = myPrefs.getString("genero", "nothing");
        email = myPrefs.getString("email", "nothing");

        System.out.println(nome+id+genero+email);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    idTv.setText(profile.getId());
                    idN.setText(profile.getName());
                }

                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {


                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        System.out.println(object.toString());

                        try {
                            id = object.getString("id").toString();
                            nome = object.getString("name").toString();
                            email = object.getString("email").toString();
                            genero = object.getString("gender").toString();
                            profilePicture.setProfileId(id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Algumas informações não puderam ser carregadas", Toast.LENGTH_LONG).show();

                        }

                    }
                });



                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Verifique sua conexão de internet", Toast.LENGTH_LONG).show();

            }
        });

       AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
           @Override
           protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                      AccessToken currentAccessToken) {
               if (currentAccessToken == null) {
                   idTv.setText("null");
                   idN.setText("NoName");
                   idE.setText("Faça Login");
                   idG.setText("Faça Login");
                   profilePicture.setProfileId(null);

               }
           }
       };


        idTv.setText(id);
        idN.setText(nome);
        idE.setText(email);
        idG.setText(genero);
        profilePicture.setProfileId(id);


    }


    @Override
    public void onSaveInstanceState(Bundle out){
        out.putString("nome",nome);
        out.putString("id",id);
        out.putString("email",email);
        out.putString("genero",genero);

        super.onSaveInstanceState(out);

    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("nome", nome);
        prefsEditor.putString("id", id);
        prefsEditor.putString("email", email);
        prefsEditor.putString("genero", genero);
        prefsEditor.commit();
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        nome = savedInstanceState.getString("nome");
        id = savedInstanceState.getString("id");
        email = savedInstanceState.getString("email");
        genero = savedInstanceState.getString("genero");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
