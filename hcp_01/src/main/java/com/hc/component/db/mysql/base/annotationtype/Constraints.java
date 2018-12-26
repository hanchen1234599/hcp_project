package com.hc.component.db.mysql.base.annotationtype;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraints {
	boolean primaryKey() default false; // 主键，默认为空
	boolean allowNull() default true; // 默认允许为空
	boolean autoIncement() default false; //自动增长
}
