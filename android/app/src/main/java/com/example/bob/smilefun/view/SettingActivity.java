package com.example.bob.smilefun.view;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.db.GameSetting;

public class SettingActivity extends AppCompatActivity {

    private EditText etLine,etColumn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        etLine = findViewById(R.id.et_num_line);
        etColumn = findViewById(R.id.et_num_column);
    }

    public void clickReset(View view) {
        etLine.setText(GameSetting.COUNT_LINE);
        etColumn.setText(GameSetting.COUNT_COLUMN);
    }

    public void clickOk(View view) {
        int lineNum=Integer.parseInt(etLine.getText().toString());
        int columnNum=Integer.parseInt(etColumn.getText().toString());
        GameSetting setting=new GameSetting();
        setting.setColumnCount(columnNum);
        setting.setLineCount(lineNum);
        ContentValues cv = setting.getCV();
        getContentResolver().update(GameSetting.uri, cv, null, null);
        setResult(RESULT_OK);
        finish();
    }
}
