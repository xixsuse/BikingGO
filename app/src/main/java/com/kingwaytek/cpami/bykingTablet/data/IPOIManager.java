package com.kingwaytek.cpami.bykingTablet.data;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Interface for accessing POI data.
 */
public interface IPOIManager {

    /**
     * Returns all classes for POI.
     * 
     * @param superId
     *            ID of super category.
     * @return categories (or sub categories) for POI.
     * @throws IOException
     *             occurs when reading file
     */
    public ArrayList<IPOICategory> getPOICategories(String superId)
	    throws IOException;

    /**
     * Get the POI by given ID.
     * 
     * @param id
     *            the ID of POI
     * @return instance of IPOI or null if the ID does not exist.
     */
    public IPOI getPOI(int id);

    /**
     * Get attributes of POI by given instance of IPOI with valid ID.
     * 
     * @param poi
     *            the instance of IPOI with valid ID and its attributes will be
     *            set.
     * @return the same with given instance of IPOI.
     */
    public IPOI getPOI(IPOI poi);

    /**
     * Get POIs according to the given conditions.
     * 
     * @param categories
     *            ID of categories, set null for all categories
     * @param lon
     *            the longitude of central point for the searching range
     * @param lat
     *            the latitude of central point for the searching range
     * @param distance
     *            the distance limitation from the central point in meter, if
     *            the value is large than 20KM then the engine will use 20KM as
     *            the limitation
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @return POIs with ID, name, and distance or null if no data found.
     */
    public IPOI[] getPOIBasic(
	    String[] categories,
	    double lon,
	    double lat,
	    int distance,
	    String keyword,
	    int num);

    /**
     * Get POIs according to the given conditions in a city.
     * 
     * @param cityId
     *            ID of city
     * @param categories
     *            ID of categories, set null for all categories
     * @param lon
     *            the longitude of central point for the searching range
     * @param lat
     *            the latitude of central point for the searching range
     * @param distance
     *            the distance limitation from the central point in meter, if
     *            the value is large than 20KM then the engine will use 20KM as
     *            the limitation
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @return POIs with ID, name, and distance or null if no data found.
     */
    public IPOI[] getPOIBasicInCity(
	    int cityId,
	    String[] categories,
	    double lon,
	    double lat,
	    int distance,
	    String keyword,
	    int num);

    /**
     * Get POIs according to the given conditions in a town.
     * 
     * @param townId
     *            ID of town
     * @param categories
     *            ID of categories, set null for all categories
     * @param lon
     *            the longitude of central point for the searching range
     * @param lat
     *            the latitude of central point for the searching range
     * @param distance
     *            the distance limitation from the central point in meter, if
     *            the value is large than 20KM then the engine will use 20KM as
     *            the limitation
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @return POIs with ID, name, and distance or null if no data found.
     */
    public IPOI[] getPOIBasicInTown(
	    int townId,
	    String[] categories,
	    double lon,
	    double lat,
	    int distance,
	    String keyword,
	    int num);

    /**
     * Get POIs with attributes according to the given conditions.
     * 
     * @param categories
     *            ID of categories, set null for all categories
     * @param lon
     *            the longitude of central point for the searching range
     * @param lat
     *            the latitude of central point for the searching range
     * @param distance
     *            the distance limitation from the central point in meter, if
     *            the value is large than 20KM then the engine will use 20KM as
     *            the limitation
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @return null if no data found or POIs with longitude, latitude, ID of
     *         category, name, telephone number, mobile phone number, address,
     *         note, city name, town name, and distance.
     */
    public IPOI[] getPOIAttributes(
	    String[] categories,
	    double lon,
	    double lat,
	    int distance,
	    String keyword,
	    int num);

    /**
     * Get POIs with attributes according to the given conditions in a city.
     * 
     * @param cityId
     *            ID of city
     * @param categories
     *            ID of categories, set null for all categories
     * @param lon
     *            the longitude of central point for the searching range
     * @param lat
     *            the latitude of central point for the searching range
     * @param distance
     *            the distance limitation from the central point in meter, if
     *            the value is large than 20KM then the engine will use 20KM as
     *            the limitation
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @return null if no data found or POIs with longitude, latitude, ID of
     *         category, name, telephone number, mobile phone number, address,
     *         note, city name, town name, and distance.
     */
    public IPOI[] getPOIAttributesInCity(
	    int cityId,
	    String[] categories,
	    double lon,
	    double lat,
	    int distance,
	    String keyword,
	    int num);

    /**
     * Get POIs with attributes according to the given conditions.
     * 
     * @param townId
     *            ID of town
     * @param categories
     *            ID of categories, set null for all categories
     * @param lon
     *            the longitude of central point for the searching range
     * @param lat
     *            the latitude of central point for the searching range
     * @param distance
     *            the distance limitation from the central point in meter, if
     *            the value is large than 20KM then the engine will use 20KM as
     *            the limitation
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @return null if no data found or POIs with longitude, latitude, ID of
     *         category, name, telephone number, mobile phone number, address,
     *         note, city name, town name, and distance.
     */
    public IPOI[] getPOIAttributesInTown(
	    int townId,
	    String[] categories,
	    double lon,
	    double lat,
	    int distance,
	    String keyword,
	    int num);

    /**
     * Get sorted POIs with attributes according to the given conditions.
     * 
     * @param categories
     *            ID of categories, set null for all categories
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @param sort
     *            method for sorting, values could be SORT_BY_CITY_TOWN,
     *            SORT_BY_POI_NAME, SORT_BY_DISTANCE, SORT_BY_SIMILARITY, or
     *            NO_SORTING
     * @return null if no data found or POIs with longitude, latitude, ID of
     *         category, name, telephone number, mobile phone number, address,
     *         note, city name, town name, and distance.
     */
    public IPOI[] getSortedPOIAttributes(
	    String[] categories,
	    String keyword,
	    int num,
	    int sort);

    /**
     * Get sorted POIs with attributes according to the given conditions in a
     * city.
     * 
     * @param cityId
     *            ID of city
     * @param categories
     *            ID of categories, set null for all categories
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @param sort
     *            method for sorting, values could be SORT_BY_CITY_TOWN,
     *            SORT_BY_POI_NAME, SORT_BY_DISTANCE, SORT_BY_SIMILARITY, or
     *            NO_SORTING
     * @return null if no data found or POIs with longitude, latitude, ID of
     *         category, name, telephone number, mobile phone number, address,
     *         note, city name, town name, and distance.
     */
    public IPOI[] getSortedPOIAttributesInCity(
	    int cityId,
	    String[] categories,
	    String keyword,
	    int num,
	    int sort);

    /**
     * Get sorted POIs with attributes according to the given conditions in a
     * town.
     * 
     * @param townId
     *            ID of town
     * @param categories
     *            ID of categories, set null for all categories
     * @param keyword
     *            for searching POIs which part of name matched the keyword
     *            (like SQL statement '%keyword%'), set null or empty string for
     *            all names
     * @param num
     *            the number of POIs to query
     * @param sort
     *            method for sorting, values could be SORT_BY_CITY_TOWN,
     *            SORT_BY_POI_NAME, SORT_BY_DISTANCE, SORT_BY_SIMILARITY, or
     *            NO_SORTING
     * @return null if no data found or POIs with longitude, latitude, ID of
     *         category, name, telephone number, mobile phone number, address,
     *         note, city name, town name, and distance.
     */
    public IPOI[] getSortedPOIAttributesInTown(
	    int townId,
	    String[] categories,
	    String keyword,
	    int num,
	    int sort);

    /**
     * Get city and town where the given coordinate located.
     * 
     * @param lon
     *            longitude
     * @param lat
     *            latitude
     * @return null if no data found or an array that puts instance of ICity or
     *         null if city not found at index 0, and puts instance of ITown or
     *         null if town not found at index 1.
     */
    public IGeoData[] getDistrict(double lon, double lat);

    /**
     * Get all cities.
     * 
     * @return instances of cities that contains longitude, latitude, ID and
     *         name or null if no data found.
     */
    public ICity[] getCities();

    /**
     * Get all towns in a city.
     * 
     * @param cityId
     *            ID of city
     * @return instances of cities that contains longitude, latitude, ID and
     *         name or null if no data found.
     */
    public ITown[] getTowns(int cityId);

    /**
     * Get list of roads that matches the given address in a city.
     * 
     * @param cityId
     *            ID of city.
     * @param addr
     *            address pattern.
     * @param num
     *            maximum number of roads to return.
     * @return roads that matches the given address in a city or null if no data
     *         found.
     */
    public IRoad[] findRoadsInCity(int cityId, String addr, int num);

    /**
     * Get list of roads that matches the given address in a town.
     * 
     * @param townId
     *            ID of town.
     * @param addr
     *            address pattern.
     * @param num
     *            maximum number of roads to return.
     * @return roads that matches the given address in a town or null if no data
     *         found.
     */
    public IRoad[] findRoadsInTown(int townId, String addr, int num);

    /**
     * Get list of roads intersect with the given road. Use
     * 
     * @param roadName
     *            name of road
     * @return roads intersect with the given road or null if no data found.
     */
    public IRoad[] getIntersectedRoads(String roadName);

    /**
     * Get list of roads intersect with the given road in a city.
     * 
     * @param cityId
     *            ID of city
     * @param roadName
     *            name of road
     * @return roads intersect with the given road or null if no data found.
     */
    public IRoad[] getIntersectedRoadsInCity(int cityId, String roadName);

    /**
     * Get list of roads intersect with the given road in a town.
     * 
     * @param townId
     *            ID of town
     * @param roadName
     *            name of road
     * @return roads intersect with the given road or null if no data found.
     */
    public IRoad[] getIntersectedRoadsInTown(int townId, String roadName);

    /**
     * Get location of an address.
     * 
     * @param addr
     *            the address to find location.
     * @return instance of GeoData contains location of the address, or null if
     *         the location could not be found.
     */
    public IGeoData getAddressLocation(String addr);

    /**
     * Get location of an address by given city, road and address.
     * 
     * @param cityId
     *            ID of city.
     * @param roadName
     *            road name.
     * @param addr
     *            the address to be add to the end of road name that maybe
     *            contains section, lane, alley, etc.
     * @return instance of GeoData contains location of the address, or null if
     *         the location could not be found.
     */
    public IGeoData getAddressLocationInCity(int cityId, String roadName, String addr);

    /**
     * Get location of an address by given ID of town, road and address.
     * 
     * @param townId
     *            ID of town.
     * @param roadName
     *            road name.
     * @param addr
     *            the address to be add to the end of road name that maybe
     *            contains section, lane, alley, etc.
     * @return instance of GeoData contains location of the address, or null if
     *         the location could not be found.
     */
    public IGeoData getAddressLocationInTown(int townId, String roadName, String addr);

    /**
     * Get central location of a city.
     * 
     * @param id
     *            ID of city.
     * @return central point of the city or null if not found.
     */
    public GeoPoint getCenterOfCity(int id);

    /**
     * Get central location of a town.
     * 
     * @param id
     *            ID of town.
     * @return central point of the town or null if not found.
     */
    public GeoPoint getCenterOfTown(int id);
}
