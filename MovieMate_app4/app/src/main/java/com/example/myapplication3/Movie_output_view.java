package com.example.myapplication3;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import android.net.Uri;


public class Movie_output_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_output_view);
        ImageView popcornImage = findViewById(R.id.popchoice_icon);
        Animation SHAKING = AnimationUtils.loadAnimation(this, R.anim.shaking);
        popcornImage.startAnimation(SHAKING);
        TextView Again = findViewById(R.id.go_again);
        Again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResetRequestToServer();

                startActivity(new Intent(Movie_output_view.this, HomePage.class));
            }
        });
        TextView githubLink = findViewById(R.id.github_link);
        TextView linkedinLink = findViewById(R.id.linkedin_link);

        githubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/AymenLass";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        linkedinLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://linkedin.com/in/aymen-lassoued";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void sendResetRequestToServer() {
        String url = "http://192.168.0.25:5000/reset_server";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("Failed to reset server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    showToast("Welcome Back !");
                } else {
                    showToast("Failed to reset server");
                }
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Movie_output_view.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}