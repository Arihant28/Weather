package com.example.asinghi.weatherproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.annotation.Nullable;



/**
 * Created by asinghi on 11/7/17.
 */

public class CurrentWeather extends Fragment implements GetRawData.AsyncResponse {

    static CurrentWeather currentWeather = null;

    TextToSpeech t1;

    public CurrentWeather() {
    }

    public static CurrentWeather getInstance() {
        if (currentWeather != null)
            return currentWeather;
        return new CurrentWeather();
    }

    public static CurrentWeather newInstance() {
        return new CurrentWeather();
    }

    View rootView;

    Database dbHandler;
    ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    EditText cityText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.currentweather, container, false);
        Button fetchButton = (Button) rootView.findViewById(R.id.fetchButton);

        btnSpeak = (ImageButton) rootView.findViewById(R.id.btn_speak);

        // hide the action bar
        // getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                cityText = (EditText) getView().findViewById(R.id.cityText);
                String city = cityText.getText().toString();
                if(city.equals(null) || city.equals(""))
                {
                    Toast.makeText(getContext() , "Enter the city " , Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Toast.makeText(getContext(), "helloooooo", Toast.LENGTH_LONG).show();
                    String api = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=25039d7a8d6f6bb0c0f06edea90d50b1";
                    new GetRawData(CurrentWeather.this).execute(api);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.drawable.notication)
                                    .setContentTitle("My notification")
                                    .setContentText("Hello World!");
                    Intent resultIntent = new Intent(getContext(), MainActivity.class);
                    resultIntent.putExtra("M", 123);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, mBuilder.build());

                }
            }
        });


        return rootView;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(), getString(R.string.speech_not_supported), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    cityText.setText(result.get(0));
                }
                break;
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void processFinish(String output) {

        TextView resultText = (TextView) rootView.findViewById(R.id.resultText);
        Output out = new Output(output);
        dbHandler = new Database(getContext(), null, null, 1);

        dbHandler.add(out);


        try {
            JSONObject data = new JSONObject(output);
            double temp = data.getJSONObject("main").getDouble("temp") - 274.15;
            DecimalFormat two = new DecimalFormat("#0.00");
            String temperature = two.format(temp);
            String place = data.getString("name");
            resultText.setText("Temperature of " + place + " is: " + temperature + "°C");
            resultText.setMovementMethod(new ScrollingMovementMethod());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String toSpeak = resultText.getText().toString();
        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
                if (status == TextToSpeech.SUCCESS) {
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);

                }
            }
        });



       /* Gson gson = new Gson();

        System.out.println(gson.fromJson(output , JSONDemo.class));

        List<String> dbString = dbHandler.databasetoString();
        resultText.setText(dbString.get(0));

       */
        cityText.setText("");


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

