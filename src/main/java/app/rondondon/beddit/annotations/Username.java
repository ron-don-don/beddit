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
@NotBlank(message = "Username cannot be blank")
@Size(min = 3, max = 16, message = "Username size must be between 3 and 16 chars")
@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can contain only letters, digits and underscores")
public @interface Username {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
