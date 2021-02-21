package com.alkaid.serviceb.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Visits {
    private int userid;
    private Date visitdate;
    private String website;
}
