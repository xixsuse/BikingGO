package com.kingwaytek.cpami.bykingTablet.data;

/**
 * Interface of POI (Point of interest).
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public interface IPOI extends IGeoData {
    
    /**
     * Get the UB code.
     * 
     * @return the UB code
     */
    public String getUB();

    /**
     * Set the UB code.
     * 
     * @param code
     *            the UB code to set
     */
    public void setUB(String code);
    
    /**
     * Get the ID of category.
     * 
     * @return the ID of category
     */
    public String getCategory();

    /**
     * Set the ID of category.
     * 
     * @param id
     *            the ID of category to set
     */
    public void setCategory(String id);

    /**
     * Get the city where the POI locate in.
     * 
     * @return the city
     */
    public ICity getCity();

    /**
     * Set the city where the POI locate in.
     * 
     * @param city
     *            the city to set
     */
    public void setCity(ICity city);
    
    /**
     * Get the town where the POI locate in.
     * 
     * @return the town
     */
    public ITown getTown();

    /**
     * Set the town where the POI locate in.
     * 
     * @param town
     *            the town to set
     */
    public void setTown(ITown town);
    
    /**
     * Get the contact telephone number.
     * 
     * @return the contact telephone number
     */
    public String getTelephoneNumber();

    /**
     * Set the contact telephone number.
     * 
     * @param number
     *            the contact telephone number
     */
    public void setTelephoneNumber(String number);
    
    /**
     * Get the contact mobile number.
     * 
     * @return the contact mobile number
     */
    public String getMobilePhoneNumber();

    /**
     * Set the contact mobile number.
     * 
     * @param number
     *            the contact mobile phone number
     */
    public void setMobilePhoneNumber(String number);
    
    /**
     * Get the FAX number.
     * 
     * @return the FAX number
     */
    public String getFAX();

    /**
     * Set the FAX number.
     * 
     * @param fax
     *            the FAX number
     */
    public void setFAX(String fax);
    
    /**
     * Get the contact address.
     * 
     * @return the contact address
     */
    public String getAddress();

    /**
     * Set the contact address.
     * 
     * @param addr
     *            the contact address
     */
    public void setAddress(String addr);
    
    /**
     * Get the note.
     * 
     * @return the note
     */
    public String getNote();

    /**
     * Set the note.
     * 
     * @param note
     *            the note
     */
    public void setNote(String note);
    
    /**
     * Get the distance from the specified location in meter.
     * 
     * @return the distance
     */
    public int getDistance();

    /**
     * Set the distance from the specified location in meter.
     * 
     * @param distance
     *            the distance
     */
    public void setDistance(int distance);
}
