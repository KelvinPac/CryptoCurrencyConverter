package com.homeautogroup.cryptocurrencyconverter.model;

/**
 *
 */
public class CurrencyExchange {
    private String currencyName;
    private String bitcoinPrice;
    private int thumbnail;
    private String etherPrice;
    private String currencyShortCode;

    public CurrencyExchange() {
    }

    public CurrencyExchange(String currencyName,String currencyShortCode, String bitcoinPrice, int thumbnail, String etherPrice) {
        this.currencyName = currencyName;
        this.bitcoinPrice = bitcoinPrice;
        this.thumbnail = thumbnail;
        this.etherPrice = etherPrice;
        this.currencyShortCode = currencyShortCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String name) {
        this.currencyName = name;
    }

    public String getBitcoinPrice() {
        return bitcoinPrice;
    }

    public void setBitcoinPrice(String bitcoinPrice) {
        this.bitcoinPrice = bitcoinPrice;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getEtherPrice() {
        return etherPrice;
    }

    public void setEtherPrice(String etherPrice) {
        this.etherPrice = etherPrice;
    }

    public String getCurrencyShortCode() {
        return currencyShortCode;
    }

    public void setCurrencyShortCode(String currencyShortCode) {
        this.currencyShortCode = currencyShortCode;
    }
}
