package pl.agh.edu.iisg.io.vmms.vmmsbackend.validator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ReservationPeriodValidator.class)
@Target({ METHOD, CONSTRUCTOR })
@Retention(RUNTIME)
@Documented
public @interface ValidReservationPeriod {
    String message() default "End date must be after begin date "
            + "and both must be in the future," +
            " must be enough machines in the pool during whole period";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
