package x7.core.repository;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author Sim
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE}) 
public @interface X {
	
	String SUFFIX = "${SUFFIX}";
	String PAGINATION = "${PAGINATION}";
	
	int KEY_ONE = 1;
	int KEY_SHARDING = 7;
	
	/**
	 * just string(60<=length < 512), datetime, text<br>
	 * only effect on getter<br>
	 */
	String type() default "";
	/**
	 * 
	 * only effect on getter<br>
	 */
	int length() default 60;
	
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.TYPE}) 
	@interface Mapping {
		String value() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface isMobile{
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface isEmail{
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface notNull{
	}
	
	/**
	 * 
	 * not cached two
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	@interface noCache{
	}
	
	/**
	 * 
	 * only effect on property<br>
	 * will not save the property in relation DB, like MySql<br>
	 * but will save the property int cache, or K-V DB,like mc or redis<br>
	 * instead of "transient", while transport the stream of object
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface ignore{
	}
	

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface ID{
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@interface Sharding{
	}
}
