package cn.weli.svideo.baselib.component.jsbridge;

/**
 * Description.
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @since [1.0.0]
 */
public interface WebViewJavascriptBridge {
	 void send(String data);
	 void send(String data, CallBackFunction responseCallback);
}
