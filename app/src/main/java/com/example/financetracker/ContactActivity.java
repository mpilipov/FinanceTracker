package com.example.financetracker;

import static android.view.View.TEXT_DIRECTION_FIRST_STRONG_LTR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ContactActivity extends MainActivity {
    private static final String FILE_NAME = "transactions.json";
    private HashMap<String, HashMap<String, Object>> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_contact, null, false);
        drawerLayout.addView(contentView, 0);

        transactions = loadTransactions();
        displayTransactions();

        TableView tableView = findViewById(R.id.table_data_view);
        TableView tableView2 = findViewById(R.id.table_data_view2);
        String[] headers={"kind", "Date", "Type", "Amount"};
        String[] headers2={"Date", "Balance"};
        ArrayList<String[]> data = new ArrayList<>();
        int i = 0;
        Double totalBalance = (double) 0;
        for (String date : transactions.keySet()) {
            HashMap<String, Object> details = transactions.get(date);
            String type = (String) details.get("type");
            Double amount = (Double) details.get("amount");
            Object expin = (Object) details.get("expin");
            if (expin.equals("Income"))
                totalBalance = totalBalance + amount;
            else
                totalBalance = totalBalance - amount;
            data.add(new String[]{String.valueOf(expin), date.substring(0, date.indexOf('-')), type, String.valueOf(amount)});
            i++;
        }
        Log.i("Balance", totalBalance.toString());
        SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(this, data);
        dataAdapter.setTextSize(15);

        if (totalBalance>=0)
            tableView2.setBackgroundColor(getAppColor(R.color.lime));
        else
            tableView2.setBackgroundColor(getAppColor(R.color.red));

        tableView.setTextDirection(TEXT_DIRECTION_FIRST_STRONG_LTR);
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, headers));
        tableView.setDataAdapter(dataAdapter);


        String timeStamp = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss").format(Calendar.getInstance().getTime());
        ArrayList<String[]> data2 = new ArrayList<>();
        data2.add(new String[]{timeStamp.toString(), String.valueOf(totalBalance)});
        tableView2.setHeaderAdapter(new SimpleTableHeaderAdapter(this, headers2));
        SimpleTableDataAdapter dataAdapter2 = new SimpleTableDataAdapter(this, data2);

        tableView2.setDataAdapter(dataAdapter2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;  
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        transactions = loadTransactions();
        transactions = sortByDate(transactions);
        displayTransactions();
        Log.i("TRA", transactions.toString());


        ArrayList<String[]> data = new ArrayList<>();
        int i = 0;

        for (String date : transactions.keySet()) {
            HashMap<String, Object> details = transactions.get(date);
            String type = (String) details.get("type");
            Double amount = (Double) details.get("amount");
            Object expin = (Object) details.get("expin");

            data.add(new String[]{String.valueOf(expin), date, type, String.valueOf(amount)});
            i++;
        }
    }

    // to load the dictionary from Internal storage
    @SuppressWarnings("unchecked")
    public static HashMap<String, Integer> loadDictionary(Context context, String filename) {
        FileInputStream fis = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        HashMap<String, Integer> dictionary = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Integer value = Integer.valueOf(jsonObject.getString(key));
                dictionary.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

    private HashMap<String, HashMap<String, Object>> loadTransactions() {
        HashMap<String, HashMap<String, Object>> loadedTransactions = new HashMap<>();
        Gson gson = new Gson();

        try (FileInputStream fis = openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            loadedTransactions = gson.fromJson(isr, new TypeToken<HashMap<String, HashMap<String, Object>>>() {}.getType());
        } catch (IOException e) {
            Log.e("Contact Activity", "File not found or error reading file", e);
        }

        return loadedTransactions != null ? loadedTransactions : new HashMap<>();
    }

    private void displayTransactions() {
        StringBuilder displayText = new StringBuilder();

        for (String date : transactions.keySet()) {
            HashMap<String, Object> details = transactions.get(date);
            String type = (String) details.get("type");
            Object expin = (Object) details.get("expin");
            Double amount = (Double) details.get("amount");
            displayText.append("expin: ").append(expin).append("Date: ").append(date)
                    .append(", Type: ").append(type)
                    .append(", Amount: ").append(amount)
                    .append("\n");
        }

        Log.i("DICT_TransactContact", displayText.toString());
    }
    public static LinkedHashMap<String, HashMap<String, Object>> sortByDate(HashMap<String, HashMap<String, Object>> outerMap) {
        List<Map.Entry<String, HashMap<String, Object>>> entries = new ArrayList<>(outerMap.entrySet());

        // Sort the entries based on the date in the innerMap
        entries.sort(new Comparator<Map.Entry<String, HashMap<String, Object>>>() {
            @Override
            public int compare(Map.Entry<String, HashMap<String, Object>> o1, Map.Entry<String, HashMap<String, Object>> o2) {
                String date1 = (String) o1.getValue().get("date");
                String date2 = (String) o2.getValue().get("date");
                return date1.compareTo(date2);
            }
        });

        // Collect the sorted entries into a LinkedHashMap
        LinkedHashMap<String, HashMap<String, Object>> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, HashMap<String, Object>> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
    public int getAppColor(int resourceId) {
        Context context = getApplicationContext();
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getResources().getColor(resourceId, context.getTheme());
        }
        else {
            //noinspection deprecation
            color = context.getResources().getColor(resourceId);
        }
        return color;
    }
}