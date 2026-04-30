package com.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.shoppinglist.R;
import com.shoppinglist.utils.LocaleHelper;

public class WelcomeActivity extends AppCompatActivity {
    public static final String EXTRA_SHOW_REGISTER = "show_register";

    @Override
    protected void attachBaseContext(Context newBase) {
        // Apply saved locale to this activity's context
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Get views
        ImageView ivLogo = findViewById(R.id.iv_logo);
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvTagline = findViewById(R.id.tv_tagline);
        MaterialButton btnLogin = findViewById(R.id.btn_login);
        MaterialButton btnRegister = findViewById(R.id.btn_register);

        // Load animations
        Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        Animation fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUpAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Apply animations
        ivLogo.startAnimation(bounceAnim);
        tvAppName.startAnimation(fadeInAnim);
        tvTagline.startAnimation(fadeInAnim);
        btnLogin.startAnimation(slideUpAnim);
        btnRegister.startAnimation(slideUpAnim);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, AuthActivity.class);
            intent.putExtra(EXTRA_SHOW_REGISTER, false);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, AuthActivity.class);
            intent.putExtra(EXTRA_SHOW_REGISTER, true);
            startActivity(intent);
        });
    }
}
