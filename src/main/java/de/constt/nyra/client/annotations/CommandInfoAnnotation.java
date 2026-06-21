package de.constt.nyra.client.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfoAnnotation {
    String name();
    String description();
    String command();
}