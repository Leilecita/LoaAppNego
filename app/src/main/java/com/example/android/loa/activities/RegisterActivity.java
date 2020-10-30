package com.example.android.loa.activities;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.loa.R;
import com.example.android.loa.data.SessionPrefs;
import com.example.android.loa.network.ApiClient;
import com.example.android.loa.network.Error;
import com.example.android.loa.network.GenericCallback;
import com.example.android.loa.network.models.User;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private AutoCompleteTextView mUserIdView;
    private EditText mPasswordView;
    private EditText mNameView;
    private EditText mPhoneView;
    private EditText mKey;
    private View mProgressView;
    private View mLoginFormView;

    private TextInputLayout mFloatLabelUserId;
    private TextInputLayout mFloatLabelPassword;
    private TextInputLayout mFloatLabelName;
    private TextInputLayout mFloatLabelPhone;
    private TextInputLayout mFloatLabelKey;

    private LinearLayout home;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        home = findViewById(R.id.line_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mFloatLabelUserId =  findViewById(R.id.float_label_user_id);
        mFloatLabelPassword =  findViewById(R.id.float_label_password);
        mFloatLabelName =  findViewById(R.id.float_label_name);
        mFloatLabelPhone =  findViewById(R.id.float_label_phone);
        mFloatLabelKey =  findViewById(R.id.float_label_key);
        mUserIdView =  findViewById(R.id.email);
        mPasswordView =  findViewById(R.id.password);
        mNameView =  findViewById(R.id.name);
        mPhoneView =  findViewById(R.id.phone);
        mKey =  findViewById(R.id.key);

        Button mRegister =  findViewById(R.id.register);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (!isOnline()) {
                    showLoginError("Error de red");
                    return;
                }*/
                attemptLogin();
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    private void attemptLogin() {

        // Reset errors.
        mFloatLabelUserId.setError(null);
        mFloatLabelPassword.setError(null);
        mFloatLabelName.setError(null);
        mFloatLabelPhone.setError(null);
        mFloatLabelKey.setError(null);

        // Store values at the time of the login attempt.
        String userId = mUserIdView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();
        String phone = mPhoneView.getText().toString();
        String key = mKey.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mFloatLabelPassword.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mFloatLabelPassword.setError(getString(R.string.error_invalid_password));
            focusView = mFloatLabelPassword;
            cancel = true;
        }

        // Verificar si el ID tiene contenido.
        if (TextUtils.isEmpty(userId)) {
            mFloatLabelUserId.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelUserId;
            cancel = true;
        } else if (!isUserIdValid(userId)) {
            mFloatLabelUserId.setError("Error usuario invalido");
            focusView = mFloatLabelUserId;
            cancel = true;
        }

        if(TextUtils.isEmpty(name)){
            mFloatLabelName.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelName;
            cancel = true;
        }

        if(TextUtils.isEmpty(phone)){
            mFloatLabelPhone.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelPhone;
            cancel = true;
        }


        if(TextUtils.isEmpty(key)){
            mFloatLabelKey.setError("LLave de ingreso incorrecta");
            focusView = mFloatLabelKey;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {

            showProgress(true);
            final User u=new User(mNameView.getText().toString().trim(),mPasswordView.getText().toString().trim(),
                    mUserIdView.getText().toString().trim(),mPhoneView.getText().toString().trim(),"");

            ApiClient.get().register(u, key, new GenericCallback<User>() {
                @Override
                public void onSuccess(User data) {
                    finish();
                    showProgress(false);

                    Toast.makeText(getBaseContext(),"el usuario ha sido registrado", Toast.LENGTH_SHORT).show();

                    SessionPrefs.get(RegisterActivity.this).saveUser(u);
                }

                @Override
                public void onError(Error error) {
                    showProgress(false);
                    Toast.makeText(getBaseContext(),error.message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private boolean isUserIdValid(String userId) {
        return userId.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        mLoginFormView.setVisibility(visibility);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUserIdView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
   /* private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
