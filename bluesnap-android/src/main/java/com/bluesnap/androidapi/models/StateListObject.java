package com.bluesnap.androidapi.models;

import java.util.ArrayList;

/**
 * Created by roy.biber on 15/06/2016.
 */
public class StateListObject {
    private String stateFullName;
    private String stateCode;

    public StateListObject(String stateFullName, String stateCode) {
        this.stateFullName = stateFullName;
        this.stateCode = stateCode;
    }

    public static ArrayList<StateListObject> getStateListObject(String[] stateFullNameList, String[] stateInitialList) {
        ArrayList<StateListObject> stateListObjects = new ArrayList<>();
        StateListObject stateListObject;
        for (int i = 0; i < stateFullNameList.length; i++) {
            stateListObject = new StateListObject(stateFullNameList[i], stateInitialList[i]);
            stateListObjects.add(stateListObject);
        }
        return stateListObjects;
    }

    public String getStateFullName() {
        return stateFullName;
    }

    public void setStateFullName(String stateFullNameListObjects) {
        this.stateFullName = stateFullNameListObjects;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    @Override
    public String toString() {
        return "CustomListObject{" +
                "stateFullName='" + stateFullName + '\'' +
                "stateCode='" + stateCode + '\'' +
                '}';
    }
}

