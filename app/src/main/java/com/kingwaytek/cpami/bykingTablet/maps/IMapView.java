package com.kingwaytek.cpami.bykingTablet.maps;

import android.view.View.OnLongClickListener;

import com.kingwaytek.cpami.bykingTablet.app.MapActivity;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;

/**
 * Interface which defines the methods of a MapView
 */
public interface IMapView {

	/**
	 * Zoom in by one zoom level.
	 * 
	 * @return The current zoom level after zoom in.
	 */
	public int zoomIn();

	/**
	 * Zoom out by one zoom level.
	 * 
	 * @return The current zoom level after zoom out.
	 */
	public int zoomOut();

	/**
	 * Sets the zoom level of the map.
	 * 
	 * @param level
	 *            The level to set.
	 * @return The current zoom level after zoom in/out.
	 */
	public int setZoomLevel(int level);

	/**
	 * Returns the current zoom level.
	 * 
	 * @return The current zoom level.
	 */
	public int getZoomLevel();

	/**
	 * Returns the maximum zoom level.
	 * 
	 * @return The maximum zoom level.
	 */
	public int getMaxZoomLevel();

	/**
	 * Returns the center point of the map view.
	 * 
	 * @return The center point.
	 */
	public GeoPoint getCenter();

	/**
	 * Set the map view to the given center.
	 * 
	 * @param point
	 *            The center point.
	 */
	public void setCenter(GeoPoint point);

	/**
	 * Set the bounds of the map. The map will fix zoom level.
	 * 
	 * @param lon1
	 *            The first longitude.
	 * @param lon2
	 *            The second longitude.
	 * @param lat1
	 *            The first latitude.
	 * @param lat2
	 *            The second latitude.
	 */
	public void setBounds(double lon1, double lon2, double lat1, double lat2);

	/**
	 * Returns the current bounds of the map.
	 * 
	 * @return The west-south point at index 0 and the east-north point at index
	 *         0.
	 */
	public GeoPoint[] getBounds();

	/**
	 * Returns user location on the map.
	 * 
	 * @return The user location.
	 */
	public GeoPoint getUserLocation();

	/**
	 * Set user location on the map.
	 * 
	 * @param point
	 *            The user location.
	 */
	public void setUserLocation(GeoPoint point);

	/**
	 * Set the visibility of user location on the map.
	 * 
	 * @param visible
	 *            Set to to show or false to hide.
	 */
	public void setUserLocationVisibility(boolean visible);

	/**
	 * Set the visibility of routing path.
	 * 
	 * @param visible
	 *            Set to to show or false to hide.
	 */
	public void setRoutingPathVisible(boolean visible);

	/**
	 * Set the visibility of a node in the routing path.
	 * 
	 * @param nodeType
	 *            The type of the node.
	 * @param visible
	 *            Set to to show or false to hide.
	 */
	public void setRoutingNodeVisible(int nodeType, boolean visible);

	/**
	 * Returns the view type of the map view.
	 * 
	 * @return The current view type.
	 */
	public int getViewType();

	/**
	 * Set the view type of the map view. (Like satellite view or street view)
	 * 
	 * @param type
	 *            The view type.
	 */
	public void setViewType(int type);

	/**
	 * Returns the control mode of the map view.
	 * 
	 * @return The current control mode.
	 */
	public int getControlMode();

	/**
	 * Set the control mode of the map view. (Like navigation mode or emulator
	 * mode)
	 * 
	 * @param mode
	 *            The control mode.
	 */
	public void setControlMode(int mode);

	/**
	 * Returns the width of the map.
	 * 
	 * @return The width.
	 */
	public int getMapWidth();

	/**
	 * Returns the height of the map.
	 * 
	 * @return The height.
	 */
	public int getMapHeight();

	/**
	 * Set the size of map.
	 * 
	 * @param width
	 *            The width.
	 * @param height
	 *            The height.
	 */
	public void setMapSize(int width, int height);

	public void setSelectionPoint();

	public double[] getMapXY();

	public void setChoosePointMode(boolean mode);
	
	public void setMapActivity(MapActivity mapactivity ) ;

	public void setLongClickable(boolean b);

	public void setLongClickListener(OnLongClickListener onLongClickListener);

	
}
