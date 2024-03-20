package com.example.myapplication3;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.widget.EditText;
//import android.widget.RadioGroup;
//import android.widget.CompoundButton;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
import android.util.Log ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.app.ProgressDialog;
import android.os.Handler;


//The n_th person's answer
public class N_question extends AppCompatActivity {
    List<String> likes = new ArrayList<>();

    EditText favorite, favorite_actor;

    boolean[] isSelectedArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_question);

        favorite = findViewById(R.id.the_shawshank_redemption_because_it_taught_me_to_never_give_up_hope_no_matter_how_hard_life_gets);
        favorite_actor = findViewById(R.id.tom_hanks_because_he_is_really_funny_and_can_do_the_voice_of_woody);

        int pageNumber = getIntent().getIntExtra("pageNumber", 0);

        TextView nextPersonTextView = findViewById(R.id.next_person);

        nextPersonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfPersons = getIntent().getIntExtra("numberOfPersons", 0);

                if (pageNumber < numberOfPersons) {
                    submitForm(pageNumber);
                    Intent intent = new Intent(N_question.this, N_question.class);
                    intent.putExtra("pageNumber", pageNumber + 1);
                    intent.putExtra("numberOfPersons", numberOfPersons);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    submitForm(pageNumber);
                    Toast.makeText(N_question.this, "End of questions", Toast.LENGTH_SHORT).show();
                    sendTriggerRequestToServer();
                    ProgressDialog progressDialog = ProgressDialog.show(N_question.this, "", "Loading...", true);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(new Intent(N_question.this, Movie_output.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }, 60000); // 1 minute delay (60000 milliseconds)
                }
            }
        });

        updateViews(pageNumber);
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




//    public void Next_Person(View view) {
//        Log.d("multi_watch", "Next_Person() method called");
//        String fav = favorite.getText().toString();
//        String actor = favorite_actor.getText().toString();
//
//
//        Log.d("multi_watch", "Favorite: " + fav);
//        Log.d("multi_watch", "Favorite actor: " + actor);
//        if (!fav.isEmpty() && !actor.isEmpty() ) {
////            submitForm();
//
//        } else {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//        }
//    }


    public void submitForm(int pageNumber) {
        String fav = favorite.getText().toString();

        String actor = favorite_actor.getText().toString();
        Map<String, String> answerMap = new HashMap<>();
        answerMap.put("the "+pageNumber+"th person  ", " ");
        answerMap.put("Favorite Movie", fav);

        answerMap.put("Favorite actor", actor);
        System.out.println("Contents output ");
        for (String item : likes) {
            System.out.println(item);
        }
        for (int i = 0 ; i<likes.size() ;i++){
            answerMap.put((i+1)+"/ The film should be " , likes.get(i)) ;
        }

        sendFileToFlask(answerMap,pageNumber);
    }




    private void sendFileToFlask(Map<String, String> data,int pageNumber) {
//        String fav = favorite.getText().toString();
//        String currentMood = mood.getText().toString();
//        String favoriteScene = f_o_s.getText().toString();

        StringBuilder fileContent = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            fileContent.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
//        fileContent.append("Number persons who are going to watch the movie: ").append(NumberOfWatchers_S).append(" persons\n");
//        fileContent.append("How much time in hours the watchers have to watch this movie? ").append(Time_S).append(" hours\n");

        File tempFile = null;
        String temp = "temp_file"+pageNumber ;
        try {
            tempFile = File.createTempFile(temp, ".txt", getCacheDir());
            FileWriter writer = new FileWriter(tempFile);
            writer.write(fileContent.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        String filename = "multi_answer_"+pageNumber+".txt";
        if (tempFile != null) {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", filename, RequestBody.create(MediaType.parse("text/plain"), tempFile))
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


//    Save on phone
//    private void saveDataToFile(Map<String, String> data , int pageNumber) {
//        String filename = "multi_answer_"+pageNumber+".txt";
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
//            Log.d("multi", "Data saved to " + file.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("multi", "Error saving data to file: " + e.getMessage());
//        }
//    }

    private void updateViews(int pageNumber) {
        TextView textView = findViewById(R.id._1);

        textView.setText(String.valueOf(pageNumber));

        String textResourceName = "_" + pageNumber + "_string";
        int textResourceId = getResources().getIdentifier(textResourceName, "string", getPackageName());
        if (textResourceId != 0) {
            String newText = getResources().getString(textResourceId);
            textView.setText(newText);
        } else {
            textView.setText("Resource not found");
        }







        int[] viewIds = {R.id.rectangle_1_ek1, R.id.rectangle_1_ek4, R.id.rectangle_1_ek2, R.id.rectangle_1_ek3 , R.id.rectangle_1_ek5,R.id.rectangle_1_ek6};

        isSelectedArray = new boolean[viewIds.length];
        Arrays.fill(isSelectedArray, false);

        for (int i = 0; i < viewIds.length; i++) {
            int viewId = viewIds[i];
            View currentView = findViewById(viewId);


            System.out.println("View ID : " + viewId + ", View: " + currentView);

            toggleBackgroundColor(currentView, isSelectedArray[i], viewId);

            currentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getIndex(viewIds, v.getId());
                    Log.d("Clicked View ID" ,"the id of the clicked one is "+   v.getId()) ;
                    toggleBackgroundColor(v, isSelectedArray[index], v.getId());
                    isSelectedArray[index] = !isSelectedArray[index];
                }
            });
        }
    }

//    fun : 2131231147 , serious 2131231148 , inspiring : 2131231149 , scary : 2131231150 ,
//    new:2131231151 , classic : 2131231152

    // Method to toggle the background color
    @SuppressLint("ResourceType")
    private void toggleBackgroundColor(View view, boolean isSelected, int viewId) {
        // Change the background color based on the selection state
        if (isSelected) {
            view.setBackgroundResource(R.drawable._rectangle_7_shape);
            System.out.println("Item with ID " + viewId + " is selected.");
            Log.d("Toggle Clicked View ID " ,"the id of the Toggled clicked one is "+   view.getId()) ;
//    fun : 2131231147 , serious 2131231148 , inspiring : 2131231149 , scary : 2131231150 ,
//    new:2131231151 , classic : 2131231152
            if (view.getId() == 2131231147) {
                likes.add("fun") ;
            } else if (view.getId() == 2131231148) {
                likes.add("serious") ;
            } else if (view.getId() == 2131231149) {
                likes.add("inspiring") ;
            } else if (view.getId() == 2131231150) {
                likes.add("scary") ;
            } else if (view.getId() == 2131231151) {
                likes.add("new") ;
            } else if (view.getId() == 2131231152) {
                likes.add("classic") ;
            } else {
                Log.d("ID not found" , "ID not found") ;
            }
            System.out.println("Contents of the list:");
            for (String item : likes) {
                System.out.println(item);
            }

        } else {
            view.setBackgroundResource(R.drawable.rectangle_1_ek1_shape);
            System.out.println("Item with ID " + viewId + " is unselected.") ;

            if (view.getId() == 2131231147) {
                likes.remove("fun") ;
            } else if (view.getId() == 2131231148) {
                likes.remove("serious") ;
            } else if (view.getId() == 2131231149) {
                likes.remove("inspiring") ;
            } else if (view.getId() == 2131231150) {
                likes.remove("scary") ;
            } else if (view.getId() == 2131231151) {
                likes.remove("new") ;
            } else if (view.getId() == 2131231152) {
                likes.remove("classic") ;
            } else {
                Log.d("ID not found" , "ID not found") ;
            }
            System.out.println("Contents of the list:");

            for (String item : likes) {
                System.out.println(item);
            }
        }
    }

    private int getIndex(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }

}



//        Spinner dropdownMenu = findViewById(R.id.dropdown_menu);
//
//        // Define array adapter for spinner
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.dropdown_options, android.R.layout.simple_spinner_item);
//
//        // Set dropdown layout style
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // Apply adapter to spinner
//        dropdownMenu.setAdapter(adapter);

