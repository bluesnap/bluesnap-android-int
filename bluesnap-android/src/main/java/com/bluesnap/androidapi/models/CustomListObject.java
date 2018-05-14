package com.bluesnap.androidapi.models;

import java.util.ArrayList;

/**
 * Created by roy.biber on 15/06/2016.
 */
public class CustomListObject {

    private String name;

    public CustomListObject(String name) {
        this.name = name;
    }

    public static ArrayList<CustomListObject> getCustomListObject(String[] listValue) {
        ArrayList<CustomListObject> customListObjects = new ArrayList<>();
        CustomListObject customListObject;
        for (int i = 0; i < listValue.length; i++) {
            customListObject = new CustomListObject(listValue[i]);
            customListObjects.add(customListObject);
        }
        return customListObjects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CustomListObject{" +
                "name='" + name + '\'' +
                '}';
    }
}

