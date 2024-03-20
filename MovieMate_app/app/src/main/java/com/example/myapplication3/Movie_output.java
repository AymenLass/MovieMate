package com.example.myapplication3;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;


public class Movie_output extends AppCompatActivity {

    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView answerTextView;
    private TextView releaseDateTextView;
    private TextView overviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_output);

        // Initialize UI elements
        posterImageView = findViewById(R.id.image_5);
        titleTextView = findViewById(R.id.the_martian__2015_);
        answerTextView = findViewById(R.id.the_inspiring_story_of_an_astronaut_stranded_on_mars_who_needs_to_rely_on_his_ingenuity_to_come_back_to_earth);
        // releaseDateTextView = findViewById(R.id.releaseDateTextView);
        // overviewTextView = findViewById(R.id.overviewTextView);

        // Start downloading files when the activity is created
        new DownloadFilesTask().execute(
                "http://192.168.0.25:5000/download/title1",
                "title1.txt");

        new DownloadFilesTask().execute(
                "http://192.168.0.25:5000/download/Poster_IMG_1.png",
                "Poster_IMG_1.png");

        new DownloadFilesTask().execute(
                "http://192.168.0.25:5000/download/answer1",
                "answer1.txt");

        new DownloadFilesTask().execute(
                "http://192.168.0.25:5000/download/release_date1",
                "release_date1.txt");

        new DownloadFilesTask().execute(
                "http://192.168.0.25:5000/download/overview1",
                "overview1.txt");

        TextView nextMovie = findViewById(R.id.next_movie);
        //onclick heya el base ta3 el passage men page l page
        nextMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Movie_output.this, Movie_output2.class));

                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }



    private class DownloadFilesTask extends AsyncTask<String, Void, Void> {

        private String fileName;

        @Override
        protected Void doInBackground(String... urls) {
            String fileUrl = urls[0];
            fileName = urls[1];

            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    FileOutputStream outputStream = openFileOutput(fileName, MODE_PRIVATE);

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();
                } else {
                    Log.e("DownloadFilesTask", "Server returned HTTP response code: " + responseCode);
                }

                urlConnection.disconnect();

            } catch (Exception e) {
                Log.e("DownloadFilesTask", "Error downloading file: " + e.getMessage());
            }

            return null;
        }


            @Override
            protected void onPostExecute(Void result) {
                if (fileName.equals("title1.txt")) {
                    String titleContent = readFromFile("title1.txt");
                    titleTextView.setText(titleContent);
                } else if (fileName.equals("answer1.txt")) {
                    String answerContent = readFromFile("answer1.txt");
                    answerTextView.setText(answerContent);
                }
//                else if (fileName.equals("overview1.txt")) {
//                    String overviewContent = readFromFile("overview1.txt");
//                    // Set the content to the corresponding TextView
//                    overviewTextView.setText(overviewContent);
//                } else if (fileName.equals("poster1.txt")) {
//                    String posterContent = readFromFile("poster1.txt");
//                    // Set the content to the corresponding ImageView
//                    Bitmap posterBitmap = BitmapFactory.decodeFile(posterContent);
//                    posterImageView.setImageBitmap(posterBitmap);
//                } This caused teh fatal error due to tgeh abscence of their text viewsù
                else if (fileName.equals("Poster_IMG_1.png")) {
                    File posterFile = new File(getFilesDir(), "Poster_IMG_1.png");
                    if (posterFile.exists()) {
                        Bitmap posterBitmap = BitmapFactory.decodeFile(posterFile.getAbsolutePath());
                        posterImageView.setImageBitmap(posterBitmap);
                    } else {
                        Log.e("PosterImage", "Poster image file not found.");
                    }
                }
            }
        }

    private String readFromFile(String fileName) {
        StringBuilder fileContents = new StringBuilder();
        try {
            FileInputStream fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContents.toString();
    }


}



