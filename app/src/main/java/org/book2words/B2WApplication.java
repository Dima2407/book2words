package org.book2words;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import org.book2words.dao.DaoMaster;
import org.book2words.dao.DaoSession;
import org.data.DaoHolder;
import org.data.DataContext;
import org.jetbrains.annotations.NotNull;


public class B2WApplication extends Application implements DaoHolder {
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        ImageLoader.getInstance().init(config.build());

        DataContext.Companion.setup(this);
    }

    @Override
    @NotNull
    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void setDaoSession(@NotNull DaoSession newSession) {
        daoSession = newSession;
    }
}
