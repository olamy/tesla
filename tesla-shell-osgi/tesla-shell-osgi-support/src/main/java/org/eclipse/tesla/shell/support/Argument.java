package org.eclipse.tesla.shell.support;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention( RUNTIME )
@Target( { FIELD, METHOD } )
public @interface Argument
{

    java.lang.String DEFAULT_STRING = "DEFAULT";

    java.lang.String DEFAULT = "##default";

    java.lang.String name() default "##default";

    java.lang.String description() default "";

    boolean required() default false;

    int index() default 0;

    boolean multiValued() default false;

    java.lang.String valueToShowInHelp() default "DEFAULT";

}
