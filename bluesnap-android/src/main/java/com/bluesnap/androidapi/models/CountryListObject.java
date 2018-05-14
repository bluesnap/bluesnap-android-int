package com.bluesnap.androidapi.models;

import java.util.ArrayList;

/**
 * Created by roy.biber on 15/06/2016.
 */
public class CountryListObject {
    private String countryFullName;
    private String countryInitial;
    private int drawable;

    public CountryListObject(String countryFullName, String countryInitial, int drawable) {
        this.countryFullName = countryFullName;
        this.countryInitial = countryInitial;
        this.drawable = drawable;
    }

    public static ArrayList<CountryListObject> getCountryListObject(String[] countryFullNameList, String[] countryInitialList, int[] drawableList) {
        ArrayList<CountryListObject> countryListObjects = new ArrayList<>();
        CountryListObject countryListObject;
        for (int i = 0; i < countryFullNameList.length; i++) {
            countryListObject = new CountryListObject(countryFullNameList[i], countryInitialList[i], drawableList[i]);
            countryListObjects.add(countryListObject);
        }
        return countryListObjects;
    }

    public String getCountryFullName() {
        return countryFullName;
    }

    public void setCountryFullName(String countryFullNameListObjects) {
        this.countryFullName = countryFullNameListObjects;
    }

    public String getCountryInitial() {
        return countryInitial;
    }

    public void setCountryInitial(String countryInitial) {
        this.countryInitial = countryInitial;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    @Override
    public String toString() {
        return "CustomListObject{" +
                "countryFullName='" + countryFullName + '\'' +
                "countryInitial='" + countryInitial + '\'' +
                '}';
    }
}

