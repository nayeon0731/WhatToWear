package com.example.whattowear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingActivity extends AppCompatActivity {
    RadioButton wButton, mButton, coldButton, normalButton, hotButton;
    Button submitButton;
    int rUCold = 0;
    int sex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Log.d("check", "설정 화면");

        final RadioGroup radioGroup1 = (RadioGroup)findViewById(R.id.radioGroup1);
        final RadioGroup radioGroup2 = (RadioGroup)findViewById(R.id.radioGroup2);

        wButton = findViewById(R.id.wButton);
        mButton = findViewById(R.id.mButton);
        coldButton = findViewById(R.id.coldButton);
        normalButton = findViewById(R.id.normalButton);
        hotButton = findViewById(R.id.hotButton);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //성별 체크
                int id1 = radioGroup1.getCheckedRadioButtonId();
                RadioButton radioButton1 = (RadioButton)findViewById(id1);

                switch (id1){
                    case R.id.wButton:
                        sex = 1;
                        break;
                    case R.id.mButton:
                        sex = 0;
                        break;
                }

                //선호도 체크
                int id2 = radioGroup2.getCheckedRadioButtonId();
                RadioButton radioButton2 = (RadioButton)findViewById(id2);

                //어느 버튼 눌렸는지 체크
                switch (id2){
                    case R.id.coldButton:
                        rUCold = 1;
                        break;
                    case R.id.normalButton:
                        rUCold = 0;
                        break;
                    case R.id.hotButton:
                        rUCold = -1;
                        break;
                }

                Intent sendIntent = new Intent(SettingActivity.this, MainActivity.class);
                sendIntent.putExtra("성별", sex);
                sendIntent.putExtra("선호도", rUCold);

                startActivity(sendIntent);
            }
        });
    }
}