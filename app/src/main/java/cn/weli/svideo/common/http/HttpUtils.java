package cn.weli.svideo.common.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import cn.etouch.logger.Logger;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-27
 * @see [class/method]
 * @since [1.0.0]
 */
public class HttpUtils {

    /** 将传递进来的参数拼接成 url */
    public static String createUrlFromParams(String url, Map<String, List<Object>> params) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            for (Map.Entry<String, List<Object>> urlParams : params.entrySet()) {
                List<Object> urlValues = urlParams.getValue();
                for (Object value : urlValues) {
                    //对参数进行 utf-8 编码,防止头信息传中文
                    String urlValue = URLEncoder.encode(String.valueOf(value), "UTF-8");
                    sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            Logger.e(e.getMessage());
        }
        return url;
    }

}
