package dbdump.job0020;

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
@ComponentScans({@ComponentScan("dbdump.common"), @ComponentScan("dbdump.job0020")})
public class AppConfig0020 {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DiffDataTasklet diffDataTasklet;
    @Autowired
    private JobExecutionLoggingListener jobExecutionLoggingListener;
    @Autowired
    private StepExecutionLoggingListener stepExecutionLoggingListener;

    @Bean
    public Job diffDataJob(Step diffDataStep) {
        return jobBuilderFactory.get("diffDataJob").start(diffDataStep)
                .listener(jobExecutionLoggingListener).build();
    }

    @Bean
    public Step diffDataStep() {
        return stepBuilderFactory.get("diffDataStep").tasklet(diffDataTasklet)
                .listener(stepExecutionLoggingListener).build();
    }
}
