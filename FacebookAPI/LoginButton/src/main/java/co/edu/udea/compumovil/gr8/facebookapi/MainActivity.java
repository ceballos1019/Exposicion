package co.edu.udea.compumovil.gr8.facebookapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private LoginButton loginButton;
    ProfileTracker profileTracker;
    TextView tvNombre;
    ImageView ivImagen;
    Bitmap imageProfile;
    AsyncTask<String, Void, Bitmap> asyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));

        tvNombre = (TextView) findViewById(R.id.nombre);
        ivImagen = (ImageView) findViewById(R.id.imagen);

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Login Successfull", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login  attempt canceled", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Login attempt failed", Toast.LENGTH_LONG).show();

            }
        });

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                if(currentProfile!=null){
                    tvNombre.setText(currentProfile.getName());
                    Uri uri = currentProfile.getProfilePictureUri(100, 100);
                    
                    asyncTask.execute(uri.toString()); //Descargar imagen
                }else{
                    tvNombre.setText("Not user on");
                    ivImagen.setImageBitmap(null);
                }
            }
        };

        asyncTask = new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                URL imageUrl;
                Bitmap imagen = null;
                try {
                    imageUrl = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection)
                            imageUrl.openConnection();
                    conn.connect();
                    imagen = BitmapFactory. decodeStream(conn.getInputStream());
                } catch (IOException e) {
                    Toast. makeText (getApplicationContext(), "Error cargando la imagen: " +
                            e.getMessage(), Toast. LENGTH_LONG ).show();
                    e.printStackTrace();
                }
                return imagen;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                ivImagen.setImageBitmap(bitmap);
            }
        };




        /*
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    private void getAppHashKey(){
         /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "co.edu.udea.compumovil.gr8.facebookapi",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        */
    }
}
