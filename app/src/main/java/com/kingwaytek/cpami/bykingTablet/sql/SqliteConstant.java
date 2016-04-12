package com.kingwaytek.cpami.bykingTablet.sql;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class SqliteConstant {

    public enum TableName {
        Favorite("favorites"), History("history"), Track("track"), TrackPoints(
                "track_points"), POI("poi"), Spoi("spoi"), PoiKind("poi_kind");

        private final String name;

        TableName(String value) {
            name = value;
        }

        public String getName() {
            return name;
        }
    }

    public enum CursorColumn {
        ID("_id"), EMPTY;

        private final String name;

        CursorColumn() {
            this.name = "";
        }

        CursorColumn(String value) {
            name = value;
        }

        public String get() {
            return name;
        }
    }

    public enum FavoriteColumn {
        ID(0, "f_id"), NAME(1, "f_name"), TYPE(2, "f_type"), ITEM(3, "f_item"), DATE(
                4, "f_date");

        private final int index;
        private final String name;

        FavoriteColumn(int value, String name) {
            index = value;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }

    public enum HistoryColumn {
        ID(0, "h_id"), NAME(1, "h_name"), TYPE(2, "h_type"), ITEM(3, "h_item"), DATE(
                4, "h_date");

        private final int index;
        private final String name;

        HistoryColumn(int value, String name) {
            index = value;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }

    public enum TrackColumn {
        ID(0, "track_id"), NAME(1, "track_name"), DIFFICULTY(2,
                "track_difficulty"), DESCRIPTION(3, "track_desc"), START(4,
                "track_start"), END(5, "track_end"), CREATE(6, "track_create"), RECORDING(
                7, "track_recording");

        private final int index;
        private final String name;

        TrackColumn(int value, String name) {
            index = value;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        };
    }

    public enum TrackPointsColumn {
        ID(0, "tpt_track_id"), LONGITUDE(1, "tpt_longitude"), LATITUDE(2,
                "tpt_latitude"), ALTITUDE(3, "tpt_altitude"), DATE(4,
                "tpt_date"), TYPE(5, "tpt_type");

        private final int index;
        private final String name;

        TrackPointsColumn(int value, String name) {
            index = value;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        };
    }

    public enum POIColumn {
        ID(0, "p_id"), NAME(1, "p_name"), SHORT_NAME(2, "p_shortname"), ENGLISH_NAME(
                3, "p_engname"), SUB_BRANCH(4, "p_subbranch"), ADDRESS(5,
                "p_address"), TELPHONE(6, "p_tel"), TOWN_CODE(7, "p_towncode"), CATEGORY(
                8, "p_kind"), FAX(9, "p_fax"), ZIP_CODE(10, "p_zipcode"), WEB_SITE(
                11, "p_website"), BUSINESE_HOUR(12, "p_time"), CREDIT_CARD(13,
                "p_card"), CONTACT(14, "p_contact"), BUSINESE_MODEL(15,
                "p_bmodel"), ANNOTATION(16, "p_annotation"), CROAD(17,
                "p_croad"), ALONG(18, "p_along"), TMX(19, "p_colx"), TMY(20,
                "p_coly"), LATITUDE(22, "p_lat"), LONGITUDE(21, "p_lon");

        private final int index;
        private final String name;

        POIColumn(int value, String name) {
            index = value;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        };
    }

    public enum POIKindColumn {
        ID(0, "kind_id"), NAME(1, "kind_name");

        private final int index;
        private final String name;

        POIKindColumn(int value, String name) {
            index = value;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        };
    }

    public enum SpoiColumn {
        /*PHOTO_ONE,TWO,THERE因資料庫欄位移除,key改為負值不用*/
        ID(0, "s_id"), DESCRIPTION(1, "s_desp"), PHOTO_ONE(2, "s_photo1"), PHOTO_TWO(
                3, "s_photo2"), PHOTO_THREE(4, "s_photo3"), POI_ID(5, "poi_id"), THEME(
                6, "s_theme");

        private final int index;
        private final String name;

        SpoiColumn(int value, String name) {
            index = value;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        };
    }

    public enum ContentType {
        POI(1, "重要地標POI"), TRACK(2, "軌跡"), ADDRESS(3, "門牌地址");

        private static final Map<Integer, ContentType> typeMap = new HashMap<Integer, ContentType>();
        private final int value;
        private final String name;

        static {
            for (ContentType ct : EnumSet.allOf(ContentType.class)) {
                typeMap.put(ct.getValue(), ct);
            }
        }

        ContentType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static ContentType get(int value) {
            return typeMap.get(value);
        }
    }

    public enum POICategory {
        POI_01("POI_01x"), POI_02("POI_02x"), POI_03("POI_03x"), POI_04(
                "POI_04x"), POI_05("POI_05x"), POI_06("POI_06x"), POI_07(
                "POI_07x"), POI_08("POI_08x"), POI_09("POI_09x"), POI_10(
                "POI_10x"), POI_11("POI_11x"), POI_12("POI_12x"), POI_13(
                "POI_13x"), POI_14("POI_14x");

        private static final Map<String, POICategory> catMap = new HashMap<String, POICategory>();
        private final String name;

        static {
            for (POICategory ct : EnumSet.allOf(POICategory.class)) {
                catMap.put(ct.getName(), ct);
            }
        }

        POICategory(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static POICategory get(String value) {
            return catMap.get(value);
        }
    }

    public static final DateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
}