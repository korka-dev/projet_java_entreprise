package org.post.models;
import java.sql.Date;

import java.io.Serializable;
public class User implements Serializable{

    int id;
    String nomComplet;
    String email;
    int age;
    Date service;
    double salaire;
    String poste;

    public User(){}

    public User(int id,String email ,String nomComplet, int age, Date service, double salaire,String poste){
        this.id=id;
        this.email=email;
        this.nomComplet = nomComplet;
        this.age = age;
        this.service = service;
        this.salaire = salaire;
        this.poste = poste;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public int getAge() {
        return age;
    }

    public Date getService() {
        return service;
    }

    public double getSalaire() {
        return salaire;
    }

    public String getPoste() {
        return poste;
    }


    @Override
    public String toString() {
        return String.format("| %-5d | %-30s | %-30s | %-6d | %-30tF | %-10.2f | %-20s |",
                this.id, this.email, this.nomComplet, this.age, this.service, this.salaire, this.poste);
    }


}

