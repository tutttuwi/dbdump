package dbdump.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
public class ChunkLoggingListener implements ChunkListener {

    private static final Logger logger = LoggerFactory.getLogger(ChunkLoggingListener.class);

    @Override
    public void beforeChunk(ChunkContext context) {
        logger.info("chunk started. [StepName:{}]", context.getStepContext().getStepName());

    }

    @Override
    public void afterChunk(ChunkContext context) {
        logger.info("chunk finished.[StepName:{}][ExitStatus:{}]", context.getStepContext().getStepName(),
                context.getStepContext().getStepExecution().getExitStatus());
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        logger.error("chunk failed. [StepName:{}][ExitStatus:{}]", context.getStepContext().getStepName(),
                context.getStepContext().getStepExecution().getExitStatus());
    }
}