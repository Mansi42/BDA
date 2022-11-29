package com.example.bda.Model;

public class User {
    String name,bloodgroup,email,id,idnumber,phonenumber,profilepictureUrl,search,type;

    public User() {
    }

    public User(String name, String bloodgroup, String email, String id, String idnumber, String phonenumber, String profilepictureUrl, String search, String type) {
        this.name = name;
        this.bloodgroup = bloodgroup;
        this.email = email;
        this.id = id;
        this.idnumber = idnumber;
        this.phonenumber = phonenumber;
        this.profilepictureUrl = profilepictureUrl;
        this.search = search;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getProfilepictureUrl() {
        return profilepictureUrl;
    }

    public void setProfilepictureUrl(String profilepictureUrl) {
        this.profilepictureUrl = profilepictureUrl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
