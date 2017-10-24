package com.homeautogroup.cryptocurrencyconverter.model;

/**
 * Created by exodus on 10/20/17.
 */

/*
* Class required by Retrofit to map data after server response*/
public class CryptoCompare {

    private ETH ETH;

    private BTC BTC;

    public ETH getETH ()
    {
        return ETH;
    }

    public void setETH (ETH ETH)
    {
        this.ETH = ETH;
    }

    public BTC getBTC ()
    {
        return BTC;
    }

    public void setBTC (BTC BTC)
    {
        this.BTC = BTC;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ETH = "+ETH+", BTC = "+BTC+"]";
    }
}
