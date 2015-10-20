package com.letv.recorder.data;

import java.util.ArrayList;

/**
 * Created by malin on 15-10-20.
 */
public class WatcherDataFactory {

    public static final String URL = "http://qlogo4.store.qq.com/qzone/1461826511/1461826511/100?1412661446";
    //https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png
    public static ArrayList<Watcher> createData() {
        ArrayList<Watcher> watchers = new ArrayList<Watcher>();
        Watcher watcher = new Watcher();
        for (int i = 0; i < 10; i++) {
            watcher.url = URL;
            watchers.add(watcher);
        }
        return watchers;
    }
}
