package com.samsan.xcapeapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samsan.xcapeapplication.util.XcapeConstant;
import com.samsan.xcapeapplication.vo.HintVO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText;

    TextView textView1;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
        String hintListInString = preferences.getString(XcapeConstant.HINT_LIST, "");
        String themeName = preferences.getString(XcapeConstant.THEME_NAME, "");
        setTitle(themeName);

        ArrayList<HintVO> hintVOList;

        editText = findViewById(R.id.searchWithKey);
        textView1 = findViewById(R.id.message1);
        textView2 = findViewById(R.id.message2);
        Button searchButton = findViewById(R.id.searchWithKeyButton);

        if (!hintListInString.isEmpty()) {
            Gson gson = new Gson();
            hintVOList = gson.fromJson(hintListInString, new TypeToken<ArrayList<HintVO>>() {
            }.getType());
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String targetKey = editText.getText().toString();

                    for (HintVO hintVO : hintVOList) {
                        if (targetKey.equals(hintVO.getKey())) {
                            textView1.setText(hintVO.getMessage1());
                            textView2.setText(hintVO.getMessage2());
                        }
                    }

                }
            });

        } else {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hintSettingButton:
                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}