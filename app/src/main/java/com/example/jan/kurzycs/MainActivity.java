package com.example.jan.kurzycs;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    //Constants
    private final String BASE_URL = "https://www.csast.csas.cz/webapi/api/v1/rates/exchangerates";
    private final String API_KEY = "";

    //Variables
    private int noOfCurrencies = 21;

    TableLayout mTableLayout;
    TextView dateView;
    TableRow mTableRow[] = new TableRow[noOfCurrencies];
    TableRow.LayoutParams mLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
    TextView[][] mTextViews = new TextView[noOfCurrencies][4];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        letsDoSomeNetworking(BASE_URL);
// table format
        dateView = (TextView) findViewById(R.id.date_label);
        mTableLayout = (TableLayout) findViewById(R.id.tab_layout);

        for(int i = 0; i < noOfCurrencies; i++) {
            mTableRow[i] = new TableRow(this);
            mTableRow[i].setLayoutParams(mLayoutParams);
            mTableRow[i].setVisibility(View.VISIBLE);

            for(int j = 0; j < 4; j++) {
                mTextViews[i][j] = new TextView(this);
                mTextViews[i][j].setLayoutParams(mLayoutParams);
                mTextViews[i][j].setVisibility(View.VISIBLE);
                mTextViews[i][j].setTextSize(16);
                mTextViews[i][j].setTextColor(getResources().getColor(R.color.textColor));
                mTextViews[i][j].setPadding(20,0,20,0);
                mTextViews[i][j].setGravity(Gravity.CENTER);
                mTableRow[i].addView(mTextViews[i][j]);
            }

            if (i==0) {
                mTextViews[0][0].setText(R.string.country_header);
                mTextViews[0][1].setText(R.string.currency_header);
                mTextViews[0][2].setText(R.string.buy_header);
                mTextViews[0][3].setText(R.string.sell_header);
            }

            mTableLayout.addView(mTableRow[i], new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
        }

        for(int j = 0; j < 4; j++) {
            mTextViews[0][j].setTypeface(null, Typeface.BOLD);
        }

    }
// API connection attempt
    public void letsDoSomeNetworking(String url) {

        Toast.makeText(getApplicationContext(), R.string.connecting_msg, Toast.LENGTH_SHORT).show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("web-api-key",API_KEY);
        client.get(url, new JsonHttpResponseHandler() {
// successful connection to API
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("KurzyCS", "Status " + statusCode);
                Log.d("KurzyCS", "JSON: " + response.toString());

                for (int i = 1; i < noOfCurrencies; i++) {
                    try {
                        JSONObject json = response.getJSONObject(i);

// validity date display
                        String date = json.getString("validFrom");
                        String year = date.substring(0,4);
                        String month = date.substring(5,7);
                        if (month.startsWith("0")) {
                            month = month.substring(1);
                        }
                        String day = date.substring(8,10);
                        if (day.startsWith("0")) {
                            day = day.substring(1);
                        }

                        //String validityDateLabel = (String) R.string.validity_date;
                        dateView.setText(getString(R.string.validity_date) + " " + day + ". " + month + ". " +year);
// table display
                        if (json.getLong("amount") == 1.0) {
                            mTextViews[i][1].setText(json.getString("shortName"));
                        } else {
                            mTextViews[i][1].setText(json.getLong("amount") + " " + json.getString("shortName"));
                        }

                        mTextViews[i][0].setText(json.getString("country"));
                        mTextViews[i][2].setText(json.getString("currBuy"));
                        mTextViews[i][3].setText(json.getString("currSell"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("KurzyCS", "Exception occured");
                    }

                }
            }
// API connection error
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("KurzyCS", "Status " + statusCode);
                Log.d("KurzyCS", "Fail response: " + response);
                Toast.makeText(getApplicationContext(),R.string.connection_error_msg,Toast.LENGTH_SHORT).show();
            }
        });

    }

}
