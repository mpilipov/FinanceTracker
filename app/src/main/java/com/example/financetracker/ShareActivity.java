package com.example.financetracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ShareActivity extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_share, null, false);
        drawerLayout.addView(contentView, 0);

        HashMap<String, HashMap<String, Object>> transactions;
        transactions = loadTransactions();
        final Button export = (Button) contentView.findViewById(R.id.export);

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToExcel(contentView.getContext(), transactions);

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static void exportToExcel(Context context, HashMap<String, HashMap<String, Object>> dataMap) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create header row
        Row headerRow = sheet.createRow(0);
        int headerCellIndex = 0;
        headerRow.createCell(headerCellIndex++).setCellValue("Key");

        // Get first inner map to extract header keys
        HashMap<String, Object> firstInnerMap = dataMap.values().iterator().next();
        for (String headerKey : firstInnerMap.keySet()) {
            headerRow.createCell(headerCellIndex++).setCellValue(headerKey);
        }

        // Create data rows
        int rowIndex = 1;
        for (Map.Entry<String, HashMap<String, Object>> entry : dataMap.entrySet()) {
            Row dataRow = sheet.createRow(rowIndex++);
            int cellIndex = 0;
            dataRow.createCell(cellIndex++).setCellValue(entry.getKey());
            HashMap<String, Object> innerMap = entry.getValue();
            for (Object value : innerMap.values()) {
                dataRow.createCell(cellIndex++).setCellValue(value.toString());
            }
        }

        // Write the output to a file
        String fileName = "exported_data.xlsx";
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            workbook.write(fileOutputStream);
            workbook.close();
            Toast toast = Toast.makeText(context, "All the data was exported to Excel format successfully", Toast.LENGTH_SHORT);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //openExcelFile(context, file);
    }
    private HashMap<String, HashMap<String, Object>> loadTransactions() {
        HashMap<String, HashMap<String, Object>> loadedTransactions = new HashMap<>();
        Gson gson = new Gson();

        try (FileInputStream fis = getApplicationContext().openFileInput("transactions.json");
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            loadedTransactions = gson.fromJson(isr, new TypeToken<HashMap<String, HashMap<String, Object>>>() {}.getType());
        } catch (IOException e) {
            Log.e("Contact Activity", "File not found or error reading file", e);
        }
        //         Log.i("FRA1", loadedTransactions.toString());
        return loadedTransactions != null ? loadedTransactions : new HashMap<>();
    }

    private static void openExcelFile(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}