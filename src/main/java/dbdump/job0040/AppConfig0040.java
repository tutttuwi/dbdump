package dbdump.job0040;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import dbdump.common.listener.JobExecutionLoggingListener;
import dbdump.common.listener.StepExecutionLoggingListener;

@Configuration
@EnableBatchProcessing
@ComponentScans({@ComponentScan("dbdump.common"), @ComponentScan("dbdump.job0040")})
public class AppConfig0040 {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private CreateInsertSqlTasklet createInsertSqlTasklet;
    @Autowired
    private JobExecutionLoggingListener jobExecutionLoggingListener;
    @Autowired
    private StepExecutionLoggingListener stepExecutionLoggingListener;

    @Bean
    public Job createInsertSqlJob(Step createInsertSqlStep) {
        return jobBuilderFactory.get("createInsertSqlJob").start(createInsertSqlStep)
                .listener(jobExecutionLoggingListener).build();
    }

    @Bean
    public Step createInsertSqlStep() {
        return stepBuilderFactory.get("createInsertSqlStep").tasklet(createInsertSqlTasklet)
                .listener(stepExecutionLoggingListener).build();
    }
}
