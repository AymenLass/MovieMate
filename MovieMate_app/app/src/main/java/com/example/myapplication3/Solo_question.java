package com.example.myapplication3;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Solo_question extends AppCompatActivity {


    EditText favorite, mood, f_o_s ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_question);

        favorite = findViewById(R.id.the_shawshank_redemption_because_it_taught_me_to_never_give_up_hope_no_matter_how_hard_life_gets);
        mood = findViewById(R.id.i_want_to_watch_movies_that_were_released_after_1990);
        f_o_s = findViewById(R.id.i_want_to_watch_something_stupid_and_fun);
    }

//this function is called when you click on lets go on the page it should have the same name
//            give on teh onclick you can click on usage to findout where it was called
    public void let_s_go(View view) {
        Log.d("solo_watch", "letsgo() method called");
        String fav = favorite.getText().toString();
        String currentMood = mood.getText().toString();
        String favoriteScene = f_o_s.getText().toString();

        Log.d("solo_watch", "Favorite: " + fav);
        Log.d("solo_watch", "Current Mood: " + currentMood);
        Log.d("solo_watch", "Favorite Scene: " + favoriteScene);
        if (!fav.isEmpty() && !currentMood.isEmpty() && !favoriteScene.isEmpty()) {
            submitForm();
//            startActivity(new Intent(Solo_question.this, Movie_output_view.class));
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            Toast.makeText(Solo_question.this, "End of questions", Toast.LENGTH_SHORT).show();
            sendTriggerRequestToServer();
            ProgressDialog progressDialog = ProgressDialog.show(Solo_question.this, "", "Loading...", true);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    startActivity(new Intent(Solo_question.this, Movie_output.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }, 60000); // 1 minute delay (60000 milliseconds)
        }
         else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendTriggerRequestToServer() {

        String url = "http://192.168.0.25:5000/trigger_main";
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), "");
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
            }
        });
    }
    public void submitForm() {
        String fav = favorite.getText().toString();
        String currentMood = mood.getText().toString();
        String favoriteScene = f_o_s.getText().toString();
        Map<String, String> answerMap = new HashMap<>();
        answerMap.put("Favorite Movie", fav);
        answerMap.put("Current Mood", currentMood);
        answerMap.put("Favorite Scene", favoriteScene);

        sendFileToFlask(answerMap);
    }
    private void sendFileToFlask(Map<String, String> data) {
        // Get the content of NumberOfWatchers and Time
//        String fav = favorite.getText().toString();
//        String currentMood = mood.getText().toString();
//        String favoriteScene = f_o_s.getText().toString();

        StringBuilder fileContent = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            fileContent.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp_file2", ".txt", getCacheDir());
            FileWriter writer = new FileWriter(tempFile);
            writer.write(fileContent.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        if (tempFile != null) {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "user_answer.txt", RequestBody.create(MediaType.parse("text/plain"), tempFile))
                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.0.25:5000/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("SoloWatch", "File uploaded successfully");
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("SoloWatch", "Error uploading file: " + e.getMessage());
                }
            });
        }
    }
}
//Save on phone
//    private void saveDataToFile(Map<String, String> data) {
//        String filename = "user_answer.txt";
//        FileOutputStream outputStream;
//
//        try {
//            // Get the path to the internal storage directory
//            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//            File file = new File(directory, filename);
//
//            // Write data to the file
//            outputStream = new FileOutputStream(file);
//            for (Map.Entry<String, String> entry : data.entrySet()) {
//                String line = entry.getKey() + ": " + entry.getValue() + "\n";
//                outputStream.write(line.getBytes());
//            }
//            outputStream.close();
//
//            Log.d("solo", "Data saved to " + file.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("solo", "Error saving data to file: " + e.getMessage());
//        }
//    }

