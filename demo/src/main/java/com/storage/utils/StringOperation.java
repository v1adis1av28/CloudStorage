package com.storage.utils;

public class StringOperation {
    //user-20-files/test/some/
    //Method get folder name -> trim -> return folder path for create new folder name 
    public static String trimFolderPath(String str)
    {
        if(!str.endsWith("/"))
        {
            return "your path doesn't end with /";
        }
        return str.substring(0,str.lastIndexOf("/", str.lastIndexOf("/")-1)+1);
    }



    public static void main(String[] args)
    {
        System.out.println(trimFolderPath("//user-20-files/test/"));
    }
}
