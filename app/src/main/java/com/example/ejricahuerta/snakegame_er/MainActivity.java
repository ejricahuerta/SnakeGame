package com.example.ejricahuerta.snakegame_er;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends Activity {
    SnakeView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        view = new SnakeView(this, point);
        setContentView(view);
    }
    @Override
    protected void onResume() {
        super.onResume();
        view.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        view.pause();
    }

}
