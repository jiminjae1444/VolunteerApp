package com.example.myapplication_login_test;

public class MyData {
        private int ImageId;
        private String text;

        MyData(int ImageID, String text){
            this.ImageId= ImageID;
            this.text=text;
        }
        int getImageId() {
            return ImageId;
        }

        String getText() {
            return text;
        }
}

