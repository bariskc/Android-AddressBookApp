package com.example.bariskoc.addressbook.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by bariskoc on 24.08.2017.
 */

public class Kisi implements Serializable {
    private String id;
    private String ad, soyad, telefon, mail;


    public Kisi() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAd() {
        return ad.substring(0, 1).toUpperCase() + ad.substring(1).toLowerCase();
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad.toUpperCase();
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return String.format("%s %s", ad.substring(0, 1).toUpperCase() + ad.substring(1).toLowerCase(), soyad.toUpperCase());
    }
}
