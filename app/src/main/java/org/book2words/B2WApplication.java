package org.book2words;

import android.app.Application;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import org.book2words.database.DaoSession;
import org.book2words.data.*;
import org.jetbrains.annotations.NotNull;


public class B2WApplication extends Application implements DaoHolder, PreferenceHolder {
    private DaoSession daoSession;
    private Configs configs;
    private CacheDictionary dictionary;

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
        configs = ConfigsContext.Companion.setup(this);
        dictionary = DictionaryContext.Companion.setup(this);
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

    @NotNull
    @Override
    public Configs getConfigs() {
        return configs;
    }

    @NotNull
    @Override
    public CacheDictionary getDictionary() {
        return dictionary;
    }
}
