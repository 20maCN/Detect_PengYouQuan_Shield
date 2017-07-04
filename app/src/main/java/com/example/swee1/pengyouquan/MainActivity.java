package com.example.swee1.pengyouquan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button bnt1 = null;
    private Button bnt2 = null;
    private SharedPreferences sp;
    ListView listView;
    SimpleAdapter simpleAdapter;
    List<Map<String,Object>> data;
    myDB helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new myDB(this);
        listView = (ListView) findViewById(R.id.shieldlist);
        data = new ArrayList<>();
        renewData();
        simpleAdapter = new SimpleAdapter(this,data,R.layout.item, new String[] {"friendID","description"},
                new int[]{R.id.nameID,R.id.description});
        listView.setAdapter(simpleAdapter);
        bnt1 = (Button) findViewById(R.id.jumpToSetting);
        bnt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);//跳转到辅助功能设置页
                startActivity(intent);
            }
        });
        bnt2 = (Button) findViewById(R.id.Notice);
        bnt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setTitle("使用说明")//设置对话框标题
                        .setMessage("1 请赋予本软件辅助选项权限，之后打开微信即可\n" +
                                "2 请确保扫描过程中亮屏并不退出微信界面\n" +
                                "3 请在扫描结束后关闭辅助功能选项\n" +
                                "4 重新开始扫描时，原先存储数据将被删除\n" +
                                "5 该软件仅供娱乐，将来会推出改进版本，可关注作者github")//设置显示的内容
                        .show();//在按键响应事件中显示此对话框
            }
        });
    }

    private void renewData() {
        data.clear();
        Cursor c = helper.queryAllData();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext() ) {
            Map<String,Object> tmp = new LinkedHashMap<>();
            int friendIDCol = c.getColumnIndex("friendID");
            int descriptionCol = c.getColumnIndex("description");
            String friendID = c.getString(friendIDCol);
            String description = c.getString(descriptionCol);
            tmp.put("friendID",friendID);
            tmp.put("description",description);
            data.add(tmp);
        }
    }
}
