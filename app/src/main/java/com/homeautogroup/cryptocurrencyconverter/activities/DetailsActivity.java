package com.homeautogroup.cryptocurrencyconverter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.homeautogroup.cryptocurrencyconverter.R;

public class DetailsActivity extends AppCompatActivity {

    private Button showEtherRate,showBtcRate;
    //private TextView CurrencyShortCodeEther;

    //Intent data
    String currencyName,bitcoinPrice,etherPrice,shortCode;

    //card view Bitcoin conversion
    private TextView cardBtcTitle,cardBtcCurrencyShortCode1,cardBtcCurrencyShortCode2,cardBtcConversionAnswer;
    private EditText cardBtcInput;
    private Button cardBtcViceVersa,cardBtcConvertCurrencies;
    boolean convertBtcInViceVersaMode = false;

    //card view Ether conversion
    private TextView cardEthTitle,cardEthCurrencyShortCode1,cardEthCurrencyShortCode2,cardEthConversionAnswer;
    private EditText cardEthInput;
    private Button cardEthViceVersa,cardEthConvertCurrencies;
    boolean convertEthInViceVersaMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showEtherRate = (Button) findViewById(R.id.btnEthRate);
        showBtcRate = (Button)findViewById(R.id.btnBtcRate);

        //CurrencyShortCodeEther = (TextView)findViewById(R.id.txtViewCurrencyShortCodeEther);

        //initialize the BTC card view views
        initCardViewBitcoinConversion();

        //initialize the Ether card view views
        initCardViewEtherConversion();
        Intent intentIncoming = getIntent();
        if (intentIncoming.hasExtra("name")){
            currencyName = getIntent().getExtras().getString("name");
            bitcoinPrice = getIntent().getExtras().getString("btc");
            etherPrice = getIntent().getExtras().getString("ether");
            shortCode = getIntent().getExtras().getString("shortCode");
            int thumbnail = getIntent().getExtras().getInt("thumbnail");


            Toast.makeText(this, currencyName+" BTC= "+bitcoinPrice+" ETH= "+etherPrice, Toast.LENGTH_SHORT).show();
            /*Glide.with(this)
                    .load(thumbnail)
                    .into(imageView);
            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);*/

            showEtherRate.setText("ETH "+etherPrice);
            showBtcRate.setText("BTC "+bitcoinPrice);
            cardBtcCurrencyShortCode1.setText(shortCode);
            cardEthCurrencyShortCode1.setText(shortCode);

        } else {
            Toast.makeText(this, "Error Occurred. No API Data", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    //card view Bitcoin conversion
    private void initCardViewBitcoinConversion() {
        cardBtcCurrencyShortCode1 = (TextView)findViewById(R.id.crdBtc_currencyShortCode1);
        cardBtcCurrencyShortCode2 = (TextView)findViewById(R.id.crdBtc_currencyShortCode2);
        cardBtcTitle = (TextView)findViewById(R.id.crdBtc_conversionTitle);
        cardBtcConversionAnswer = (TextView)findViewById(R.id.crdBtc_conversionAnswer);
        cardBtcInput = (EditText)findViewById(R.id.crdBtc_Input);
        cardBtcViceVersa = (Button)findViewById(R.id.crdBtc_BtnViceVersa);
        cardBtcConvertCurrencies = (Button)findViewById(R.id.crdBtc_ConvertCurrencies);

        //Setting input fields to vice versa to support both way conversion
        cardBtcViceVersa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if conversion mode is vice versa then return to normal mode
                if (convertBtcInViceVersaMode){
                    convertBtcInViceVersaMode = false;
                    cardBtcTitle.setText("Convert from "+shortCode+" to "+"BTC");
                    cardBtcCurrencyShortCode1.setText(shortCode);
                    cardBtcCurrencyShortCode2.setText("BTC");
                    cardBtcConversionAnswer.setText("");
                    cardBtcInput.setText("");
                }else {
                    //if conversion mode normal then return to vice versa mode
                    convertBtcInViceVersaMode = true;
                    cardBtcTitle.setText("Convert from "+"BTC to "+shortCode);
                    cardBtcCurrencyShortCode1.setText("BTC");
                    cardBtcCurrencyShortCode2.setText(shortCode);
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
                double inputToBeConverted =  Double.parseDouble(input);
                //Do the conversion
                    //Converting BTC to {shortCode}
                    doBTCConversion(bitcoinPrice,inputToBeConverted);

            }
        });
    }



    //card view Ether conversion
    private void initCardViewEtherConversion() {
        cardEthCurrencyShortCode1 = (TextView)findViewById(R.id.crdEth_currencyShortCode1);
        cardEthCurrencyShortCode2 = (TextView)findViewById(R.id.crdEth_currencyShortCode2);
        cardEthTitle = (TextView)findViewById(R.id.crdEth_conversionTitle);
        cardEthConversionAnswer = (TextView)findViewById(R.id.crdEth_conversionAnswer);
        cardEthInput = (EditText)findViewById(R.id.crdEth_Input);
        cardEthViceVersa = (Button)findViewById(R.id.crdEth_BtnViceVersa);
        cardEthConvertCurrencies = (Button)findViewById(R.id.crdEth_ConvertCurrencies);

        //Setting input fields to vice versa to support both way conversion
        cardEthViceVersa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if conversion mode is vice versa then return to normal mode
                if (convertEthInViceVersaMode){
                    convertEthInViceVersaMode = false;
                    cardEthTitle.setText("Convert from "+shortCode+" to "+"ETH");
                    cardEthCurrencyShortCode1.setText(shortCode);
                    cardEthCurrencyShortCode2.setText("ETH");
                    cardEthConversionAnswer.setText("");
                    cardEthInput.setText("");
                }else {
                    //if conversion mode normal then return to vice versa mode
                    convertEthInViceVersaMode = true;
                    cardEthTitle.setText("Convert from "+"ETH to "+shortCode);
                    cardEthCurrencyShortCode1.setText("ETH");
                    cardEthCurrencyShortCode2.setText(shortCode);
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
                double inputToBeConverted =  Double.parseDouble(input);
                //Do the conversion
                //Converting BTC to {shortCode}
                doEthConversion(etherPrice,inputToBeConverted);

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
        double btcPrice =  Double.parseDouble(bitcoinPrice);

        if (!convertBtcInViceVersaMode){
            //Convert Other selected currency to BTC
           double answer = inputToBeConverted/btcPrice;
            cardBtcConversionAnswer.setText(String.valueOf(answer));
        }else {
            //Convert BTC to Other selected currency
            double answer = inputToBeConverted*btcPrice;
            cardBtcConversionAnswer.setText(String.valueOf(answer));
        }
    }

    private void doEthConversion(String ethPrice, double inputToBeConverted) {
        //change the bitcoinPrice into a double
        double etherPrice =  Double.parseDouble(ethPrice);

        if (!convertEthInViceVersaMode){
            //Convert Other selected currency to ETH
            double answer = inputToBeConverted/etherPrice;
            cardEthConversionAnswer.setText(String.valueOf(answer));
        }else {
            //Convert ETH to Other selected currency
            double answer = inputToBeConverted*etherPrice;
            cardEthConversionAnswer.setText(String.valueOf(answer));
        }
    }

}
