package dbdump.common.listener;

import java.io.File;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import dbdump.common.exception.BeforeStepException;

@Component
@Scope("step")
public class CheckJobParameterErrorStepExecutionListener implements StepExecutionListener {

    @Value("#{jobParameters['inputFile']}") // (1)
    private File inputFile;

    @Value("#{jobParameters['outputFile']}") // (1)
    private File outputFile;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        if (inputFile == null) {
            throw new BeforeStepException("The input file must be not null."); // (2)
        } else if (outputFile == null) {
            throw new BeforeStepException("The output file must be not null."); // (2)
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // omitted.
        return null;
    }
}