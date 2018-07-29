/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Boolean signUpModeActive = true;

    TextView changeSignupModeTextView;

    EditText passwordEditText;

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

            signUp(view);

        }

        return false;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.changeSignupModeTextView) {

            Button signupButton = (Button) findViewById(R.id.signupButton);

            if (signUpModeActive) {

                signUpModeActive = false;
                signupButton.setText("Login");
                changeSignupModeTextView.setText("Or, Signup");

            } else {

                signUpModeActive = true;
                signupButton.setText("Signup");
                changeSignupModeTextView.setText("Or, Login");

            }

        } else if (view.getId() == R.id.backgroundRelativeLayout || view.getId() == R.id.logoImageView) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        }

    }

    public void signUp(View view) {

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);



        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {

            Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();

        } else {

            if (signUpModeActive) {

                ParseUser user = new ParseUser();

                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            Log.i("Signup", "Successful");

                        } else {

                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            } else {

                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (user != null) {

                            Log.i("Signup", "Login successful");

                        } else {

                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }
                });


            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        changeSignupModeTextView = (TextView) findViewById(R.id.changeSignupModeTextView);

        changeSignupModeTextView.setOnClickListener(this);

        RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.backgroundRelativeLayout);

        ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);

        backgroundRelativeLayout.setOnClickListener(this);

        logoImageView.setOnClickListener(this);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        passwordEditText.setOnKeyListener(this);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }


}






/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.

package com.parse.starter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;
import java.util.Objects;

import static com.google.android.gms.analytics.internal.zzy.i;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    Boolean signUpModeActive = true;

    TextView changeSignupModeTextView;

    EditText passwordEditText;

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){

            signUp(view);

        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.changeSignupModeTextView) {
            //Log.i("AppInfo", "Change Signup Mode");

            Button signUpButton = (Button) findViewById(R.id.signupButton);

            if (signUpModeActive){
                signUpModeActive = false;
                assert signUpButton != null;
                signUpButton.setText("Login");
                changeSignupModeTextView.setText("or, Sign Up");
            }else{
                signUpModeActive = true;
                assert signUpButton != null;
                signUpButton.setText("Sign Up");
                changeSignupModeTextView.setText("or, Login");
            }
        } else if (view.getId() == R.id.backgroundRelativeLayout || view.getId() == R.id.logoImageView) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);

        }
    }

    public void signUp(View view) {

        EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        //EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        assert usernameEditText != null;
        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "A username and password are required", Toast.LENGTH_SHORT).show();
        } else {

            if (signUpModeActive) {


                ParseUser user = new ParseUser();

                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Signup", "Successful");
                        } else {

                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else {
                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (user !=null){
                            Log.i("Signup", "Login successful");
                        }else{
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        }
    }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


  /*  ParseObject Balance = new ParseObject("Balance");
      Balance.put("username", "T'Challa");
      Balance.put("Balance", 1000);
      Balance.saveInBackground(new SaveCallback() {
          @Override
          public void done(ParseException e) {

              if (e == null) {
                  Log.i("SavedInBackground", "Successful");

              }else {
                  Log.i("SaveInBackground", "Failed. Error: " + e.toString());
              }

          }
      });
*/

    /*
      ParseQuery<ParseObject> query = ParseQuery.getQuery("Balance");
      query.getInBackground("Rw92rODaWe", new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject object, ParseException e) {
              if (e == null && object !=null){

                  object.put("Balance", 1000);
                  object.saveInBackground();

                  Log.i("objectValue", object.getString("username"));
                      Log.i("objectValue", Integer.toString(object.getInt("Balance")));

              }
          }
      });
*/

/*
    ParseObject Balance1 = new ParseObject("Balance1");
    Balance1.put("username", "Ben");
    Balance1.put("Balance1", 100);


    Balance1.saveInBackground(new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e == null){
                Log.i("Balance1", "successful");
            }else{
                Log.i("Balance1", "Failed");
            }
        }
    });
*/
/*
//take 100 from Tchalla account.
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Balance");
    query.whereGreaterThanOrEqualTo("Balance", 100);
    query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> objects, ParseException e) {
            if (e == null && objects != null){
                for (ParseObject object :objects){
                    object.put("Balance", object.getInt("Balance") - 100);
                    object.saveInBackground();
                }
            }
        }
    });
    */


//CREATE AND SIGN UP A USER.
  /*    ParseUser user = new ParseUser();

      user.setUsername("David");
      user.setPassword("myPass");


      user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
              if (e == null){
                  Log.i("Sign Up", "Success");
              }else {
                  Log.i("Sign Up", "Failed");
              }
          }
      });


      */


  //loging users

/*
      ParseUser.logInInBackground("David", "myPass", new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
              if ((user != null)) {

                  Log.i("Login", "Success");
              }else{
                  Log.i("Login", "Fail" + e.toString());
              }
          }
      });
*/
      //log out user
/*
      ParseUser.logOut();



      //check if user is loged in

      if (ParseUser.getCurrentUser() != null) {

          Log.i("currentUser", "User logged in " + ParseUser.getCurrentUser().getUsername());

      } else {

          Log.i("currentUser", "User not logged in");

      }

      //TextView changeSignupModeTextView = (TextView) findViewById(R.id.changeSignupModeTextView);
      changeSignupModeTextView = (TextView) findViewById(R.id.changeSignupModeTextView);

      assert changeSignupModeTextView != null;
      changeSignupModeTextView.setOnClickListener(this);

      RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.backgroundRelativeLayout);
      ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);

      assert backgroundRelativeLayout != null;
      backgroundRelativeLayout.setOnClickListener(this);
      assert logoImageView != null;
      logoImageView.setOnClickListener(this);

      passwordEditText = (EditText) findViewById(R.id.passwordEditText);

      assert passwordEditText != null;
      passwordEditText.setOnKeyListener(this);
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }


}
*/
