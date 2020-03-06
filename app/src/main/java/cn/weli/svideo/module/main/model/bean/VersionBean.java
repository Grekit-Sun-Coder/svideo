package cn.weli.svideo.module.main.model.bean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-29
 * @see [class/method]
 * @since [1.0.0]
 */
public class VersionBean {

    public int force_flag;
    public int update_flag;
    public int version_code;
    public String version_desc;
    public String version_link;
    public String version_name;

    public boolean needUpdate() {
        return update_flag == 1;
    }

    public boolean forceUpdate() {
        return force_flag == 1;
    }
}
