package com.kingwaytek.anchorpoint;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.sonavtek.sonav.PPDATA;
import com.sonavtek.sonav.XLIST;
import com.sonavtek.sonav.sonav;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.data.ICity;
import com.kingwaytek.cpami.bykingTablet.data.IGeoData;
import com.kingwaytek.cpami.bykingTablet.data.IPOI;
import com.kingwaytek.cpami.bykingTablet.data.IPOICategory;
import com.kingwaytek.cpami.bykingTablet.data.IPOIManager;
import com.kingwaytek.cpami.bykingTablet.data.IRoad;
import com.kingwaytek.cpami.bykingTablet.data.ITown;
import com.kingwaytek.cpami.bykingTablet.data.POICategory;

/**
 * Wrapper of engine for querying POI information.
 * 
 * @author Harvey Cheng(harvey@kingwaytek.com)
 */
public class POIEngine extends EngineWrapper implements IPOIManager {

	/**
	 * Create new instance of POIWrapper.
	 */
	public POIEngine() {
		super();
	}

	/**
	 * Create new instance of POIWrapper.
	 * 
	 * @param engine
	 *            instance of engine.
	 */
	public POIEngine(sonav engine) {
		super(engine);
	}

	/**
	 * {@inheritDoc}
	 */
	public ArrayList<IPOICategory> getPOICategories(String superId)
			throws IOException {
		// get name of file to read content by super category
		String fileName = null;
		int level = superId == null ? 0 : superId.length() / 2;

		switch (level) {
		case 0:
			fileName = "MainCategory";
			break;
		case 1:
			fileName = "Category";
			break;
		case 2:
			fileName = "SubCategory";
			break;
		default:
			return null;
		}

		// create list to return
		ArrayList<IPOICategory> list = new ArrayList<IPOICategory>();

		// parse content from file, the charset of file is BIG5
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(engine.getDataPath() + fileName), "BIG5"));

		try {
			String line;
			String[] values;

			while (br.ready()) {
				line = br.readLine();

				if (line.startsWith(superId)) {
					values = line.split(" ");

					if (values.length > 1) {
						list.add(new POICategory(values[0], values[1]));
					}
				}
			}
		} finally {
			br.close();
		}

		return list;
	}

	/**
	 * <span class="nativeName">native method: PPDATA getptproperty(PPDATA data,
	 * int id)</span><br/>
	 * <span class="important">This method is not implemented well. The name and
	 * distance won't be set in returned instance.</span> {@inheritDoc}
	 */
	public IPOI getPOI(int id) {
		PPDATA poi = engine.getptproperty(new PPDATA(), id);

		return poi.getId() > 0 ? poi : null;
	}

	/**
	 * <span class="nativeName">native method: PPDATA getptproperty(PPDATA data,
	 * int id)</span><br/> {@inheritDoc}
	 */
	public IPOI getPOI(IPOI poi) {
		return engine.getptproperty((PPDATA) poi, poi.getId());
	}

	/**
	 * <span class="nativeName">native method: NEARPOI[] xynearpoi( int
	 * cityOrTown, String keyword, String category, double lon, double lat,
	 * double distance, int num)</span><br/> {@inheritDoc}
	 */
	public IPOI[] getPOIBasic(String[] categories, double lon, double lat,
			int distance, String keyword, int num) {
		IPOI[] pois = engine.xynearpoi(sonav.ALL_DISTRICTS,
				(keyword == null ? "" : keyword),
				getCategoryCondition(categories), lon, lat, distance, num);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * <span class="nativeName">native method: NEARPOI[] xynearpoi( int
	 * cityOrTown, String keyword, String category, double lon, double lat,
	 * double distance, int num)</span><br/> {@inheritDoc}
	 */
	public IPOI[] getPOIBasicInCity(int cityId, String[] categories,
			double lon, double lat, int distance, String keyword, int num) {
		IPOI[] pois = engine.xynearpoi(cityId,
				(keyword == null ? "" : keyword),
				getCategoryCondition(categories), lon, lat, distance, num);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * <span class="nativeName">native method: NEARPOI[] xynearpoi( int
	 * cityOrTown, String keyword, String category, double lon, double lat,
	 * double distance, int num)</span><br/> {@inheritDoc}
	 */
	public IPOI[] getPOIBasicInTown(int townId, String[] categories,
			double lon, double lat, int distance, String keyword, int num) {
		IPOI[] pois = engine.xynearpoi(townId,
				(keyword == null ? "" : keyword),
				getCategoryCondition(categories), lon, lat, distance, num);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * <span class="nativeName">native method: XLIST4 *listnearpoi(int
	 * ctcode,char *kw,char *poiclass,double x,double y,double len,int count,int
	 * *iCount)</span><br/>
	 * <span class="important"> 1. The value of ID, city, town, UB code, and
	 * distance in returned instaces won't be set. 2. The returned data has been
	 * sorted.</span><br/> {@inheritDoc}
	 */
	public IPOI[] getPOIAttributes(String[] categories, double lon, double lat,
			int distance, String keyword, int num) {
		IPOI[] pois = engine.listnearpoi(sonav.ALL_DISTRICTS,
				(keyword == null ? "" : keyword),
				getCategoryCondition(categories), lon, lat, distance, num);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * <span class="nativeName">native method: XLIST4 *listnearpoi(int
	 * ctcode,char *kw,char *poiclass,double x,double y,double len,int count,int
	 * *iCount)</span><br/>
	 * <span class="important">1. The value of ID, city, town, UB code, and
	 * distance in returned instaces won't be set. 2. The returned data has been
	 * sorted.</span><br/> {@inheritDoc}
	 */
	public IPOI[] getPOIAttributesInCity(int cityId, String[] categories,
			double lon, double lat, int distance, String keyword, int num) {
		IPOI[] pois = engine.listnearpoi(cityId, (keyword == null ? ""
				: keyword), getCategoryCondition(categories), lon, lat,
				distance, num);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * <span class="nativeName">native method: XLIST4 *listnearpoi(int
	 * ctcode,char *kw,char *poiclass,double x,double y,double len,int count,int
	 * *iCount)</span><br/>
	 * <span class="important">1. The value of ID, city, town, UB code, and
	 * distance in returned instaces won't be set. 2. The returned data has been
	 * sorted.</span><br/> {@inheritDoc}
	 */
	public IPOI[] getPOIAttributesInTown(int townId, String[] categories,
			double lon, double lat, int distance, String keyword, int num) {
		IPOI[] pois = engine.listnearpoi(townId, (keyword == null ? ""
				: keyword), getCategoryCondition(categories), lon, lat,
				distance, num);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * <span class="nativeName">native method: XLIST4[] findlistpoi1( String
	 * keyword, int cityOrTown, String category, int num, int sort)</span><br/>
	 * <span class="important">The value of ID, mobile phone number, city, town,
	 * and distance in returned instaces won't be set.</span><br/> {@inheritDoc}
	 */
	public IPOI[] getSortedPOIAttributes(String[] categories, String keyword,
			int num, int sort) {
		IPOI[] pois = engine.findlistpoi1((keyword == null ? "" : keyword),
				sonav.ALL_DISTRICTS, getCategoryCondition(categories), num,
				sort);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * <span class="nativeName">native method: XLIST4[] findlistpoi1( String
	 * keyword, int cityOrTown, String category, int num, int sort)</span><br/>
	 * <span class="important">The value of ID, mobile phone number, city, town,
	 * and distance in returned instaces won't be set.</span><br/> {@inheritDoc}
	 */
	public IPOI[] getSortedPOIAttributesInCity(int cityId, String[] categories,
			String keyword, int num, int sort) {
		IPOI[] pois = engine.findlistpoi1((keyword == null ? "" : keyword),
				cityId, getCategoryCondition(categories), num, sort);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * <span class="nativeName">native method: XLIST4[] findlistpoi1( String
	 * keyword, int cityOrTown, String category, int num, int sort)</span><br/>
	 * <span class="important">The value of ID, mobile phone number, city, town,
	 * and distance in returned instaces won't be set.</span><br/> {@inheritDoc}
	 */
	public IPOI[] getSortedPOIAttributesInTown(int townId, String[] categories,
			String keyword, int num, int sort) {
		IPOI[] pois = engine.findlistpoi1((keyword == null ? "" : keyword),
				townId, getCategoryCondition(categories), num, sort);

		return pois == null || pois.length == 0 ? null : pois;
	}

	/**
	 * Return a string condition for querying data.
	 * 
	 * @param categories
	 *            ID of categories
	 * @return a string separated by comma or empty string if categories is null
	 */
	private String getCategoryCondition(String[] categories) {
		if (categories == null) {
			return "";
		}

		// fetch all IDs and combine to a string separated by comma
		StringBuilder sb = new StringBuilder();

		for (String id : categories) {
			sb.append(",").append(id);
		}

		return sb.substring(1).toString();
	}

	/**
	 * <span class="nativeName">native method: String[]
	 * showcitytownnameExt(double lon, double lat)</span><br/> {@inheritDoc}
	 */
	public IGeoData[] getDistrict(double lon, double lat) {
		IGeoData[] cityTown = null;
		int[] ids = engine.showcitytowncode(lon, lat);

		if (ids != null && ids[0] > 0) {
			String[] names = engine.showcitytownnameExt(lon, lat);

			cityTown = new IGeoData[2];

			cityTown[0] = new XLIST(0, 0, ids[0], names[0],
					IGeoData.NO_LOCATE_METHOD);

			if (ids[1] > 0) {
				cityTown[1] = new XLIST(0, 0, ids[1], names[1],
						IGeoData.NO_LOCATE_METHOD);
			}
		}

		return cityTown;
	}

	/**
	 * <span class="nativeName">native method: XLIST *showlistcity(int
	 * *i)</span><br/> {@inheritDoc}
	 */
	public ICity[] getCities() {
		ICity[] cities = engine.showlistcity();

		return cities.length > 0 ? cities : null;
	}

	/**
	 * <span class="nativeName">native method: XLIST *showlisttown(int *i,int
	 * citycode)</span><br/> {@inheritDoc}
	 */
	public ITown[] getTowns(int cityId) {
		ITown[] towns = engine.showlisttown(cityId);

		return towns.length > 0 ? towns : null;
	}

	/**
	 * <span class="nativeName">native method: XLIST[] findlistroad1(int
	 * cityCode, String addr, int num)</span><br/> {@inheritDoc}
	 */
	public IRoad[] findRoadsInCity(int cityId, String addr, int num) {
		IRoad[] roads = engine.findlistroad1(cityId, addr, num);

		return roads.length > 0 ? roads : null;
	}

	/**
	 * <span class="nativeName">native method: XLIST[] findlistroad1(int
	 * cityCode, String addr, int num)</span><br/> {@inheritDoc}
	 */
	public IRoad[] findRoadsInTown(int townId, String addr, int num) {
		IRoad[] roads = engine.findlistroad1(townId, addr, num);

		return roads.length > 0 ? roads : null;
	}

	/**
	 * <span class="nativeName">native method: XLIST[] showlistroad2(int
	 * cityOrTown, String roadName)</span><br/> {@inheritDoc}
	 */
	public IRoad[] getIntersectedRoads(String roadName) {
		IRoad[] roads = engine.showlistroad2(sonav.ALL_DISTRICTS, roadName);

		return roads.length > 0 ? roads : null;
	}

	/**
	 * <span class="nativeName">native method: XLIST[] showlistroad2(int
	 * cityOrTown, String roadName)</span><br/> {@inheritDoc}
	 */
	public IRoad[] getIntersectedRoadsInCity(int cityId, String roadName) {
		IRoad[] roads = engine.showlistroad2(cityId, roadName);

		return roads.length > 0 ? roads : null;
	}

	/**
	 * <span class="nativeName">native method: XLIST[] showlistroad2(int
	 * cityOrTown, String roadName)</span><br/> {@inheritDoc}
	 */
	public IRoad[] getIntersectedRoadsInTown(int townId, String roadName) {
		IRoad[] roads = engine.showlistroad2(townId, roadName);

		return roads.length > 0 ? roads : null;
	}

	/**
	 * <span class="nativeName">native method: double[] showaddrxy1(String
	 * addr)</span><br/> {@inheritDoc}
	 */
	public IGeoData getAddressLocation(String addr) {
		double[] values = engine.showaddrxy1(addr);

		if (values[0] != IGeoData.NO_LOCATE_METHOD) {
			return new XLIST(values[1], values[2], 0, null, (int) values[0]);
		} else {
			return null;
		}
	}

	/**
	 * <span class="nativeName">native method: double[] showaddrxy2(int
	 * cityOrTownId, String roadName, String addr)</span><br/> {@inheritDoc}
	 */
	public IGeoData getAddressLocationInCity(int cityId, String roadName,
			String addr) {
		double[] values = engine.showaddrxy2(cityId, roadName, addr);

		if (values[0] != IGeoData.NO_LOCATE_METHOD) {
			return new XLIST(values[1], values[2], 0, null, (int) values[0]);
		} else {
			return null;
		}
	}

	/**
	 * <span class="nativeName">native method: double[] showaddrxy2(int
	 * cityOrTownId, String roadName, String addr)</span><br/> {@inheritDoc}
	 */
	public IGeoData getAddressLocationInTown(int townId, String roadName,
			String addr) {
		double[] values = engine.showaddrxy2(townId, roadName, addr);

		if (values[0] != IGeoData.NO_LOCATE_METHOD) {
			return new XLIST(values[1], values[2], 0, null, (int) values[0]);
		} else {
			return null;
		}
	}

	/**
	 * <span class="nativeName">native method: double[] getcityxy(id)</span><br/>
	 * {@inheritDoc}
	 */
	public GeoPoint getCenterOfCity(int id) {
		double[] ll = engine.getcityxy(id);

		return ll.length != 0 ? new GeoPoint(ll[0], ll[1]) : null;
	}

	/**
	 * <span class="nativeName">native method: double[] gettownxy(id)</span><br/>
	 * {@inheritDoc}
	 */
	public GeoPoint getCenterOfTown(int id) {
		double[] ll = engine.gettownxy(id);

		return ll.length != 0 ? new GeoPoint(ll[0], ll[1]) : null;
	}

}
