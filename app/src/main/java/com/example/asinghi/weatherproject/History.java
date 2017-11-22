package com.example.asinghi.weatherproject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class History extends Fragment{

    Database dbHandler;
    View rootView;
    static History history = null;

    public static   History getInstance(){
        if(history != null)
            return history;
        return  new History();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        rootView = inflater.inflate(R.layout.history, container, false);

        //resulttext = (TextView) rootView.findViewById(R.id.resultText);


        dbHandler = new Database(getContext(),null,null,1);
        String[] dbString = dbHandler.databasetoString();
        String[] str = new String [dbString.length];
        for (int i=0; i < dbString.length; i++)
        {
            String f="";
            try {
                JSONObject data = new JSONObject(dbString[i]);
                double temp = data.getJSONObject("main").getDouble("temp") - 274.15;
                DecimalFormat two = new DecimalFormat("#0.00");
                String temperature = two.format(temp);
                String place = data.getString("name");
                f = f + place + " " + temperature;
                str[i] = f;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        MySimpleArrayAdapter adp = new MySimpleArrayAdapter(getContext() , str);

        ListView listView = (ListView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adp);
        return rootView;
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MySimpleArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.rows, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.first);
            textView.setText(values[position]);
            return rowView;
        }
    }



}
