package com.example.arturmusayelyan.notificationsheduler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.rg_letters)
    RadioGroup rgLetters;
    Button defaultButton;
    boolean buttonDefaultCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        defaultButton=findViewById(R.id.defaultButton);
        buttonDefaultCheck=false;


        for (int i = 0; i < rgLetters.getChildCount(); i++) {
            ((RadioButton)rgLetters.getChildAt(i)).setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }


    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.isPressed()){
                buttonDefaultCheck=true;
                if (isChecked){
                    Toast.makeText(MainActivity.this, buttonView.getText(), Toast.LENGTH_SHORT).show();
                    if(!buttonDefaultCheck){
                        ((RadioButton)defaultButton).setChecked(false);
                    }
                }
            }
        }
    };

}
