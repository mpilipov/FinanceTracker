package com.example.financetracker;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class LoginActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_login, null, false);
        drawerLayout.addView(contentView, 0);
        final Button btn1 = (Button) contentView.findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // total_expenses.txt
                HashMap<String, Integer> dictionary = new HashMap<>();
                dictionary.put("Health", 0);
                dictionary.put("Leisure", 0);
                dictionary.put("Home", 0);
                dictionary.put("Groceries", 0);
                dictionary.put("Gifts", 0);
                dictionary.put("Cafe", 0);
                dictionary.put("Transport", 0);
                dictionary.put("Cosmetics", 0);
                dictionary.put("Clothes, shoes", 0);
                dictionary.put("Total", 0);
                saveDictionary(v.getContext(), dictionary, "total_expenses.txt");


                dictionary = new HashMap<>();
                dictionary.put("Paycheck", 0);
                dictionary.put("Gift", 0);
                dictionary.put("Dividend", 0);
                dictionary.put("Cashback", 0);
                dictionary.put("Bonus", 0);
                dictionary.put("Total", 0);
                saveDictionary(v.getContext(), dictionary, "total_incomes.txt");
                // expenses.txt
                clearStorage("transactions.json");

                Toast toast = Toast.makeText(v.getContext(), "All the recordings were removed", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        final Button btn3 = (Button) contentView.findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent myIntent = new Intent(LoginActivity.this, FirstActivity.class);
                    LoginActivity.this.startActivity(myIntent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void saveDictionary(Context context, HashMap<String, Integer> dictionary, String filename){
        JSONObject jsonObject = new JSONObject(dictionary);
        String jsonString = jsonObject.toString();
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if (fos != null){
                try{
                    fos.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearStorage(String filename) {
        try (FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE)) {
            fos.write("".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}