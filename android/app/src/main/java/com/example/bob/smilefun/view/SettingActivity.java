package com.example.bob.smilefun.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.db.PreferenceSetting;
import com.example.bob.smilefun.utils.SPUtil;

public class SettingActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private static final String TAG = "SettingActivityTAG";
    private EditText etLine,etColumn;
    private RadioGroup rgDifficulty;
    private int preDifficulty;
    private SPUtil spUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        etLine = findViewById(R.id.et_num_line);
        etColumn = findViewById(R.id.et_num_column);
        rgDifficulty=findViewById(R.id.rg_difficulty);
        spUtil = SPUtil.build(getApplicationContext());
        etLine.setText(String.valueOf(spUtil.get(PreferenceSetting.NUM_LINE, PreferenceSetting.COUNT_LINE)));
        etColumn.setText(String.valueOf(spUtil.get(PreferenceSetting.NUM_COLUMN, PreferenceSetting.COUNT_COLUMN)));
        int i = spUtil.get(PreferenceSetting.GAME_DIFFICULTY, PreferenceSetting.MAX_DIFFICULTY%PreferenceSetting.MAX_DIFFICULTY);
        rgDifficulty.check(rgDifficulty.getChildAt(i%PreferenceSetting.MAX_DIFFICULTY).getId());
        rgDifficulty.setOnCheckedChangeListener(this);
    }

    public void clickReset(View view) {
        etLine.setText(String.valueOf(PreferenceSetting.COUNT_LINE));
        etColumn.setText(String.valueOf(PreferenceSetting.COUNT_COLUMN));
        rgDifficulty.check(rgDifficulty.getChildAt(PreferenceSetting.MAX_DIFFICULTY%PreferenceSetting.MAX_DIFFICULTY).getId());
    }

    public void clickOk(View view) {
        int lineNum=Integer.parseInt(etLine.getText().toString());
        int columnNum=Integer.parseInt(etColumn.getText().toString());
        spUtil.put(PreferenceSetting.NUM_LINE, lineNum);
        spUtil.put(PreferenceSetting.NUM_COLUMN, columnNum);
        spUtil.put(PreferenceSetting.GAME_DIFFICULTY, preDifficulty%PreferenceSetting.MAX_DIFFICULTY);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        preDifficulty=checkedId-1;
    }
}
