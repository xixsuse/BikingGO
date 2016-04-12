package com.kingwaytek.cpami.bykingTablet.data;

/**
 * Class for category of POIs.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class POICategory implements IPOICategory {

    /** ID of the category */
    protected String id;

    /** Name of the category */
    protected String name;

    /**
     * Create new instance of POICategory.
     * 
     * @param id
     *            ID of the category
     * @param name
     *            Name of the category
     */
    public POICategory(String id, String name) {
	this.id = id;
	this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
	return id;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
	return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	return "POICategory [id=" + id + ", name=" + name + "]";
    }
}
