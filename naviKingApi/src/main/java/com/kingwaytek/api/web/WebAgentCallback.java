package com.kingwaytek.api.web;


public interface WebAgentCallback<E> {
	
	/**
	 * 每個app call的url不同，所以將實作拉到app端
	 * @return server回傳的result
	 */
	public E getWebAgentResult();
}
