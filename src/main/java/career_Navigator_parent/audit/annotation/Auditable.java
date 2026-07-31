package career_Navigator_parent.audit.annotation;

import career_Navigator_parent.audit.enums.AuditAction;
import career_Navigator_parent.audit.enums.AuditEntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    AuditAction action();

    AuditEntityType entityType();

    String description() default "";

    String entityIdParameter() default "";

    boolean captureArguments() default true;

    boolean captureResponse() default true;

}