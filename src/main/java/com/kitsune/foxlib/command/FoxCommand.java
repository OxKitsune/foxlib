package com.kitsune.foxlib.command;

import net.md_5.bungee.api.ChatColor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FoxCommand {

    String path();

    String[] aliases() default {};

    String permission() default "";

    String noPermissionsMessage() default "&cInsufficient permissions!";

    String description() default "No description defined.";

    String usage() default "No usage defined.";


}
