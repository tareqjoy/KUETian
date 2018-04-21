package com.tareq.kuetian;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class EditInfoView extends AppCompatActivity {

    private Spinner spinner;
    private EditText text;
    private int from, inputType = 0;
    private String operation, prevData;
    private RadioGroup optionsGroup;
    private DatePicker date;
    private RadioButton option1, option2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info_view);

        text = findViewById(R.id.the_text);
        spinner = findViewById(R.id.the_spin);
        optionsGroup = findViewById(R.id.options_group);
        option1 = findViewById(R.id.option_1);
        option2 = findViewById(R.id.option_2);
        date = findViewById(R.id.date);

        text.setSingleLine(true);
        text.setSelection(text.getText().length());
        Bundle extras = getIntent().getExtras();
        String str = extras.getString("singleLine");
        if (str != null)
            if (str.equals("false"))
                text.setSingleLine(false);

        inputType = extras.getInt("inputType");
        text.setInputType(inputType);

        str = extras.getString("digits");
        if (str != null) {
            text.setKeyListener(DigitsKeyListener.getInstance(str));
        }


        from = extras.getInt("from");
        prevData = extras.getString("prevData");
        if (prevData != null) {
            text.setText(prevData);
            text.setSelection(text.getText().length());
        }
        operation = extras.getString("operation");
        if (operation == null)
            operation = "text";
        else if (operation.equals("radio")) {
            text.setVisibility(View.GONE);
            optionsGroup.setVisibility(View.VISIBLE);
            if (option1.getText().toString().equals(prevData)) {
                option1.setChecked(true);
            } else {
                option2.setChecked(true);
            }
        } else if (operation.equals("spin")) {
            text.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            Adapter adapter = spinner.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                String indiv = adapter.getItem(i).toString();
                if (indiv.equals(prevData)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        } else if (operation.equals("date")) {
            text.setVisibility(View.GONE);
            date.setVisibility(View.VISIBLE);
            int d = -1, m = -1, y = -1;
            String[] prevDateStr;
            if (prevData != null) {
                try {
                    prevDateStr = prevData.split("/");
                    d = Integer.parseInt(prevDateStr[0]);
                    m = Integer.parseInt(prevDateStr[1]);
                    m--;
                    y = Integer.parseInt(prevDateStr[2]);
                    date.updateDate(y, m, d);
                } catch (Exception e) {

                }
            }
        }

        if (operation.equals("text")) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        str = extras.getString("name");
        if (str != null)
            getSupportActionBar().setTitle(str);
        else
            getSupportActionBar().setTitle("Input");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void returnValue(int ResultCode, String newdata) {
        Bundle conData = new Bundle();
        conData.putString("data", newdata);
        conData.putString("prevData", prevData);
        conData.putInt("from", from);
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(ResultCode, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            returnValue(RESULT_CANCELED, "");
        } else if (item.getItemId() == R.id.action_save_editText) {
            if (operation.equals("text"))
                if (from == MainActivity.BATCH_NO) {
                    String temp = text.getText().toString();
                    boolean k = false;
                    for (int i = 0; i < temp.length(); i++) {
                        if (temp.charAt(i) == 'k') {
                            k = true;
                            break;
                        }
                    }
                    if (!k) {
                        text.setError("Wrong Format. Ex: 2k15");
                    } else {
                        returnValue(RESULT_OK, text.getText().toString());
                    }
                } else

                    returnValue(RESULT_OK, text.getText().toString());

            else if (operation.equals("spin"))
                if (spinner.getSelectedItemId() == 0) {
                    Toast.makeText(getApplicationContext(), "Department must be selected", Toast.LENGTH_SHORT).show();
                } else
                    returnValue(RESULT_OK, spinner.getSelectedItem().toString());
            else if (operation.equals("radio")) {
                String str;
                if (optionsGroup.getCheckedRadioButtonId() == option1.getId()) {
                    str = option1.getText().toString();
                } else {
                    str = option2.getText().toString();
                }
                returnValue(RESULT_OK, str);
            } else if (operation.equals("date")) {
                int d, y, m;
                y = date.getYear();
                d = date.getDayOfMonth();
                m = date.getMonth();
                m++;
                String dateStr = String.valueOf(d) + "/" + String.valueOf(m) + "/" + String.valueOf(y);
                returnValue(RESULT_OK, dateStr);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_text_menu, menu);
        return true;
    }


}
