package com.example.database;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // 선언부. 아래 버튼 안에 사용하는 변수들로, 버튼은 inner class에 해당하므로 클래스에 선언해야됨.
    String  nameSt, depSt;
    int ageInt = 0;
    int cnt = 0; // 조회(select) 버튼 내에서 사용되는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        EditText name = findViewById(R.id.editTextTextName);
        EditText age = findViewById(R.id.editTextAge);
        EditText dep = findViewById(R.id.editTextDepartment);

        // test값 입력 ****************************************** 테스트용
//        name.setText("test");
//        age.setText("12");
//        dep.setText("dep");

        // helper 클래스 객체 생성
        MyDbHelper myDbHelper = new MyDbHelper(getApplicationContext());

        // 파싱 버튼
        Button buttonParsing = findViewById(R.id.buttonParsing);
        buttonParsing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    nameSt = name.getText().toString();
                    ageInt = Integer.parseInt(age.getText().toString());
                    depSt = dep.getText().toString();
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "나이는 숫자만 입력 가능합니다.", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "nameSt: " + nameSt + ", ageInt: " + ageInt + ", depSt: " + depSt);
            }
        });

        // 추가 버튼
        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(TableInfo.COLUMN_NAME_NAME, nameSt);
                values.put(TableInfo.COLUMN_NAME_AGE, ageInt);
                values.put(TableInfo.COLUMN_NAME_DEP, depSt);

                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                long newRowId = db.insert(TableInfo.TABLE_NAME, null, values);
                Log.i(TAG, "new row ID: " + newRowId);
            }
        });

        // 삭제 버튼
        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "삭제 버튼은 미구현 입니다.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "삭제 버튼은 미구현 입니다.");
            }
        });


        // 조회 버튼
        Button buttonSelect = findViewById(R.id.buttonSelect);
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "sel btn clicked");

                // 과제 1) 사용자가 조회버튼을 클릭한 횟수를 shared preferences에 저장하고, 클릭할 때마다 로그에 해당 횟수 보여주기.
                // SharedPreferences로 값 저장(Write)
                SharedPreferences sharedPreferences = getSharedPreferences("ClickCount", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("count", ++cnt);// 조회 버튼을 누른 횟수 +1 를 저장
                editor.apply();
                Log.i(TAG, "SharedPreferences로 값 저장(Write) - cnt에 저장된 값: " + cnt);

                // SharedPreferences로 값 읽기(Read)
                SharedPreferences sharedPref = getSharedPreferences("ClickCount", Context.MODE_PRIVATE);
                int count = sharedPref.getInt("count", 0); // 0은 기본값
                Log.i(TAG, "SharedPreferences로 값 읽기(Read) - count에 저장된 값: " + count);

                /*
                 과제 2) 사용자가 입력한 값들을 SQLite에 저장하고, 조회 버튼 클릭 시 입력한 값들을 기반으로 조회해서,
                         로그로 결과 데이터 모두 보여주기.
                         - 예: 사용자가 이름, 나이를 입력했으면 두 개가 모두 일치하는 데이터를 조회해서 이름, 나이, 학부를 로그로 찍어주고,
                               학부만 입력했으면 학부가 일치하는 데이터를 조회해서 이름, 나이, 학부를 로그로 찍어주기
                         - 테스트할 수 있도록 파싱, 추가, 삭제 버튼도 앞의 강의자료 내용대로 코딩 완료하기
                           (rawQuery, query 모두 사용 가능)
                 */

                // 파싱하여 저장
                String nameText = name.getText().toString();
                String ageText = age.getText().toString();
                String depText = dep.getText().toString();

                // ****** query 이용
                SQLiteDatabase db1 = myDbHelper.getReadableDatabase();
                String[] projection = {
                        BaseColumns._ID,
                        // 전체 조회의 경우, 아래에서 null로 하면 됨!
                };

                // Where절에 입력하는 값(SQL문)
                String selection = "";

                if (!nameText.equals("")) {
                    selection = selection + TableInfo.COLUMN_NAME_NAME + " = ?";
                    if (!ageText.equals(""))
                        selection = selection + " AND " + TableInfo.COLUMN_NAME_AGE + " = ?";
                    if (!depText.equals(""))
                        selection = selection + " AND " + TableInfo.COLUMN_NAME_DEP + " = ?";
                } else if (!ageText.equals("")) {
                    selection = selection + TableInfo.COLUMN_NAME_AGE + " = ?";
                    if (!depText.equals(""))
                        selection = selection + " AND " + TableInfo.COLUMN_NAME_DEP + " = ?";
                } else {
                    selection = selection + TableInfo.COLUMN_NAME_DEP + " = ?";
                }

                // ?에 해당하는 값이 들어가도록
                String[] selectionArgs = createSelectionArgs(nameText, ageText, depText);

                // query문
                Cursor c1 = db1.query(
                        TableInfo.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                if (c1.moveToFirst()) {
                    do {
                        int col1 = c1.getInt(0); // id
                        String col2 = c1.getString(1); // name
                        int col3 = c1.getInt(2); // age
                        String col4 = c1.getString(3); // depart
                        Log.i(TAG, "query / READ, id: " + col1 + " , 이름: " + col2 + " , 나이: " + col3 +
                                " , 학부: " + col4);
                    } while (c1.moveToNext());
                }
                c1.close();
                db1.close();
            }
        });
    }

    // SelectionArges 배열 생성
    private String[] createSelectionArgs(String name, String age, String depart) {
        if(!name.equals("")) {
            if(!age.equals("")) {
                if(!depart.equals("")) {
                    String[] a = { name, age, depart };
                    return a;
                } else {
                    String[] a = { name, age };
                    return a;
                }
            } else {
                if(!depart.equals("")) {
                    String[] a = { name, depart };
                    return a;
                } else {
                    String[] a = { name };
                    return a;
                }
            }
        } else if(!age.equals("")) {
            if(!depart.equals("")) {
                String[] a = { age, depart };
                return a;
            } else {
                String[] a = { age };
                return a;
            }
        } else {
            String[] a = { depart };
            return a;
        }
    }
}