package com.homeautogroup.cryptocurrencyconverter.utils;


import com.homeautogroup.cryptocurrencyconverter.R;

public class Theme {

    public void setColorTheme(){

        switch (Constant.color){
            //case 0xffF44336:
            case 0xffFF0000:
                Constant.theme = R.style.AppTheme_red;
                break;
            case 0xffE91E63:
                Constant.theme = R.style.AppTheme_pink;
                break;
            case 0xff9B26AF:
                Constant.theme = R.style.AppTheme_darpink;
                break;
            case 0xff6639B6:
                Constant.theme = R.style.AppTheme_violet;
                break;
            case 0xff3F51B5:
                Constant.theme = R.style.AppTheme_blue;
                break;
            case 0xff03A9F4:
                Constant.theme = R.style.AppTheme_skyblue;
                break;
            case 0xff388E3C:
                Constant.theme = R.style.AppTheme_green;
                break;
            case 0xffFF9800:
                Constant.theme = R.style.AppTheme;
                break;
            case 0xff9D9D9D:
                Constant.theme = R.style.AppTheme_grey;
                break;
            case 0xff795548:
                Constant.theme = R.style.AppTheme_brown;
                break;
            default:
                Constant.theme = R.style.AppTheme;
                break;
        }
    }
}
