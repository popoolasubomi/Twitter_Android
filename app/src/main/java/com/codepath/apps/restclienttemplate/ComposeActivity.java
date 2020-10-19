package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    EditText etCompose;
    Button btnTweet;
    TextView etCharacters;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        etCharacters = findViewById(R.id.etCharactersLeft);

        client = TwitterApp.getRestClient(this);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "No function");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int tweetLength = etCompose.getText().toString().length();
                int remaining = MAX_TWEET_LENGTH - tweetLength;
                String charactersLeft = String.valueOf(remaining);
                etCharacters.setText(charactersLeft + " characters left");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "No function");
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                 if (tweetContent.isEmpty()){
                     Toast.makeText(ComposeActivity.this, "Sorry your tweet cannot be empty", Toast.LENGTH_LONG).show();
                     return;
                 }
                 if (tweetContent.length() > MAX_TWEET_LENGTH){
                     Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                     return;
                 }
                 Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();

                 client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                     @Override
                     public void onSuccess(int statusCode, Headers headers, JSON json) {
                         Log.i(TAG, "onSuccess to publish tweet");
                         try {
                             Tweet tweet = Tweet.fromJson(json.jsonObject);
                             Log.i(TAG, "Published Tweet says: " + tweet);

                             Intent intent = new Intent();
                             intent.putExtra("tweet", Parcels.wrap(tweet));
                             setResult(RESULT_OK, intent);
                             finish();

                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                     }

                     @Override
                     public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                         Log.e(TAG, "onFailure to publish tweet", throwable);
                     }
                 });
            }
        });
    }
}