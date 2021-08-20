package com.samsan.xcapeapplication;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.google.gson.Gson;
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

    TextView textView;
    Spinner merchantSpinner;
    Spinner themeSpinner;

    ArrayAdapter<String> merchantAdapter;
    ArrayAdapter<String> themeAdapter;

    ArrayList<String> merchantCodeList = new ArrayList<>();
    ArrayList<String> merchantNameList = new ArrayList<>();
    ArrayList<String> themeCodeList = new ArrayList<>();
    ArrayList<String> themeNameList = new ArrayList<>();
    ArrayList<HintVO> hintList = new ArrayList<>();

//    ArrayList<MerchantVO> merchantVOS = new ArrayList<>();

    String merchantCode;
    String themeCode;
    String themeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        textView = findViewById(R.id.textView);
        merchantSpinner = findViewById(R.id.merchantSpinner);
        themeSpinner = findViewById(R.id.themeSpinner);

//        SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
//        preferences.getString(XcapeConstant.MERCHANT_CODE_LIST, "");
//        preferences.getString(XcapeConstant.THEME_CODE_LIST, "");
//        preferences.getString(XcapeConstant.MERCHANT_NAME_LIST, "");
//        preferences.getString(XcapeConstant.THEME_NAME_LIST, "");
        /**
         *  getMerchantList
         */
        Call<List<MerchantVO>> callMerchant = getApiService().getMerchantList();
        callMerchant.enqueue(new Callback<List<MerchantVO>>() {
            @Override
            public void onResponse(Call<List<MerchantVO>> call, Response<List<MerchantVO>> response) {
                Log.d(TAG, "onResponse: " + response.body());
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>... response 받는 시점");
                for(MerchantVO merchantVO : response.body()) {
//                    merchantVOS.add(merchantVO);
                    merchantCodeList.add(merchantVO.getMerchant().getMerchantCode());
                    merchantNameList.add(merchantVO.getMerchant().getMerchantName());
                }
                responseToMerchantSpinner();
            }

            @Override
            public void onFailure(Call<List<MerchantVO>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        /**
         *  getThemeList
         */
        merchantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                merchantCode = merchantCodeList.get(position);
                Toast.makeText(getApplicationContext(), merchantCode, Toast.LENGTH_SHORT).show();
                Call<List<ThemeVO>> callTheme = getApiService().getThemeList(merchantCode);
                callTheme.enqueue(new Callback<List<ThemeVO>>() {
                    @Override
                    public void onResponse(Call<List<ThemeVO>> call, Response<List<ThemeVO>> response) {
                        themeNameList.clear();
                        themeCodeList.clear();
                        for(ThemeVO themeVO : response.body()) {
                            themeCodeList.add(themeVO.getThemeCode());
                            themeNameList.add(themeVO.getThemeName());
                        }
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
                        hintList.clear();
                        textView.setText("");
                        for(HintVO hintVO : response.body()) {
                            hintList.add(hintVO);
                        }
                        Gson gson = new Gson();
                        SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(XcapeConstant.HINT_LIST, gson.toJson(response.body()));
                        editor.putString(XcapeConstant.THEME_NAME, themeName);
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
        themeAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, themeNameList);
        themeSpinner.setAdapter(themeAdapter);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                themeCode = themeCodeList.get(position);
                themeName = themeNameList.get(position);
                Toast.makeText(getApplicationContext(), themeCode, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void responseToMerchantSpinner() {
        merchantAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, merchantNameList);
        merchantSpinner.setAdapter(merchantAdapter);
    }
}
