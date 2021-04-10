package dbdump.job0010;

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
import dbdump.common.validator.JobParametersSampleValidator;

@Configuration
@EnableBatchProcessing
@ComponentScans({@ComponentScan("dbdump.common"), @ComponentScan("dbdump.job0010")})
public class AppConfig0010 {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DbDumpTasklet dbDumpTasklet;
    @Autowired
    private JobExecutionLoggingListener jobExecutionLoggingListener;
    @Autowired
    private StepExecutionLoggingListener stepExecutionLoggingListener;
    @Autowired
    private JobParametersSampleValidator jobParametersSampleValidator;

    @Bean
    public Job dbDumpJob(Step dbDumpStep) {
        return jobBuilderFactory.get("dbDumpStepJob").validator(jobParametersSampleValidator)
                .start(dbDumpStep).listener(jobExecutionLoggingListener).build();
    }

    @Bean
    public Step dbDumpStep() {
        return stepBuilderFactory.get("dbDumpStep").tasklet(dbDumpTasklet)
                .listener(stepExecutionLoggingListener).build();
    }
}
