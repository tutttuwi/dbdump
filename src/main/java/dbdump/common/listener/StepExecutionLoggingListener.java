package dbdump.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class StepExecutionLoggingListener implements StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(StepExecutionLoggingListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("step started. [StepName:{}]", stepExecution.getStepName());

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("step finished.[StepName:{}][ExitStatus:{}]", stepExecution.getStepName(),
                stepExecution.getExitStatus().getExitCode());
        return null;
    }
}