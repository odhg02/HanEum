package com.example.byeongwoo.handroad;

import com.skt.Tmap.TMapPOIItem;

public class POI {
    TMapPOIItem item;

    public POI(TMapPOIItem item){
        this.item = item;
    }

    @Override
    public String toString() {
        return item.getPOIName();
    }
}
