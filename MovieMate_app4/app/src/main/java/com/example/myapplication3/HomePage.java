package com.example.myapplication3;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.FileWriter;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class HomePage extends AppCompatActivity {

    EditText NumberOfWatchers, Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        NumberOfWatchers = findViewById(R.id.__how_many_people_);
        Time = findViewById(R.id.__how_much_time_do_you_have_);
        ImageView popcornImage = findViewById(R.id.popchoice_icon);
        Animation SHAKING = AnimationUtils.loadAnimation(this, R.anim.shaking);
        popcornImage.startAnimation(SHAKING);
    }

    public void start(View view) {

        EditText inputNumber = findViewById(R.id.__how_many_people_);
        String input = inputNumber.getText().toString();


        if (!input.isEmpty()) {
            int numberOfPersons = Integer.parseInt(input);

            if (numberOfPersons == 1) {
                startActivity(new Intent(this, Solo_question.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                Intent intent = new Intent(this, N_question.class);
                intent.putExtra("pageNumber", 1);
                intent.putExtra("numberOfPersons", numberOfPersons);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            submitForm();
        } else {

            Toast.makeText(HomePage.this, "Please enter the number of persons", Toast.LENGTH_SHORT).show();
            Toast.makeText(HomePage.this, "Please enter the film maximum duration", Toast.LENGTH_SHORT).show();

        }
    }

    public void submitForm() {
        String NumberOfWatchers_S = NumberOfWatchers.getText().toString();
        String Time_S = Time.getText().toString();

        Map<String, String> answerMap = new HashMap<>();
        answerMap.put("They are ", NumberOfWatchers_S +" persons .");
        answerMap.put("The watchers wants to watch a moviein less than ", Time_S+" hours .");

        sendFileToFlask(answerMap);
    }

    private void sendFileToFlask(Map<String, String> data) {
        String NumberOfWatchers_S = NumberOfWatchers.getText().toString();
        String Time_S = Time.getText().toString();

        StringBuilder fileContent = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            fileContent.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
//        fileContent.append("Number persons who are going to watch the movie: ").append(NumberOfWatchers_S).append(" persons\n");
//        fileContent.append("How much time in hours the watchers have to watch this movie? ").append(Time_S).append(" hours\n");

        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp_file", ".txt", getCacheDir());
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
                    .addFormDataPart("file", "number_time.txt", RequestBody.create(MediaType.parse("text/plain"), tempFile))
                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.0.25:5000/upload")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("MainActivity", "File uploaded successfully");
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("MainActivity", "Error uploading file: " + e.getMessage());
                }
            });
        }
    }

}
//public class first_page extends AppCompatActivity {
//
//    EditText favorite, mood, f_o_s;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        favorite = findViewById(R.id.the_shawshank_redemption_because_it_taught_me_to_never_give_up_hope_no_matter_how_hard_life_gets);
//        mood = findViewById(R.id.i_want_to_watch_movies_that_were_released_after_1990);
//        f_o_s = findViewById(R.id.i_want_to_watch_something_stupid_and_fun);
//    }
//
//
//
//    public void start(View view) {
//        String fav = favorite.getText().toString();
//        String currentMood = mood.getText().toString();
//        String favoriteScene = f_o_s.getText().toString();
//
//
//        Log.d("solo", "Favorite: " + fav);
//        Log.d("solo", "Current Mood: " + currentMood);
//        Log.d("solo", "Favorite Scene: " + favoriteScene);
//        if (!fav.isEmpty() && !currentMood.isEmpty() && !favoriteScene.isEmpty()) {
//            submitForm(fav, currentMood, favoriteScene);
//            // Start the next activity after data submission
//            startActivity(new Intent(this, first_question.class));
//        } else {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void submitForm(String fav, String currentMood, String favoriteScene) {
//        Map<String, String> answerMap = new HashMap<>();
//        answerMap.put("Favorite Movie", fav);
//        answerMap.put("Current Mood", currentMood);
//        answerMap.put("Favorite Scene", favoriteScene);
//
//        // Create text file and store data
//        saveDataToFile(answerMap);
//    }
//
//    private void saveDataToFile(Map<String, String> data) {
//        String filename = "user_data.txt";
//        FileOutputStream outputStream;
//
//        try {
//            // Get the path to the internal storage directory
//            File directory = getFilesDir();
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
//}
