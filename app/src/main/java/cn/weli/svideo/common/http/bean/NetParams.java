package cn.weli.svideo.common.http.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**网络参数类，用于发起网络请求时想url中提交参数*/
public class NetParams {

	int paramsNumbers=0;
	private StringBuffer result=new StringBuffer();
	
	public NetParams(){
		
	}
	
	/**添加一个参数,参数无须编码*/
	public void addParam(String key, String value){
		if(paramsNumbers!=0){
			result.append("&");
		}
		try {
			result.append(key + "="+ URLEncoder.encode(value, "utf-8"));
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		paramsNumbers++;
		
	}
	public String getParamsAsString(){
		return result.toString();
	}
}
