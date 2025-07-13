package org.koreait.map.entity;

import lombok.Data;

@Data
public class Restaurant {
    private String id;
    private String name;
    private String category;
    private String address;
    private String roadAddress;
    private double lat;
    private double lon;
    private String phone;
    private String distance;
    private String placeUrl;
}
