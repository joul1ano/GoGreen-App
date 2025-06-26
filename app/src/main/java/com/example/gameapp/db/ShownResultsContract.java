package com.example.gameapp.db;

import android.provider.BaseColumns;

public class ShownResultsContract {

    private ShownResultsContract(){};

    public static class ShownResultEntry implements BaseColumns{
        public static final String TABLE_USER = "User";
        public static final String COLUMN_USER_USERNAME = "UserUsername";
        public static final String COLUMN_USER_PASSWORD = "UserPassword";
        public static final String COLUMN_USER_POINTS = "UserPoints";
        public static final String COLUMN_USER_GlASS_QUANTITY = "UserGlassQuantity";
        public static final String COLUMN_USER_PAPER_QUANTITY = "UserPaperQuantity";
        public static final String COLUMN_USER_ALUMINIUM_QUANTITY = "UserAluminiumQuantity";
        public static final String COLUMN_USER_ELECTRIC_DEVICES_QUANTITY = "UserElectricDevicesQuantity";
        public static final String COLUMN_USER_BATTERIES_QUANTITY = "UserBatteriesQuantity";
        public static final String COLUMN_USER_CLOTHING_QUANTITY = "UserClothingQuantity";
        public static final String COLUMN_USER_OIL_QUANTITY = "UserOilQuantity";
        public static final String COLUMN_USER_PLASTIC_QUANTITY = "UserPlasticQuantity";

        // ADMIN
        public static final String TABLE_ADMIN = "Admin";
        public static final String COLUMN_ADMIN_USERNAME = "AdminUsername";
        public static final String COLUMN_ADMIN_PASSWORD = "AdminPassword";

        // APPLICATION_FORM
        public static final String TABLE_APPLICATION_FORM = "ApplicationForm";
        public static final String COLUMN_APPLICANT_USERNAME = "ApplicantUsername";
        public static final String COLUMN_GLASS_ITEMS = "GlassItems";
        public static final String COLUMN_PAPER_ITEMS = "PaperItems";
        public static final String COLUMN_ALUMINIUM_ITEMS = "AluminiumItems";
        public static final String COLUMN_ELECTRIC_DEVICE_ITEMS = "ElectricDeviceItems";
        public static final String COLUMN_BATTERIES = "Batteries";
        public static final String COLUMN_CLOTHES = "Clothes";
        public static final String COLUMN_USED_OIL_KG = "UsedOil";
        public static final String COLUMN_PLASTIC_ITEMS = "PlasticItems";
        public static final String COLUMN_APPLICATION_POINTS = "ApplicationPoints";
    }
}
