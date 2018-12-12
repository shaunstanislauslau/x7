package x7;

import org.springframework.context.annotation.Import;
import x7.interceptor.RcDataSrouceConfiguration;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({RcDataSrouceConfiguration.class})
public @interface EnableTransactionManagementReadable {

}
