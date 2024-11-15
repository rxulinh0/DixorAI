package com.ai.dixorai;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.card.MaterialCardView;

public class getting_started_a extends AppCompatActivity {
    private MaterialCardView card_button;
    private LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_getting_started);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView gradientTextView = findViewById(R.id.getting_started_a_main_title_tv);

        // Set up the gradient colors
        int startColor = getResources().getColor(R.color.main_blue);
        int endColor = getResources().getColor(R.color.teal_700);

        // Create a LinearGradient shader
        LinearGradient linearGradient = new LinearGradient(
                0, 0, 0, gradientTextView.getTextSize(),
                startColor, endColor,
                Shader.TileMode.REPEAT);

        // Apply the shader to the TextView's paint
        gradientTextView.getPaint().setShader(linearGradient);

//        TextView main_title_tv = findViewById(R.id.getting_started_a_main_title_tv);
//        Paint paint = main_title_tv.getPaint();
//        float width = paint.measureText(main_title_tv.getText().toString());
//        main_title_tv.getPaint().setShader(new LinearGradient(
//                0f, 0f, width, main_title_tv.getTextSize(), new int[]{
//                Color.parseColor("#FFFFFF"),
//                Color.parseColor("#8195CB"),
//                Color.parseColor("#9aaad6"),
//        }, null, Shader.TileMode.REPEAT
//        ));
        // Lottie AnimationView
        animationView = findViewById(R.id.getting_started_animationView);
        // Start the animation
        animationView.playAnimation();
        // Optionally, control speed
        animationView.setSpeed(1.5f);  // 1.0 is normal speed
        // Loop the animation
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        // .....
        card_button = findViewById(R.id.getting_started_a_next_button);
        card_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card_button.setClickable(false);
                Intent new_intent = new Intent(getting_started_a.this,getting_started_b.class);
                startActivity(new_intent);
            }
        });
    }
}