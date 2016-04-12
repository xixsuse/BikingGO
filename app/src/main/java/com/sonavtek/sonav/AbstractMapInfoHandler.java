package com.sonavtek.sonav;

import java.util.Hashtable;

import android.os.Message;
import android.util.Log;

/**
 * This is the handler that process events for map.
 */
public abstract class AbstractMapInfoHandler extends EngineEventHandler {

	/** 無路徑規劃時的道路等級: 國道 */
	public static final int NCLS_NATIONAL_HIGHWAY = 14;

	/** 無路徑規劃時的道路等級: 快速道路 */
	public static final int NCLS_EXPRESSWAY = 13;

	/** 無路徑規劃時的道路等級: 交流道 */
	public static final int NCLS_INTERCHANGE = 12;

	/** 無路徑規劃時的道路等級: 省道 */
	public static final int NCLS_PROVINCIAL_HIGHWAY = 11;

	/** 無路徑規劃時的道路等級: 縣道 */
	public static final int NCLS_COUNTY_ROAD = 10;

	/** 無路徑規劃時的道路等級: 鄉道 */
	public static final int NCLS_TOWNSHIP = 9;

	/** 無路徑規劃時的道路等級: 重要與一般道路 */
	public static final int NCLS_MAJOR_OR_NORMAL = 8;

	/** 無路徑規劃時的道路等級: 計劃與慢速道路 */
	public static final int NCLS_PLAN_OR_SLOW = 7;

	/** 無路徑規劃時的道路等級: 自行車道 */
	public static final int NCLS_BICYCLE = 6;

	/** 無路徑規劃時的道路等級: 巷弄與無名道路 */
	public static final int NCLS_ALLEY_OR_UNKNOWN = 5;

	/** 有路徑規劃路網時的道路等級，參考{@link EngineEvent#AM_NOWROADADDRNO} */
	public static final Hashtable<Integer, String> ROAD_CLASS = new Hashtable<Integer, String>();

	static {
		ROAD_CLASS.put(NCLS_ALLEY_OR_UNKNOWN, "巷弄與無名道路");
		ROAD_CLASS.put(NCLS_BICYCLE, "自行車道");
		ROAD_CLASS.put(NCLS_PLAN_OR_SLOW, "計劃與慢速道路");
		ROAD_CLASS.put(NCLS_MAJOR_OR_NORMAL, "重要與一般道路");
		ROAD_CLASS.put(NCLS_TOWNSHIP, "鄉道");
		ROAD_CLASS.put(NCLS_COUNTY_ROAD, "縣道");
		ROAD_CLASS.put(NCLS_PROVINCIAL_HIGHWAY, "省道");
		ROAD_CLASS.put(NCLS_INTERCHANGE, "交流道");
		ROAD_CLASS.put(NCLS_EXPRESSWAY, "快速道路");
		ROAD_CLASS.put(NCLS_NATIONAL_HIGHWAY, "國道");
	}

	private String strAM_NOWROADNAME = "";
	private final int SPECIAL_LANE_COUNTER_MAX = 3;
	private int specialCounter = 0;
	private int specialAnnouncement = 0;
	private int TRACTION_LANE = 1;
	private int BICYCLE_LANE = 2;
	private int PEDESTRIAN_LANE = 3;
	private int OTHER_LANE = 4;
	private boolean YES = true;
	private boolean NO = false;
	private boolean onSpecialLane = false;

	@Override
	public void handleMessage(Message msg) {
		// Log.d(getClass().toString(), "handleMessage: " + msg);

		super.handleMessage(msg);

		String str = null;

		switch (msg.what) {
		case EngineEvent.AM_NOWROADNAME: // 25551
			strAM_NOWROADNAME = engine.getcallbackstr(msg.arg2);
			onCurrentRoadChanged(engine.getcallbackstr(msg.arg2), engine.getcallbackstr(msg.arg1));

			break;
		case EngineEvent.AM_NOWROADADDRNO: // 25552
			onCurrentAddressChanged(msg.arg1, engine.getcallbackstr(msg.arg2));

			break;
		case EngineEvent.AM_NOWCITYTOWN: // 25553
			String[] names = engine.getctname(msg.arg1);

			onDistrictChanged(names[0], names[1]);

			break;
		case EngineEvent.AM_NAVITIME: // 25554
			onTimeLeftChanged(msg.arg2);

			break;
		case EngineEvent.AM_NAVIDIST: // 25556
			onDistanceChanged(msg.arg2);

			break;
		case EngineEvent.AM_NEXTTURN: // 25563
			str = engine.getcallbackstr(msg.arg2);

			if (str != null && str.length() > 0) {
				onNextTurnChanged(Integer.valueOf(str.split(",")[0]));
			}

			break;
		case EngineEvent.AM_NEXTROAD: // 25564
			onNextRoadChanged(engine.getcallbackstr(msg.arg2), engine.getcallbackstr(msg.arg1));
			break;
		case EngineEvent.AM_NEXTDIST: // 25565
			if (engine.getcallbackstr(msg.arg2) != null) {//引擎會吐null...只好判斷
				onDistanceToNextRoadChanged(Integer.parseInt(engine.getcallbackstr(msg.arg2)));
			}
			break;
		case EngineEvent.AM_ROUTE: // 25569
			if (msg.arg2 == EngineEvent.ROUTING_PATH_GENERATED && PathFinder.getInstance().isPathFoundSuccessful()) {
				onRoutingPathChanged();
			}
			break;
		case EngineEvent.AM_NOWROADLAYER:// 25584
			displayGradient(msg.arg1);
			Log.i("AbstracMapInfoHandle.java", "AM_NOWROADLAYER msg.arg2=" + msg.arg2);
			Log.i("AbstracMapInfoHandle.java", "AM_NOWROADLAYER onSpecialLane=" + onSpecialLane);
			// if( msg.arg2 == 6 || msg.arg2 == 7 || onSpecialLane ){
			if (msg.arg2 == 7 || msg.arg2 == 8 || onSpecialLane) {
				engine.setvoiceself(1);
				PrepareToBroadCastSpecialLane(msg.arg2);
				engine.setvoiceself(0);
			}
			/*
			 * displayGradient(msg.arg1);
			 * Log.i("AbstractMapInfoHandler.java","msg.arg1="+msg.arg2);
			 * if(msg.arg2==6){ onRoadLayer(); }
			 */
			break;
		case EngineEvent.AM_DESTINATION:// 25579
			Log.i("AbstractMapInfoHandler.java", "25579");
			naviFinished();
			break;
		default: // unhandled messages
			break;
		}
	}

	/**
	 * Called when current road changed ({@link EngineEvent#AM_NOWROADNAME}
	 * received).
	 * 
	 * @param roadName
	 *            The name of current road.
	 * @param sign
	 *            The sign here.
	 */
	public abstract void onCurrentRoadChanged(String roadName, String sign);

	/**
	 * Called when current address changed ({@link EngineEvent#AM_NOWROADADDRNO}
	 * received).
	 * 
	 * @param clazz
	 *            道路等級.
	 * @param address
	 *            The current address.
	 */
	public abstract void onCurrentAddressChanged(int clazz, String address);

	/**
	 * Called when current address changed ({@link EngineEvent#AM_NOWROADADDRNO}
	 * received).
	 * 
	 * @param cityName
	 *            The name of city.
	 * @param townName
	 *            The name of town.
	 */
	public abstract void onDistrictChanged(String cityName, String townName);

	/**
	 * Called when the time left to destination changed(for navigation). (
	 * {@link EngineEvent#AM_NAVITIME} received).
	 * 
	 * @param minute
	 *            The time left in minute.
	 */
	public abstract void onTimeLeftChanged(int minute);

	/**
	 * Called when the distance between destination changed(for navigation). (
	 * {@link EngineEvent#AM_NAVIDIST} received).
	 * 
	 * @param meter
	 *            The distance in meter.
	 */
	public abstract void onDistanceChanged(int meter);

	/**
	 * Called when the next turn changed(for navigation). (
	 * {@link EngineEvent#AM_NEXTTURN} received).
	 * 
	 * @param turn
	 *            The next turn.
	 */
	public abstract void onNextTurnChanged(int turn);

	/**
	 * Called when next road changed ({@link EngineEvent#AM_NEXTROAD} received).
	 * 
	 * @param roadName
	 *            The name of next road.
	 * @param sign
	 *            The next sign.
	 */
	public abstract void onNextRoadChanged(String roadName, String sign);

	/**
	 * Called when distance to next road changed (
	 * {@link EngineEvent#AM_NEXTDIST} received).
	 * 
	 * @param meter
	 *            The distance in meter.
	 */
	public abstract void onDistanceToNextRoadChanged(int meter);

	/**
	 * Called when new routing path generated ( {@link EngineEvent#AM_ROUTE}
	 * received).
	 */
	public abstract void onRoutingPathChanged();

	public abstract void onRoadLayer();

	public abstract void displayGradient(double gradient);

	public abstract void naviFinished();

	private void PrepareToBroadCastSpecialLane(int msg) {

		switch (msg) {

		case 7:
			Log.i("AbstracMapInfoHandle.java", "AM_NOWROADLAYER case 6");
			if (strAM_NOWROADNAME != null && strAM_NOWROADNAME.indexOf("牽引道") != -1) {
				Log.i("AbstracMapInfoHandle.java", "AM_NOWROADLAYER TRACTION_LANE enter TRACTION_LANE");
				if (TRACTION_LANE != specialAnnouncement) {
					Log.i("AbstracMapInfoHandle.java", "AM_NOWROADLAYER TRACTION specialAnnouncement="
							+ specialAnnouncement);
					if (TRACTION_LANE != specialAnnouncement) { // tranction
																// lane is not
																// announced yet
						specialCounter += 1;
					} else {
						specialCounter = 0;
					}
					Log.i("AbstracMapInfoHandle.java", "AM_NOWROADLAYER TRACTION specialCounter=" + specialCounter);
					if (SPECIAL_LANE_COUNTER_MAX <= specialCounter) {
						// call play traction
						BroadCastSpecialLane(TRACTION_LANE);
					}

				}
			} else {
				if (BICYCLE_LANE != specialAnnouncement) { // bicycle lane is
															// not announced yet
					specialCounter += 1;
				} else {
					specialCounter = 0;
				}

				if (SPECIAL_LANE_COUNTER_MAX <= specialCounter) {
					// call play bicycle
					BroadCastSpecialLane(BICYCLE_LANE);
				}
			}

			break;
		case 8:
			Log.i("AbstracMapInfoHandle.java", "AM_NOWROADLAYER case 7");
			if (PEDESTRIAN_LANE != specialAnnouncement) { // pedestrian lane is
															// not announced yet
				specialCounter += 1;
			} else {
				specialCounter = 0;
			}

			if (SPECIAL_LANE_COUNTER_MAX <= specialCounter) {
				// call play pedestrain
				BroadCastSpecialLane(PEDESTRIAN_LANE);
			}
			break;
		default:

			Log.i("AbstracMapInfoHandle.java", "AM_NOWROADLAYER onSpecialLane =" + onSpecialLane);
			if (onSpecialLane) { // user just left a special lane
				if (SPECIAL_LANE_COUNTER_MAX <= specialCounter) {
					// call play leave
					BroadCastSpecialLane(OTHER_LANE);
				} else {
					specialCounter += 1;
				}
			}

			break;

		}
	}

	private void BroadCastSpecialLane(int msg) {
		switch (msg) {
		case 1: // case of traction lane
			specialAnnouncement = TRACTION_LANE;
			onSpecialLane = YES;
			specialCounter = 0;
			engine.addnavivoice("請注意前方為自行車牽引道請下車牽行");
			Log.i("AbstracMapInfoHandle.java", "Add navi voice of TRACTION LANE");
			break;
		case 2: // case of bicycle lane
			// announce that user is on bicycle lane
			specialAnnouncement = BICYCLE_LANE;
			onSpecialLane = YES;
			specialCounter = 0;
			engine.addnavivoice("請注意前方為自行車專用道請安心騎乘");
			Log.i("AbstracMapInfoHandle.java", "Add navi voice of BICYLE LANE");
			break;
		case 3: // case of pedestrian lane
			// annouce that user is on pedestrian lane
			specialAnnouncement = PEDESTRIAN_LANE;
			onSpecialLane = YES;
			specialCounter = 0;
			engine.addnavivoice("請注意前方的人行道可以騎乘自行車");
			Log.i("AbstracMapInfoHandle.java", "Add navi voice of PEDESTRIAN LANE");
			break;
		case 4: // case return to normal
			// announce that user has left a special lane and reset flags
			specialAnnouncement = OTHER_LANE;
			onSpecialLane = NO;
			specialCounter = 0;
			engine.addnavivoice("請注意前方為一般道路請小心騎乘");
			Log.i("AbstracMapInfoHandle.java", "Add navi voice of leave SPECIAL LANE");
			break;
		default:
			break;
		}
	}

}
