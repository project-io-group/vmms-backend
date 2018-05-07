package pl.agh.edu.iisg.io.vmms.vmmsbackend.validator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ReservationPeriodValidator.class)
@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Documented
public @interface ValidReservationPeriod {
    String message() default "An End date must be after a begin date "
            + "and both must be in the future," +
            " there have to be enough machines in the pool during the whole reservation duration.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
