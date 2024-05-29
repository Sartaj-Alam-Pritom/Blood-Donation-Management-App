package com.example.blooddonationapp.ui;


public class ReadWriteUserDetails {
    public String name,phone,email,password,date_of_Birth,address,blood_Group,gender,image;
    public boolean access_granted,dataLocked;
    public long access_until;


    public ReadWriteUserDetails()
    {}

        public String getName () {
            return name;
        }

        //public void setName(String name) {this.name = name;}

        public String getPhone () {
            return phone;
        }

        //public void setPhone(String phone) {this.phone = phone;}

        public String getEmail () {
            return email;
        }

        //public void setEmail(String email) {this.email = email;}

        public String getPassword () {
            return password;
        }

        //public void setPassword(String password) {this.password = password;}

        public String getDate_of_Birth () {
            return date_of_Birth;
        }

        //public void setDate_of_Birth(String date_of_Birth) {this.date_of_Birth = date_of_Birth;}

        public boolean isAccess_granted () {
            return access_granted;
        }

        public boolean isDataLocked () {return dataLocked;}

    public Long getaccess_until() {
        return access_until;
    }

    public void setaccess_until(Long access_until) {
        // Check if the access_until parameter is null
        if (access_until != null) {
            this.access_until = access_until.longValue();
        } else {
            // Handle the case where access_until is null, perhaps by setting a default value
            this.access_until = 0L; // You can use any default value you prefer
        }
    }

    public String getImage() {
        return image;
    }

        public String getAddress () {
            return address;
        }

        //public void setAddress(String address) {this.address = address;}

        public String getBlood_Group () {
            return blood_Group;
        }

        //public void setBlood_Group(String blood_Group) {this.blood_Group = blood_Group;}

        public String getGender () {
            return gender;
        }

   // public void setGender(String gender) {this.gender = gender;}

    public ReadWriteUserDetails(String name, String email, String phone, String blood, String password, String gender, String DoB, String address ,boolean access_granted,long access_until,boolean dataLocked,String image)
    {
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.blood_Group=blood;
        this.password=password;
        this.gender=gender;
        this.date_of_Birth=DoB;
        this.address=address;
        this.access_granted=access_granted;
        this.access_until=access_until;
        this.dataLocked=dataLocked;
        this.image=image;
    }

}
