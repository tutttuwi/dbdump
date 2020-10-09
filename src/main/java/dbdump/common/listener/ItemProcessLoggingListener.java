package dbdump.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.stereotype.Component;

@Component
public class ItemProcessLoggingListener implements ItemProcessListener {

    private static final Logger logger = LoggerFactory.getLogger(ItemProcessLoggingListener.class);

    @Override
    public void beforeProcess(Object item) {
        logger.info("item process started.");
    }

    @Override
    public void afterProcess(Object item, Object result) {
        logger.info("item process finished.[Item:{}]");
    }

    @Override
    public void onProcessError(Object item, Exception e) {
    }
}