package app.rondondon.beddit.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {})
@NotBlank(message = "Password cannot be blank")
@Size(min = 6, max = 32, message = "Password size must be between 6 and 32 chars")
@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$", message = "Password must contain at least 1 letter and 1 digit")
public @interface Password {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}