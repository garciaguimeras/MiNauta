package dev.blackcat.minauta;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MyAppCompatActivity extends AppCompatActivity
{

    public <T extends View> T getViewById(int resId)
    {
        return (T) this.findViewById(resId);
    }

}