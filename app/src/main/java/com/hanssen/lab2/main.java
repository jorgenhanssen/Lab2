package com.hanssen.lab2;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.XML;

import android.util.Log;
import android.widget.ListView;
import java.util.ArrayList;


public class main extends AppCompatActivity {

    JSONArray itemList  = null;

    int     itemLimit = 0;
    String  xml       = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isMyServiceRunning()){
            startService(new Intent(this, update.class));
        }

        getUserPreferences();

        if (!xml.isEmpty()) {
            itemList = getItemList(xmlToJSON(xml));
            updateList();
        }

        addListenerOnPreferencesButton();

        addListenerOnItemList();
    }


    private void addListenerOnPreferencesButton() {
        Button button = findViewById(R.id.gotoPreferences);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(main.this, preferences.class));
            }
        });
    }


    private void getUserPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("preferences", MODE_PRIVATE);

        xml       = sharedPref.getString("xml", "");
        itemLimit = sharedPref.getInt("items", -1);
    }


    private JSONObject xmlToJSON(String xml) {
        JSONObject object = null;

        try {
            object = XML.toJSONObject(xml);
        } catch (JSONException e) {
            Log.d("Error", e.getMessage());
        }

        return object;
    }


    private JSONArray getItemList(JSONObject obj) {
        JSONArray list = null;

        try {
            list = obj.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
        } catch (JSONException e) {
            Log.d("Error", e.getMessage());
        }

        return list;
    }


    private void updateList() {
        ListView list = findViewById(R.id.list);

        ArrayList<String> stringArr = new ArrayList<>();

        try {
            for (int i = 0; i < itemLimit; i++) {
                String title = itemList.getJSONObject(i).getString("title");
                stringArr.add(title);
            }
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(main.this, android.R.layout.simple_list_item_1, stringArr);
        list.setAdapter(adapter);
    }

    private void addListenerOnItemList() {
        ListView list = findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(main.this, content.class);
                try {
                    intent.putExtra("title", itemList.getJSONObject(position).getString("title"));
                    intent.putExtra("description", itemList.getJSONObject(position).getString("description"));
                    intent.putExtra("link", itemList.getJSONObject(position).getString("link"));
                } catch (JSONException e) {
                    Log.e("JSON exception", e.getMessage());
                }

                startActivity(intent);
            }
        });
    }












    public void alert(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton( "Ok",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }



    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(this.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (update.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
