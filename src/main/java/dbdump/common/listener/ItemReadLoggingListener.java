package dbdump.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class ItemReadLoggingListener implements ItemReadListener<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ItemReadLoggingListener.class);

    @Override
    public void beforeRead() {
        logger.info("item read started.");
    }

    @Override
    public void afterRead(Object item) {
        logger.info("item read finished.[Item:{}]");
    }

    @Override
    public void onReadError(Exception ex) {
    }
}