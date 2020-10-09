package dbdump.common.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

@Component
public class ItemWriteLoggingListener implements ItemWriteListener<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ItemWriteLoggingListener.class);

    @Override
    public void beforeWrite(List<? extends Object> items) {
        logger.info("item write started.");
    }

    @Override
    public void afterWrite(List<? extends Object> items) {
        logger.info("item write finished.[Item:{}]");
    }

    @Override
    public void onWriteError(Exception exception, List<? extends Object> items) {
    }
}