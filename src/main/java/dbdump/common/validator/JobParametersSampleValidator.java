package dbdump.common.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.stereotype.Component;

@Component
public class JobParametersSampleValidator implements JobParametersValidator {

  @Override
  public void validate(JobParameters parameters) throws JobParametersInvalidException {
    // ommited

    // parameters.getParameters();
    // throw new JobParametersInvalidException("xxxxx");

  }
}
