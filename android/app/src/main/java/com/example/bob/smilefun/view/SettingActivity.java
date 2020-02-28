package com.example.bob.smilefun.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.db.GameSetting;
import com.example.bob.smilefun.utils.SPUtil;

public class SettingActivity extends AppCompatActivity {

    private EditText etLine,etColumn;
    private SPUtil spUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        etLine = findViewById(R.id.et_num_line);
        etColumn = findViewById(R.id.et_num_column);
        spUtil = SPUtil.build(getApplicationContext());
        etLine.setText(String.valueOf(spUtil.get(GameSetting.NUM_LINE, GameSetting.COUNT_LINE)));
        etColumn.setText(String.valueOf(spUtil.get(GameSetting.NUM_COLUMN, GameSetting.COUNT_COLUMN)));
    }

    public void clickReset(View view) {
        etLine.setText(String.valueOf(GameSetting.COUNT_LINE));
        etColumn.setText(String.valueOf(GameSetting.COUNT_COLUMN));
    }

    public void clickOk(View view) {
        int lineNum=Integer.parseInt(etLine.getText().toString());
        int columnNum=Integer.parseInt(etColumn.getText().toString());
        spUtil.put(GameSetting.NUM_LINE, lineNum);
        spUtil.put(GameSetting.NUM_COLUMN, columnNum);
        setResult(RESULT_OK);
        finish();
    }
}
