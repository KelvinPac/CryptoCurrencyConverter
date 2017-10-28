package com.homeautogroup.cryptocurrencyconverter.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.homeautogroup.cryptocurrencyconverter.R;
import com.homeautogroup.cryptocurrencyconverter.adapters.CurrencyExchangeAdapter;
import com.homeautogroup.cryptocurrencyconverter.api.APIService;
import com.homeautogroup.cryptocurrencyconverter.api.APIUrl;
import com.homeautogroup.cryptocurrencyconverter.materialcolorpicker.ColorChooserDialog;
import com.homeautogroup.cryptocurrencyconverter.materialcolorpicker.ColorListener;
import com.homeautogroup.cryptocurrencyconverter.model.CurrencyExchange;
import com.homeautogroup.cryptocurrencyconverter.model.BTC;
import com.homeautogroup.cryptocurrencyconverter.model.CryptoCompare;
import com.homeautogroup.cryptocurrencyconverter.model.ETH;
import com.homeautogroup.cryptocurrencyconverter.utils.Constant;
import com.homeautogroup.cryptocurrencyconverter.utils.Theme;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    //Theme
    SharedPreferences sharedPreferences, app_preferences;
    SharedPreferences.Editor editor;
    Theme theme;
    int appTheme;
    int themeColor;
    int appColor;
    Constant constant;
    private RecyclerView recyclerView;
    private CurrencyExchangeAdapter adapter;
    private List<CurrencyExchange> currencyExchangeList;

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

        //setTheme(R.style.AppTheme_darpink);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


        //initialize variables  and object needed for new theme
        theme = new Theme();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

       // colorize();

        initCollapsingToolbar();
        //initialize the recyclerview

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        currencyExchangeList = new ArrayList<>();
        adapter = new CurrencyExchangeAdapter(this, currencyExchangeList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Fetch the exchange rates for 20 countries from CryptoCompare Api using Retrofit
        fetchLatestExchangeRates();

       /* try {
            Glide.with(this).load(R.drawable.crypto_community).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
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
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /*
    * Fetch latest exchange rates from crptocompare API using retrofit*/
    private void fetchLatestExchangeRates() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching latest exchange rates");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService service = retrofit.create(APIService.class);

        Call<CryptoCompare> call = service.getLatestExchangeRates();

        call.enqueue(new Callback<CryptoCompare>() {
            @Override
            public void onResponse(@NonNull Call<CryptoCompare> call, @NonNull Response<CryptoCompare> response) {
                progressDialog.dismiss();
                /*
                * All successful responses from crypto compare have a http code 200
                * Check if http code is 200 then continue
                * */
                if (response.code()==200){


                    try{
                        Log.e(TAG,response.body().toString());
                        //Toast.makeText(MainActivity.this,  response.body().toString(), Toast.LENGTH_SHORT).show();

                        //Get ether and btc objects from the response body
                        ETH ether = response.body().getETH();
                        BTC bitcoin = response.body().getBTC();

                        // Log.e(TAG,ether.getEUR()+ " "+ether.getKES()+" "+ether.getUSD());

                        //Prepare RecyclerView data passing the 2 objects as  parameters
                        prepareExchangeRates(ether,bitcoin);

                    }catch (NullPointerException e){
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

    /**
     * Adding few albums for testing
     * @param ether
     * @param bitcoin
     */

    //TODO add currency short symbol
    private void prepareExchangeRates(ETH ether, BTC bitcoin) {

        //Log.e("MainActivity",ether.toString());
        //Toast.makeText(this, ether.toString(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, eth1.getKES(), Toast.LENGTH_SHORT).show();
        int[] covers = new int[]{
                R.drawable.nigeria,
                R.drawable.kenya,
                R.drawable.usa,
                R.drawable.british,
                R.drawable.china,
                R.drawable.swiss,
                R.drawable.euro,
                R.drawable.mexico,
                R.drawable.gilbraltar,
                R.drawable.jordan,
                R.drawable.oman,
                R.drawable.bahraini,
                R.drawable.kuwait,
                R.drawable.japan,
                R.drawable.canada,
                R.drawable.uae,
                R.drawable.chile,
                R.drawable.brazil,
                R.drawable.india,
                R.drawable.israel
        };

        CurrencyExchange a = new CurrencyExchange("Nigeria", "NGN",String.valueOf(bitcoin.getNGN()), covers[0],String.valueOf(ether.getNGN()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Kenya","KES", String.valueOf(bitcoin.getKES()), covers[1],String.valueOf(ether.getKES()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("USA","USD", String.valueOf(bitcoin.getUSD()), covers[2],String.valueOf(ether.getUSD()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Britain","GBP", String.valueOf(bitcoin.getGBP()), covers[3],String.valueOf(ether.getGBP()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("China","CNY",String.valueOf(bitcoin.getCNY()), covers[4],String.valueOf(ether.getCNY()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Swirzerland","CHF" ,String.valueOf(bitcoin.getCHF()), covers[5],String.valueOf(ether.getCHF()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("EURO","EUR",String.valueOf(bitcoin.getEUR()), covers[6],String.valueOf(ether.getEUR()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Mexico","MXN", String.valueOf(bitcoin.getMXN()), covers[7],String.valueOf(ether.getMXN()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Gibraltar","GLD", String.valueOf(bitcoin.getGLD()), covers[8],String.valueOf(ether.getGLD()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Jordan","JOD", String.valueOf(bitcoin.getJOD()), covers[9],String.valueOf(ether.getJOD()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Oman","OMR", String.valueOf(bitcoin.getOMR()), covers[10],String.valueOf(ether.getOMR()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Bahrain","BHD", String.valueOf(bitcoin.getBHD()), covers[11],String.valueOf(ether.getBHD()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Kuwait", "KWD",String.valueOf(bitcoin.getKWD()), covers[12],String.valueOf(ether.getKWD()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Japan","JPY", String.valueOf(bitcoin.getJPY()), covers[13],String.valueOf(ether.getJPY()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Canada","CAD", String.valueOf(bitcoin.getCAD()), covers[14],String.valueOf(ether.getCAD()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("UAE","AED", String.valueOf(bitcoin.getAED()), covers[15],String.valueOf(ether.getAED()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Chile","CLP", String.valueOf(ether.getCLP()), covers[16],String.valueOf(ether.getCLP()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Brazil", "BRL",String.valueOf(bitcoin.getBRL()), covers[17],String.valueOf(ether.getBRL()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("India", "INR",String.valueOf(bitcoin.getINR()), covers[18],String.valueOf(ether.getINR()));
        currencyExchangeList.add(a);

        a = new CurrencyExchange("Israel","ILS", String.valueOf(bitcoin.getILS()), covers[19],String.valueOf(ether.getILS()));
        currencyExchangeList.add(a);

        adapter.notifyDataSetChanged();
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_theme){
            ColorChooserDialog dialog = new ColorChooserDialog(MainActivity.this);
            dialog.setTitle("Select");
            dialog.setColorListener(new ColorListener() {
                @Override
                public void OnColorClick(View v, int color) {
                   // colorize();
                    Constant.color = color;

                    theme.setColorTheme();
                    editor.putInt("color", color);
                    editor.putInt("theme",Constant.theme);
                    editor.commit();
                   // Toast.makeText(MainActivity.this,"Changes to be shown on next restart",Toast.LENGTH_SHORT).show();

                    recreate();
                }
            });

            dialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }



}
