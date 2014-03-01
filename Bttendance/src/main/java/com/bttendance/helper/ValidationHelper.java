package com.bttendance.helper;

/**
 * Created by TheFinestArtist on 2014. 3. 1..
 */
public class ValidationHelper {

    public static boolean phoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10,11}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3,4}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3,4}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3,4}-\\d{4}")) return true;
        //return false if nothing matches the input
        else return false;
    }
}
