package com.example.florian.app;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;

import services.ApiServiceInterface;
import services.RouterService;
import services.UserManager;

public class MainActivity extends AppCompatActivity {

    private boolean loginMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginMode = true;

        (findViewById(R.id.validUser)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showLoader();
                if (loginMode) {
                    connect(((EditText) findViewById(R.id.username)).getText().toString(),
                            ((EditText) findViewById(R.id.password)).getText().toString());
                } else {
                    createUser(((EditText) findViewById(R.id.username)).getText().toString(),
                            ((EditText) findViewById(R.id.password)).getText().toString(),
                            ((EditText) findViewById(R.id.passwordVerif)).getText().toString());
                }
            }
        });

        findViewById(R.id.createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateAccount();
            }
        });

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogin();
            }
        });
    }


    private void createUser(final String username, final String plainPassword, String plainPasswordVerif) {
        if (plainPassword.equals("") || !plainPassword.equals(plainPasswordVerif)) {
            hideLoader();
            showDialogue(this, R.string.notSamePassword);
        } else {
            ApiServiceInterface apiService = ApiService.getService();
            Call<Boolean> call = apiService.createUser(new User(username, plainPassword, plainPasswordVerif));
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.code() == 200  && response.body() == true) {
                        connect(username, plainPassword);
                    } else {
                        showDialogue(MainActivity.this, R.string.userNotAvailable);
                        hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    ApiService.showErrorMessage(MainActivity.this);
                    hideLoader();
                }
            });
        }
    }

    private void showErrorMessage(String[] strings) {
        String message = "";
        for (String string : strings) {
            message = message + string + '\n';
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.error);
        builder.setMessage(message);
        builder.setIcon(R.drawable.sablier);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void getUser(final boolean fromConnection) {
        ApiServiceInterface apiService = ApiService.getService();
        Call<User> call = apiService.getUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (ApiService.checkCode(MainActivity.this, response) && response.body() != null) {
                    if (fromConnection) {
                        UserManager.saveUser(response.body());
                    }
                    RouterService.goHome(MainActivity.this, response.body());
                    MainActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ApiService.showErrorMessage(MainActivity.this);
            }
        });
    }

    private void connect(String username, String plainPassword) {
        ApiServiceInterface apiService = ApiService.getService();
        Call<User> call = apiService.connect(username, plainPassword);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200) {
                    UserManager.savePHPSESSID(response);
                    getUser(true);
                } else {
                    hideLoader();
                    showErrorMessage(new String[]{getString(R.string.badCredentials)});
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ApiService.showErrorMessage(MainActivity.this);
                hideLoader();
            }
        });
    }

    private void showLoader() {
        findViewById(R.id.validUser).setVisibility(View.GONE);
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        findViewById(R.id.validUser).setVisibility(View.VISIBLE);
        findViewById(R.id.loader).setVisibility(View.GONE);
    }

    private void showDialogue(Activity activity, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(messageId);
        builder.setIcon(R.drawable.sablier);
        hideLoader();
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void setDrawable(View view, int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setBackground(getDrawable(drawableId));
        } else {
            view.setBackground(getResources().getDrawable(drawableId));
        }
    }

    private void showCreateAccount() {
        setDrawable(this.findViewById(R.id.createButton), R.drawable.button_shape);
        setDrawable(this.findViewById(R.id.loginButton), R.drawable.button_off_shape);
        this.findViewById(R.id.passwordVerif).setVisibility(View.VISIBLE);
        loginMode = false;
    }

    private void showLogin() {
        setDrawable(this.findViewById(R.id.loginButton), R.drawable.button_shape);
        setDrawable(this.findViewById(R.id.createButton), R.drawable.button_off_shape);
        this.findViewById(R.id.passwordVerif).setVisibility(View.GONE);
        loginMode = true;
    }
}