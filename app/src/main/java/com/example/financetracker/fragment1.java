package com.example.financetracker;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.Inflater;


public class fragment1 extends Fragment {
    PieChart piechart;
    EditText et1;
    Spinner spinner;

    private static final String FILE_NAME = "transactions.json";
    private HashMap<String, HashMap<String, Object>> transactions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_fragment1, container, false);

        // to add new expense
        final Button add1 = (Button) rootview.findViewById(R.id.add1);
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onClickAdd1(v);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);// прописать создание файла?
                } catch (IOException e) {
                    throw new RuntimeException(e);// ?
                }
            }
        });

        // to read info from Internal storage DEPRECATED
//        final Button buttonr = (Button) rootview.findViewById(R.id.buttonr);
//        buttonr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    onClickbuttonr(v);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });

        // to make reset of all the expenses values
//        final Button defaultbtn = (Button) rootview.findViewById(R.id.defaultbtn);
//        defaultbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HashMap<String, Integer> dictionary = new HashMap<>();
//                dictionary.put("Health", 0);
//                dictionary.put("Leisure", 0);
//                dictionary.put("Home", 0);
//                dictionary.put("Groceries", 0);
//                dictionary.put("Gifts", 0);
//                dictionary.put("Cafe", 0);
//                dictionary.put("Transport", 0);
//                dictionary.put("Cosmetics", 0);
//                dictionary.put("Clothes, shoes", 0);
//                dictionary.put("Total", 0);
//                saveDictionary(v.getContext(), dictionary, "total_expenses.txt");
//            }
//        });

        //to attach EditText field
        et1 = rootview.findViewById(R.id.et1);
        spinner = rootview.findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootview.getContext(),
                R.array.outcomes_array,
                android.R.layout.simple_spinner_item
        );

        //to attach spinner
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard(getActivity());
                return false; // чтобы спиннер работал после нажатия
            }
        });
        piechart = rootview.findViewById(R.id.chart1);

        Map<String, Integer> dict = loadDictionary(rootview.getContext(), "total_expenses.txt");
        UpdatePieChart(rootview, piechart, dict);

        return rootview;
    }

    //DEPRECATED
    private void onClickbuttonr(View v) throws IOException {

        File path = getActivity().getApplicationContext().getFilesDir();
        File file = new File(path, "total_expenses.txt");
        if (!file.exists())
        {
            Toast toast = Toast.makeText(v.getContext(), "file doesn't exist", Toast.LENGTH_SHORT);
            toast.show();
        }

        //Map<String, Integer> dict = loadDictionary(v.getContext(), "total_expenses.txt");

        //TextView tv1 = getActivity().findViewById(R.id.textViewr1);
        //tv1.setText(dict.toString());

    }

    //onViewCreated
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    // to update piechart
    private void UpdatePieChart(View v, PieChart piechart, Map<String, Integer> dict){

        ArrayList<String>keys = new ArrayList<String>(dict.keySet());
        ArrayList<PieEntry> entries = new ArrayList<>();

        File path = v.getContext().getApplicationContext().getFilesDir();
        File file = new File(path, "total_expenses.txt");
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

    // to add new expense into internal storage and piechart
    public void onClickAdd1(View view) throws IOException {

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss").format(Calendar.getInstance().getTime());

        HashMap<String, Integer> dict_total = new HashMap<>();
        dict_total = loadDictionary(getContext(),"total_expenses.txt");

        Object choice = spinner.getSelectedItem().toString();

        Integer expenseValue = 0;
        if( !et1.getText().toString().equals("") && et1.getText().toString().length() > 0 )
        {
            expenseValue = Integer.parseInt(et1.getText().toString());
            // expenses.txt
            transactions = loadTransactions();
            Log.i("BEFORE_", transactions.toString());
            addTransaction("Expense", String.valueOf(timeStamp),
                    String.valueOf(spinner.getSelectedItem()), expenseValue);
            saveTransactions(transactions);
            Log.i("AFTER_", transactions.toString());


            // Get String
            List<String> keys = new ArrayList<String>(dict_total.keySet());
            for(int i = 0; i < keys.size(); i++) {
                if (choice.equals(keys.get(i).toString())){

                    int editTextValue = expenseValue;
                    int previousValue = dict_total.get(keys.get(i));
                    dict_total.put(keys.get(i), editTextValue+previousValue);

                    int total = dict_total.get("Total");
                    dict_total.put("Total", total+editTextValue);
                    break;
                }
            }
        }
        else
        {
            Toast toast = Toast.makeText(getContext(), "Input a correct value of your expense", Toast.LENGTH_SHORT);
            toast.show();
        }
        // to gather all the values into the dictionary dict_total


        saveDictionary(getContext(), dict_total, "total_expenses.txt");
        View rootview = getView();
        UpdatePieChart(rootview, piechart, dict_total);

        // to hide keyboard by touching the spinner
        hideSoftKeyboard(getActivity());
    }

    // to save the dictionary into internal storage
    public void saveDictionary(Context context, HashMap<String, Integer> dictionary, String filename){
        JSONObject jsonObject = new JSONObject(dictionary);
        String jsonString = jsonObject.toString();
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, MODE_PRIVATE);
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

    // !!!!!!!!!
    public void saveExtendedDictionary(Context context, HashMap<String, String> dictionary, String filename){
        JSONObject jsonObject = new JSONObject(dictionary);
        String jsonString = jsonObject.toString();
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, MODE_PRIVATE);
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

    // to hide the system keyboard after inputting new expense amount
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

    // to set the colors for the piechart
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
        //         Log.i("FRA1", loadedTransactions.toString());
        return loadedTransactions != null ? loadedTransactions : new HashMap<>();
    }

    private void addTransaction(String expin, String date, String type, int amount) {
        HashMap<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("expin", expin);
        transactionDetails.put("type", type);
        transactionDetails.put("amount", amount);
        transactions.put(date, transactionDetails);
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