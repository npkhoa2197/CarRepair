package vn.apcs.cs426.a1551016_miniproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * Splash activtiy for the intro of this app
 */

public class Splash extends Activity {

    private static final int SPLASH_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MapsActivity.class);

                Splash.this.startActivity(intent);

                Splash.this.finish();
            }
        }, SPLASH_TIME);
    }
}
