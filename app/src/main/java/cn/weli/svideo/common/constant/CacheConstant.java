package cn.weli.svideo.common.constant;

/**
 * 缓存常量管理类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see CacheConstant
 * @since [1.0.0]
 */
public interface CacheConstant {

    /**
     * 最大缓存时间5分钟
     */
    int FLAG_MAX_MEMORY_TIME = 5 * 60 * 1000;

    /**
     * 内存对象50个
     */
    int FLAG_MAX_MEMORY_COUNT = 30;

    /**
     * 磁盘文件100个
     */
    int FLAG_DISK_FILE_COUNT = 100;

    /**
     * 100M磁盘缓存大小
     */
    int FLAG_DISK_FILE_SIZE = 100 * 1024 * 2014;

    /**
     * 点赞列表
     */
    String CACHE_PRAISE_LIST = "praise_list";

    /**
     * 收益信息
     */
    String CACHE_PROFIT_INFO = "profit_info";

    /**
     * 任务列表
     */
    String CACHE_TASK_LIST = "task_list";
}
