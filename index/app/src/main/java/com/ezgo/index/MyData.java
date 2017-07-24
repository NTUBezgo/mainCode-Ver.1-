package com.ezgo.index;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by 8320E on 2017/4/5.
 */

public class MyData {
    //建立各園區marker
    private String animalMarkers[][]={
            {"24.9985962","121.5805931","臺灣動物區"},{"24.9989718","121.5819383","兒童動物區"},
            {"24.9950215","121.5834188","亞洲熱帶雨林區"},{"24.9952621"," 121.5851489  ","沙漠動物區"},
            {"24.994184","121.5853326","澳洲動物區"},{"24.9951333","121.5880094","非洲動物區"},
            {"24.9931447","121.5896013","溫帶動物區"},{"24.9957179","121.5888946","鳥園區"},
            {"24.9929758","121.5911959","企鵝館"},{"24.9982291","121.5828744","無尾熊館"},
            {"24.9940697","121.5898494","兩棲爬蟲動物館"},{"24.9967402","121.5807004","昆蟲館"},
            {"24.9968265","121.5830956","大貓熊館"},
    };

    //建立地理圍欄範圍
    private Double geofenceList[][]={
            {25.00234, 121.48368, 1.0},
            {25.043130, 121.524834, 2.0},
            {25.002367, 121.484648, 3.0},
            {24.998719, 121.581380, 4.0},
            {24.998855, 121.582614, 5.0},
            {24.998709, 121.583150, 6.0}
    };

    //各園區的動物
    private String childrenZoo[]={"羊駝","亞洲水牛","松鼠猴","長鼻浣熊","家兔(馴化兔)","家豬"};

    private static boolean hideAnimal;

    public void displayHideAnimal(boolean isDisplay){
        if (isDisplay==true) hideAnimal=true;
        else if(isDisplay==false) hideAnimal=false;
    }

    public boolean getIsHideAnimal(){
        return hideAnimal;
    }

    public String[][] getAnimalMarkers(){
        return animalMarkers;
    }

    public Double[][] getGeofenceList(){
        return geofenceList;
    }

    //回傳各園區的動物清單
    public List<String> getAnimalList(String area){
        List<String> animalList=new ArrayList<>();

        if(area.equals("兒童動物區")){
            for(int i=0; i<childrenZoo.length; i++){
                animalList.add(childrenZoo[i]);
            }
        }
        return animalList;
    }


}
