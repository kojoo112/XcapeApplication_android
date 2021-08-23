package com.samsan.xcapeapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samsan.xcapeapplication.util.XcapeConstant;
import com.samsan.xcapeapplication.vo.HintVO;
import com.samsan.xcapeapplication.vo.MerchantVO;
import com.samsan.xcapeapplication.vo.ThemeVO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.samsan.xcapeapplication.retrofit.RetrofitClient.getApiService;

public class SubActivity extends AppCompatActivity {

    Spinner merchantSpinner;
    Spinner themeSpinner;
    Toolbar subToolbar;

    ArrayAdapter<String> merchantAdapter;
    ArrayAdapter<String> themeAdapter;

    ArrayList<String> merchantCodeList;
    ArrayList<String> merchantNameList;
    ArrayList<String> themeCodeList;
    ArrayList<String> themeNameList;

    int selectedMerchantPosition;
    int selectedThemePosition;

    String merchantCode;
    String themeCode;
    String themeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        merchantSpinner = findViewById(R.id.merchantSpinner);
        themeSpinner = findViewById(R.id.themeSpinner);

        subToolbar = findViewById(R.id.subToolbar);
        setSupportActionBar(subToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Gson gson = new Gson();
        SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
        merchantCodeList = gson.fromJson(preferences.getString(XcapeConstant.MERCHANT_CODE_LIST, String.valueOf(new ArrayList<>())), new TypeToken<ArrayList<String>>(){}.getType());
        merchantNameList = gson.fromJson(preferences.getString(XcapeConstant.MERCHANT_NAME_LIST, String.valueOf(new ArrayList<>())), new TypeToken<ArrayList<String>>(){}.getType());
        themeCodeList = gson.fromJson(preferences.getString(XcapeConstant.THEME_CODE_LIST, String.valueOf(new ArrayList<>())), new TypeToken<ArrayList<String>>(){}.getType());
        themeNameList = gson.fromJson(preferences.getString(XcapeConstant.THEME_NAME_LIST, String.valueOf(new ArrayList<>())), new TypeToken<ArrayList<String>>(){}.getType());
        selectedMerchantPosition = preferences.getInt(XcapeConstant.SELECTED_MERCHANT_POSITION, 0);
        selectedThemePosition = preferences.getInt(XcapeConstant.SELECTED_THEME_POSITION, 0);



        /**
         *  getMerchantList
         */
        if(merchantCodeList.isEmpty()) {
            Call<List<MerchantVO>> callMerchant = getApiService().getMerchantList();
            callMerchant.enqueue(new Callback<List<MerchantVO>>() {
                @Override
                public void onResponse(Call<List<MerchantVO>> call, Response<List<MerchantVO>> response) {
                    for (MerchantVO merchantVO : response.body()) {
                        merchantCodeList.add(merchantVO.getMerchant().getMerchantCode());
                        merchantNameList.add(merchantVO.getMerchant().getMerchantName());
                    }

                    SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Gson gson = new Gson();
                    editor.putString(XcapeConstant.MERCHANT_CODE_LIST, gson.toJson(merchantCodeList));
                    editor.putString(XcapeConstant.MERCHANT_NAME_LIST, gson.toJson(merchantNameList));
                    editor.commit();

                    responseToMerchantSpinner();
                }

                @Override
                public void onFailure(Call<List<MerchantVO>> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });

        } else {
            responseToMerchantSpinner();
            responseToThemeSpinner();
        }
            /**
             *  getThemeList
             */
            merchantSpinner.setSelection(selectedMerchantPosition);
            merchantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    merchantCode = merchantCodeList.get(position);
                    Call<List<ThemeVO>> callTheme = getApiService().getThemeList(merchantCode);
                    callTheme.enqueue(new Callback<List<ThemeVO>>() {
                        @Override
                        public void onResponse(Call<List<ThemeVO>> call, Response<List<ThemeVO>> response) {
                            themeNameList.clear();
                            themeCodeList.clear();
                            for (ThemeVO themeVO : response.body()) {
                                themeCodeList.add(themeVO.getThemeCode());
                                themeNameList.add(themeVO.getThemeName());
                            }

                            SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            Gson gson = new Gson();
                            editor.putInt(XcapeConstant.SELECTED_MERCHANT_POSITION, position);
                            // merchant가 바뀌었을땐 themePostion을 0으로 초기화
                            selectedThemePosition = 0;
                            editor.putString(XcapeConstant.THEME_CODE_LIST, gson.toJson(themeCodeList));
                            editor.putString(XcapeConstant.THEME_NAME_LIST, gson.toJson(themeNameList));
                            editor.commit();

                            responseToThemeSpinner();
                        }

                        @Override
                        public void onFailure(Call<List<ThemeVO>> call, Throwable t) {
                            Log.d(TAG, ">>>>>>>>>>>>>>>>>>>> getThemeList FAIL!!!!! " + t.getMessage());
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        /**
         *  button clickEvent
         */
        Button getHintButton = findViewById(R.id.getHintListButton);
        getHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<HintVO>> callHintList = getApiService().getHintList(merchantCode, themeCode);
                callHintList.enqueue(new Callback<List<HintVO>>() {
                    @Override
                    public void onResponse(Call<List<HintVO>> call, Response<List<HintVO>> response) {
                        Gson gson = new Gson();
                        SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(XcapeConstant.HINT_LIST, gson.toJson(response.body()));
                        editor.putString(XcapeConstant.THEME_NAME, themeName);
                        editor.putInt(XcapeConstant.HINT_COUNT, 0);
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        // 현재 Activity 종료
                        finish();
                    }

                    @Override
                    public void onFailure(Call<List<HintVO>> call, Throwable t) {
                        Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>> 실패 " + t.getMessage());
                    }
                });
            }
        });

    }   // onCreate 끝 지점

    private void responseToThemeSpinner() {
        themeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, themeNameList);
        themeAdapter.setDropDownViewResource(R.layout.spinner_on_click_item);
        themeSpinner.setAdapter(themeAdapter);
        themeSpinner.setSelection(selectedThemePosition);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                themeCode = themeCodeList.get(position);
                themeName = themeNameList.get(position);
                SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(XcapeConstant.SELECTED_THEME_POSITION, position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void responseToMerchantSpinner() {
        merchantAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, merchantNameList);
        merchantAdapter.setDropDownViewResource(R.layout.spinner_on_click_item);
        merchantSpinner.setAdapter(merchantAdapter);
    }
}
