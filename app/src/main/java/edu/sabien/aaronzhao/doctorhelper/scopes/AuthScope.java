package edu.sabien.aaronzhao.doctorhelper.scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by AaronZhao on 28/04/16.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthScope {
}
