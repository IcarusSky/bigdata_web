package com.sunmnet.bigdata.web.zntb.dataprovider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatasourceParameter {
    // Display name of input in the web page
    String label();

    // Input type
    Type type();

    int order() default 0;

    // Init value
    String value() default "";

    String placeholder() default "";

    String[] options() default "N/A";

    boolean checked() default false;

    boolean required() default false;

    enum Type {
        Input("input"), TextArea("textarea"), Password("password"), Checkbox("checkbox"), Select("select"), Radio("radio");
        private String name;

        Type(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

    }
}