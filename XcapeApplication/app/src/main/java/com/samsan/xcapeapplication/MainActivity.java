package com.samsan.xcapeapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samsan.xcapeapplication.util.XcapeConstant;
import com.samsan.xcapeapplication.vo.HintVO;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar mainToolbar;

    EditText inputHintKey;

    TextView message1;
    TextView message2;
    TextView toolbarTitle;
    TextView hintCountText;
    LinearLayout linearLayout;

    String strMessage2 = "";
    String recentlyHintKey = "";
    int hintCount = 0;

    boolean isHintKey = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(XcapeConstant.DATA, MODE_PRIVATE);
        String hintListInString = preferences.getString(XcapeConstant.HINT_LIST, "");
        String themeName = preferences.getString(XcapeConstant.THEME_NAME, "");

        // 진동
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        hintCountText = findViewById(R.id.hintCount);
        hintCountText.setText(String.valueOf(hintCount));
        hintCountText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibrate(vibrator);
                inputPasswordAlert(MainActivity.this);
                return true;
            }
        });

        linearLayout = findViewById(R.id.hintCountLayout);
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibrate(vibrator);
                inputPasswordAlert(MainActivity.this);
                return true;
            }
        });

        mainToolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        // toolbar tilte 제거
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // toolbar 왼쪽 돌아가기 버튼 생성
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // toolbar 뒤로가기 이미지 변경
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_settings_white_20);

        toolbarTitle = findViewById(R.id.mainToolbarTitle);
        toolbarTitle.setText(themeName);

        ArrayList<HintVO> hintVOList;
        inputHintKey = findViewById(R.id.searchWithKey);
        message1 = findViewById(R.id.message1);
        message2 = findViewById(R.id.message2);
        String msg = "🔒 터치하면 정답이 보입니다.";

        message2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!strMessage2.isEmpty() && !strMessage2.equals(message2.getText().toString())) {
                    vibrate(vibrator);
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setTitle("정답");
                    alertBuilder.setMessage("지금 확인하시겠습니까?");
                    alertBuilder.setPositiveButton("확인하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!strMessage2.isEmpty()) {
                                message2.setText(strMessage2);
                            }
                        }
                    });
                    alertBuilder.setNegativeButton("좀 더 고민해본다.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 아무것도 안함
                        }
                    });
                    alertBuilder.show();
                }
            }
        });
        ImageButton searchButton = findViewById(R.id.searchWithKeyButton);

        if (!hintListInString.isEmpty()) {
            Gson gson = new Gson();
            hintVOList = gson.fromJson(hintListInString, new TypeToken<ArrayList<HintVO>>() {}.getType());

            inputHintKey.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        vibrate(vibrator);
                        searchHint(hintVOList);

                        return true;
                    } else {
                        return false;
                    }
                }
            });

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrate(vibrator);
                    searchHint(hintVOList);
                }
            });

        } else {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vibrate(vibrator);
                    Toast.makeText(MainActivity.this, "잘못된 입력입니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void searchHint(List<HintVO> hintVOList) {
        String targetKey = inputHintKey.getText().toString();
        if (!targetKey.equals(recentlyHintKey)) {
            for (HintVO hintVO : hintVOList) {
                if (targetKey.equals(hintVO.getKey())) {
                    message1.setText(hintVO.getMessage1());
                    strMessage2 = hintVO.getMessage2();
                    hintCount++;
                    hintCountText.setText(String.valueOf(hintCount));
                    recentlyHintKey = targetKey;
                    isHintKey = true;
                    break;
                }
            }
            if (!isHintKey) {
                Toast.makeText(MainActivity.this, "힌트 코드를 확인해주세요. 🙅‍♂️", Toast.LENGTH_SHORT).show();
            } else {
                // 버튼 클릭시 키보드 내려가게
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(message2.getWindowToken(), 0);
                isHintKey = false;
            }
        }
    }

    public void settingsOnClick(Context context) {
        EditText editText = new EditText(context);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("관리자 비밀번호를 입력해주세요.");
        alertDialog.setView(editText);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!editText.getText().toString().equals("5772")) {
                    Toast.makeText(context, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    public void inputPasswordAlert(Context context) {
        EditText editText = new EditText(context);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("관리자 비밀번호를 입력해주세요.");
        alertDialog.setView(editText);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().equals("5772")) {
                    hintCount = 0;
                    hintCountText.setText(String.valueOf(hintCount));
                    Toast.makeText(context, "힌트가 초기화 되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public void vibrate(Vibrator vibrator) {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, 50));
        } else {
            vibrator.vibrate(100);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, 50));
                } else {
                    vibrator.vibrate(100);
                }
                settingsOnClick(MainActivity.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}