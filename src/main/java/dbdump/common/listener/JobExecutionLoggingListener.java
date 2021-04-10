package dbdump.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobExecutionLoggingListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobExecutionLoggingListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("job started. [JobName:{}]", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("job finished.[JobName:{}][ExitStatus:{}]",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getExitStatus().getExitCode());
    }
}
