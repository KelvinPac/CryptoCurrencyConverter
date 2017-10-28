package com.homeautogroup.cryptocurrencyconverter.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.homeautogroup.cryptocurrencyconverter.R;
import com.homeautogroup.cryptocurrencyconverter.api.APIService;
import com.homeautogroup.cryptocurrencyconverter.api.APIUrl;
import com.homeautogroup.cryptocurrencyconverter.model.BTC;
import com.homeautogroup.cryptocurrencyconverter.model.CryptoCompare;
import com.homeautogroup.cryptocurrencyconverter.model.ETH;
import com.homeautogroup.cryptocurrencyconverter.utils.Constant;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();
    //Intent data
    String currencyName, bitcoinPrice, etherPrice, shortCode;
    boolean convertBtcInViceVersaMode = false;
    boolean convertEthInViceVersaMode = false;
    //Theme
    SharedPreferences app_preferences;
    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;
    //Buttons showing exchange rates
    private Button showEtherRate, showBtcRate;
    //card view Bitcoin conversion
    private TextView cardBtcTitle, cardBtcCurrencyShortCode1, cardBtcCurrencyShortCode2, cardBtcConversionAnswer;
    private EditText cardBtcInput;
    private Button cardBtcViceVersa, cardBtcConvertCurrencies;
    //card view Ether conversion
    private TextView cardEthTitle, cardEthCurrencyShortCode1, cardEthCurrencyShortCode2, cardEthConversionAnswer;
    private EditText cardEthInput;
    private Button cardEthViceVersa, cardEthConvertCurrencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //themes
        app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appColor = app_preferences.getInt("color", 0);
        appTheme = app_preferences.getInt("theme", 0);
        themeColor = appColor;
        constant.color = appColor;

        if (themeColor == 0){
            setTheme(Constant.theme);
        }else if (appTheme == 0){
            setTheme(Constant.theme);
        }else{
            setTheme(appTheme);
        }



        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setup the collapsing toolbar layout
        initCollapsingToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refrehing exchange rates", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                refreshExchangeRates();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //initialize views
        showEtherRate = (Button) findViewById(R.id.btnEthRate);
        showBtcRate = (Button) findViewById(R.id.btnBtcRate);


        //initialize the BTC card view views
        initCardViewBitcoinConversion();

        //initialize the Ether card view views
        initCardViewEtherConversion();

        //Read data from the intent from MainActivity
        Intent intentIncoming = getIntent();
        //check if data is available using one submitted field
        if (intentIncoming.hasExtra("name")) {
            //Intent data is available  so read and store it
            currencyName = getIntent().getExtras().getString("name");
            bitcoinPrice = getIntent().getExtras().getString("btc");
            etherPrice = getIntent().getExtras().getString("ether");
            shortCode = getIntent().getExtras().getString("shortCode");
            int thumbnail = getIntent().getExtras().getInt("thumbnail");


            Toast.makeText(this, currencyName + " BTC= " + bitcoinPrice + " ETH= " + etherPrice, Toast.LENGTH_SHORT).show();
            /*Glide.with(this)
                    .load(thumbnail)
                    .into(imageView);
            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);*/

            //set the exchange rates for ETH to the button
            showEtherRate.setText("ETH " + etherPrice);
            //set the exchange rates for BTC to the button
            showBtcRate.setText("BTC " + bitcoinPrice);

            //Set the currency shortcodes to the view
            cardBtcCurrencyShortCode1.setText(shortCode);
            cardEthCurrencyShortCode1.setText(shortCode);

        }
        //intent data was not received
        else {
            Toast.makeText(this, "Error Occurred. No API Data", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    //card view Bitcoin conversion
    private void initCardViewBitcoinConversion() {

        //finding and initializing the views
        cardBtcCurrencyShortCode1 = (TextView) findViewById(R.id.crdBtc_currencyShortCode1);
        cardBtcCurrencyShortCode2 = (TextView) findViewById(R.id.crdBtc_currencyShortCode2);
        cardBtcTitle = (TextView) findViewById(R.id.crdBtc_conversionTitle);
        cardBtcConversionAnswer = (TextView) findViewById(R.id.crdBtc_conversionAnswer);
        cardBtcInput = (EditText) findViewById(R.id.crdBtc_Input);
        cardBtcViceVersa = (Button) findViewById(R.id.crdBtc_BtnViceVersa);
        cardBtcConvertCurrencies = (Button) findViewById(R.id.crdBtc_ConvertCurrencies);

        //Setting input fields to vice versa to support both way conversion
        cardBtcViceVersa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if conversion mode is vice versa then return to normal mode
                if (convertBtcInViceVersaMode) {
                    convertBtcInViceVersaMode = false;
                    cardBtcTitle.setText("Convert from " + shortCode + " to " + "BTC");
                    cardBtcCurrencyShortCode1.setText(shortCode);
                    cardBtcCurrencyShortCode2.setText("BTC");
                    //clear previous data
                    cardBtcConversionAnswer.setText("");
                    cardBtcInput.setText("");
                } else {
                    //if conversion mode normal then return to vice versa mode
                    convertBtcInViceVersaMode = true;
                    cardBtcTitle.setText("Convert from " + "BTC to " + shortCode);
                    cardBtcCurrencyShortCode1.setText("BTC");
                    cardBtcCurrencyShortCode2.setText(shortCode);
                    //clear previous data
                    cardBtcConversionAnswer.setText("");
                    cardBtcInput.setText("");
                }
            }
        });

        cardBtcConvertCurrencies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read the input from the user
                String input = cardBtcInput.getText().toString();
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(DetailsActivity.this, "Please enter a value to convert", Toast.LENGTH_SHORT).show();
                    return;
                }
                //parse the input from the string
                double inputToBeConverted = Double.parseDouble(input);
                //Do the conversion
                //Converting BTC to {shortCode}
                doBTCConversion(bitcoinPrice, inputToBeConverted);

            }
        });
    }


    //card view Ether conversion
    private void initCardViewEtherConversion() {
        //finding and initializing the views
        cardEthCurrencyShortCode1 = (TextView) findViewById(R.id.crdEth_currencyShortCode1);
        cardEthCurrencyShortCode2 = (TextView) findViewById(R.id.crdEth_currencyShortCode2);
        cardEthTitle = (TextView) findViewById(R.id.crdEth_conversionTitle);
        cardEthConversionAnswer = (TextView) findViewById(R.id.crdEth_conversionAnswer);
        cardEthInput = (EditText) findViewById(R.id.crdEth_Input);
        cardEthViceVersa = (Button) findViewById(R.id.crdEth_BtnViceVersa);
        cardEthConvertCurrencies = (Button) findViewById(R.id.crdEth_ConvertCurrencies);

        //Setting input fields to vice versa to support both way conversion
        cardEthViceVersa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if conversion mode is vice versa then return to normal mode
                if (convertEthInViceVersaMode) {
                    convertEthInViceVersaMode = false;
                    cardEthTitle.setText("Convert from " + shortCode + " to " + "ETH");
                    cardEthCurrencyShortCode1.setText(shortCode);
                    cardEthCurrencyShortCode2.setText("ETH");
                    //clear previous data
                    cardEthConversionAnswer.setText("");
                    cardEthInput.setText("");
                } else {
                    //if conversion mode normal then return to vice versa mode
                    convertEthInViceVersaMode = true;
                    cardEthTitle.setText("Convert from " + "ETH to " + shortCode);
                    cardEthCurrencyShortCode1.setText("ETH");
                    cardEthCurrencyShortCode2.setText(shortCode);
                    //clear previous data
                    cardEthConversionAnswer.setText("");
                    cardEthInput.setText("");
                }
            }
        });

        cardEthConvertCurrencies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read the input from the user
                String input = cardEthInput.getText().toString();
                //check if input is empty
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(DetailsActivity.this, "Please enter a value to convert", Toast.LENGTH_SHORT).show();
                    return;
                }
                double inputToBeConverted = Double.parseDouble(input);
                //Do the conversion
                //Converting BTC to {shortCode}
                doEthConversion(etherPrice, inputToBeConverted);

            }
        });
    }


    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_details);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Currency Exchange Conversion");

                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void doBTCConversion(String bitcoinPrice, double inputToBeConverted) {
        //change the bitcoinPrice into a double
        double btcPrice = Double.parseDouble(bitcoinPrice);

        if (!convertBtcInViceVersaMode) {
            //Convert Other selected currency to BTC
            double answer = inputToBeConverted / btcPrice;
            cardBtcConversionAnswer.setText(String.valueOf(answer));
        } else {
            //Convert BTC to Other selected currency
            double answer = inputToBeConverted * btcPrice;
            cardBtcConversionAnswer.setText(String.valueOf(answer));
        }
    }

    private void doEthConversion(String ethPrice, double inputToBeConverted) {
        //change the bitcoinPrice into a double
        double etherPrice = Double.parseDouble(ethPrice);

        if (!convertEthInViceVersaMode) {
            //Convert Other selected currency to ETH
            double answer = inputToBeConverted / etherPrice;
            cardEthConversionAnswer.setText(String.valueOf(answer));
        } else {
            //Convert ETH to Other selected currency
            double answer = inputToBeConverted * etherPrice;
            cardEthConversionAnswer.setText(String.valueOf(answer));
        }
    }

    /*
    * Fetch latest exchange rates from crptocompare API using retrofit*/
    private void refreshExchangeRates() {
        if (TextUtils.isEmpty(shortCode) || shortCode.length() != 3) {
            Toast.makeText(this, "Sorry. We could not refresh the exchange rates", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Refreshing exchange rates");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);

        Call<CryptoCompare> call = service.refreshExchangeRates("ETH,BTC", shortCode);

        call.enqueue(new Callback<CryptoCompare>() {
            @Override
            public void onResponse(@NonNull Call<CryptoCompare> call, @NonNull Response<CryptoCompare> response) {
                progressDialog.dismiss();

                /*
                * All successful responses from crypto compare have a http code 200
                * Check if http code is 200 then continue
                * */
                if (response.code() == 200) {
                    try {
                        Log.e(TAG, response.body().toString());

                        //Get ether and btc objects from the response body
                        ETH ether = response.body().getETH();
                        BTC bitcoin = response.body().getBTC();

                        Toast.makeText(DetailsActivity.this, String.valueOf(response.body()), Toast.LENGTH_SHORT).show();

                        //construct getterString
                        String getter = "get" + shortCode;
                        Gson gson = new Gson();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CryptoCompare> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}
