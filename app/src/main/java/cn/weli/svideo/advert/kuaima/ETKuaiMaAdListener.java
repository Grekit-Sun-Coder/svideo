package cn.weli.svideo.advert.kuaima;

import java.util.List;

/**
 * Created by sulei on 2016/10/25.
 */
public interface ETKuaiMaAdListener {
    void onADLoaded(List<ETKuaiMaAdData> datas);

    void onNoAD();
}
