package com.homeautogroup.cryptocurrencyconverter.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.homeautogroup.cryptocurrencyconverter.R;
import com.homeautogroup.cryptocurrencyconverter.activities.DetailsActivity;
import com.homeautogroup.cryptocurrencyconverter.model.CurrencyExchange;

import java.util.List;

public class CurrencyExchangeAdapter extends RecyclerView.Adapter<CurrencyExchangeAdapter.MyViewHolder> {

    private Context mContext;
    private List<CurrencyExchange> currencyExchangeList;
    private int previousPosition =0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, count,eth;
        ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            eth = (TextView)view.findViewById(R.id.countEthr);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public CurrencyExchangeAdapter(Context mContext, List<CurrencyExchange> currencyExchangeList) {
        this.mContext = mContext;
        this.currencyExchangeList = currencyExchangeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CurrencyExchange currencyExchange = currencyExchangeList.get(position);
        holder.title.setText(currencyExchange.getCurrencyName());
        holder.count.setText("BTC "+ currencyExchange.getBitcoinPrice());
        holder.eth.setText("ETH "+ currencyExchange.getEtherPrice());

        // loading currencyExchange cover using Glide library
        Glide.with(mContext).load(currencyExchange.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mContext.startActivity(new Intent(mContext,DetailsActivity.class));
                Intent conversionActivity = new Intent(mContext,DetailsActivity.class);
                conversionActivity.putExtra("btc",currencyExchange.getBitcoinPrice());
                conversionActivity.putExtra("ether",currencyExchange.getEtherPrice());
                conversionActivity.putExtra("name",currencyExchange.getCurrencyName());
                conversionActivity.putExtra("thumbnail",currencyExchange.getThumbnail());
                conversionActivity.putExtra("shortCode",currencyExchange.getCurrencyShortCode());
                mContext.startActivity(conversionActivity);
            }
        });

        if (position>previousPosition){
            animate(holder,false);
        }else {
            previousPosition=position;
        }
        animate(holder, true);

    }

    private void animate(MyViewHolder holder, boolean b) {
        ObjectAnimator animateTranslateY = ObjectAnimator.ofFloat(holder.itemView,"translationY", b==true?100:-100,0);
        animateTranslateY.setDuration(1000);
        animateTranslateY.start();
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return currencyExchangeList.size();
    }
}
