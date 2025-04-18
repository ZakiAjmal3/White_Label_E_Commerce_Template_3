package com.example.whitelabeltemplate3.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.whitelabeltemplate3.R;

public class OrderPlacedActivity extends AppCompatActivity {
    ImageView orderSuccessIV;
    Button gotoMyOrders;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            EdgeToEdge.enable(this);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
//        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        orderSuccessIV = findViewById(R.id.orderSuccessIV);
        gotoMyOrders = findViewById(R.id.gotoMyOrders);

        // Delay for animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the animation
                Animation logo_object = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.main_activity_logo_rotation);
                orderSuccessIV.startAnimation(logo_object);
            }
        }, 0);


        // Initialize MediaPlayer with the short sound file
        mediaPlayer = MediaPlayer.create(this, R.raw.order_placed_audio);
        mediaPlayer.start(); // Play the sound

        // Release the media player after completion
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release(); // Free up resources
            }
        });

        gotoMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderPlacedActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}