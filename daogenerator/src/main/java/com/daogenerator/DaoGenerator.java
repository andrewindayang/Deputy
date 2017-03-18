package com.daogenerator;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
/**
 * Run this by running "gradle run" from this module's root directory (daogenerator).
 *
 * @author andrewindayang
 */
public class DaoGenerator {


    public static final int DB_VERSION = 1;

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(DB_VERSION, "deputyapp.deputyapp.dao");
        schema.enableKeepSectionsByDefault();
        addEntitiesToSchema(schema);
        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, "./app/src/main/java");
    }

    private static void addEntitiesToSchema(Schema schema) {

        //----------------------SHA-1-----------------------
        Entity AuthorisationSha1 = schema.addEntity("AuthorisationSha1");
        AuthorisationSha1.addStringProperty("sha1");

        //----------------------Business_Details-----------------------
        Entity Business = schema.addEntity("Business");
        Business.addIdProperty().primaryKey();
        Business.addStringProperty("name");
        Business.addStringProperty("logo");


        //----------------------Previous_Shift-----------------------
        Entity PrevShiftsData = schema.addEntity("PrevShiftsData");
        PrevShiftsData.addStringProperty("id");
        PrevShiftsData.addStringProperty("start");
        PrevShiftsData.addStringProperty("end");
        PrevShiftsData.addStringProperty("startLatitude");
        PrevShiftsData.addStringProperty("startLongitude");
        PrevShiftsData.addStringProperty("endLatitude");
        PrevShiftsData.addStringProperty("endLongitude");
        PrevShiftsData.addStringProperty("image");



    }
}
