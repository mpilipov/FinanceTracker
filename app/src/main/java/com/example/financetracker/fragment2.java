package com.example.financetracker;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class fragment2 extends Fragment {

    EditText et2;
    PieChart piechart;
    Spinner spinner2;

    private HashMap<String, HashMap<String, Object>> transactions;
    private static final String FILE_NAME = "transactions.json";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_fragment2, container, false);
        View rootview = inflater.inflate(R.layout.fragment_fragment2, container, false);

        final Button add2 = (Button) rootview.findViewById(R.id.add2);
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAdd2(v);
            }
        });

        // to attach edittext field
        et2 = rootview.findViewById(R.id.et2);

        //spinner
        spinner2 = rootview.findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootview.getContext(),
                R.array.incomes_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner2.setAdapter(adapter);
        spinner2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(getActivity());
                return false; // чтобы спиннер работал после нажатия
            }
        });

        // piechart
        piechart = rootview.findViewById(R.id.chart2);

        Map<String, Integer> dict = loadDictionary(rootview.getContext(), "total_incomes.txt");
        UpdatePieChart(rootview, piechart, dict);

        return rootview;
    }


    public void onClickAdd2(View view) {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss").format(Calendar.getInstance().getTime());


        //saveDictionary(getContext(), (HashMap<String, String>) dictionary);
        HashMap<String, Integer> dict_total = new HashMap<>();
        dict_total = loadDictionary(getContext(),"total_incomes.txt");

        Object choice = spinner2.getSelectedItem().toString();


        // to gather all the values into the dictionary dict_total
        Integer incomeValue = 0;
        if( !et2.getText().toString().equals("") && et2.getText().toString().length() > 0 ) {

            incomeValue = Integer.parseInt(et2.getText().toString());
            // income.txt
            transactions = loadTransactions();
            addTransaction("Income", String.valueOf(timeStamp),
                    String.valueOf(spinner2.getSelectedItem()), incomeValue);
            saveTransactions(transactions);
            displayTransactions();

            List<String> keys = new ArrayList<String>(dict_total.keySet());
            for (int i = 0; i < keys.size(); i++) {
                if (choice.equals(keys.get(i).toString())) {

                    int editTextValue = incomeValue;
                    int previousValue = dict_total.get(keys.get(i));
                    dict_total.put(keys.get(i), editTextValue + previousValue);

                    int total = dict_total.get("Total");
                    dict_total.put("Total", total + editTextValue);

                    break;
                }
            }
        }
        else {
            Toast toast = Toast.makeText(getContext(), "Input a correct value of your income", Toast.LENGTH_SHORT);
            toast.show();
        }

        saveDictionary(getContext(), dict_total, "total_incomes.txt");
        View rootview = getView();
        UpdatePieChart(rootview, piechart, dict_total);
        hideSoftKeyboard(getActivity());
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

    //onViewCreated
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    private void UpdatePieChart(View v, PieChart piechart, Map<String, Integer> dict){

        ArrayList<String>keys = new ArrayList<String>(dict.keySet());
        ArrayList<PieEntry> entries = new ArrayList<>();

        File path = v.getContext().getApplicationContext().getFilesDir();
        File file = new File(path, "total_incomes.txt");
        if (!file.exists())
        {
            Toast toast = Toast.makeText(getActivity(), "file doesn't exist", Toast.LENGTH_SHORT);
            toast.show();
        }

        // to adjust legend of the piechart
        Legend l = piechart.getLegend();
        l.setTextColor(Color.BLACK);
        l.setTextSize(14f);
        l.setForm(Legend.LegendForm.DEFAULT);
        l.setFormSize(10f);
        l.setXEntrySpace(10f);
        l.setFormToTextSpace(10f);

        // to set the colors of the pies in the piechart
        final int[] pieColors = {
                getAppColor(R.color.blue),
                getAppColor(R.color.yellow),
                getAppColor(R.color.fuchsia),
                getAppColor(R.color.red),
                getAppColor(R.color.gray),
                getAppColor(R.color.olive),
                getAppColor(R.color.aqua),
                getAppColor(R.color.teal),
                getAppColor(R.color.lime)
        };

        ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        if (dict != null) {
            keys = new ArrayList<String>(dict.keySet());
            int legendCounter = 0;
            for(int i = 0; i < keys.size() ; i++) {
                if (dict.get(keys.get(i))>0 && !keys.get(i).equals("Total"))
                {
                    // to add the new element to the piechart
                    entries.add(new PieEntry(dict.get(keys.get(i))));

                    // to add element to Legend
                    LegendEntry entry = new LegendEntry();
                    entry.formColor=pieColors[legendCounter];
                    entry.label=keys.get(i);
                    legendEntries.add(entry);
                    legendCounter+=1;
                }
            }
        }

        l.setCustom(legendEntries);
        PieDataSet pieDataSet = new PieDataSet(entries, "Subjects");

        pieDataSet.setColors(pieColors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(14f);

        // to display amounts without float
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        piechart.setData(pieData);
        piechart.getLegend().setWordWrapEnabled(true);
        piechart.getDescription().setEnabled(false);
        piechart.animateY(1000);
        piechart.invalidate();
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public int getAppColor(int resourceId) {
        Context context = getActivity();
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

    private HashMap<String, HashMap<String, Object>> loadTransactions() {
        HashMap<String, HashMap<String, Object>> loadedTransactions = new HashMap<>();
        Gson gson = new Gson();

        try (FileInputStream fis = getContext().openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            loadedTransactions = gson.fromJson(isr, new TypeToken<HashMap<String, HashMap<String, Object>>>() {}.getType());
        } catch (IOException e) {
            Log.e("Contact Activity", "File not found or error reading file", e);
        }

        return loadedTransactions != null ? loadedTransactions : new HashMap<>();
    }

    private void addTransaction(String expin, String date, String type, int amount) {
        HashMap<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("expin", expin);
        transactionDetails.put("type", type);
        transactionDetails.put("amount", amount);
        transactions.put(date, transactionDetails);
    }

    private void displayTransactions() {
        StringBuilder displayText = new StringBuilder();

        for (String date : transactions.keySet()) {
            HashMap<String, Object> details = transactions.get(date);
            String type = (String) details.get("type");
            Object expin = (Object) details.get("expin");
            Object amount = details.get("amount");
            displayText.append("expin: ").append(expin).append("Date: ").append(date)
                    .append(", Type: ").append(type)
                    .append(", Amount: ").append(amount)
                    .append("\n");
        }

        Log.i("DICT_Transact", displayText.toString());
    }

    private void saveTransactions(HashMap<String, HashMap<String, Object>> transactions) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(transactions);
        FileOutputStream fos = null;
        try  {
            fos = getContext().openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null){
                try{
                    fos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}