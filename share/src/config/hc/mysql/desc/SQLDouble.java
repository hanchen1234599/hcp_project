package hc.mysql.desc;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Documented
@Target(ElementType.FIELD) // 域声明（包括枚举类型实例）
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLDouble {
	Constraints constraints() default @Constraints; // 约束注解，详细见下面代码
}
