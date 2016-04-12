package com.kingwaytek.cpami.bykingTablet.data;

/**
 * Interface for category of POIs.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public interface IPOICategory {
    
    /**
     * Returns id of class
     * 
     * @return the id
     */
    public String getId();

    /**
     * Set id of class
     * 
     * @param id
     *            the id to set
     */
    public void setId(String id);

    /**
     * Returns name of class
     * 
     * @return the name
     */
    public String getName();

    /**
     * Set name of class
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name);
}
