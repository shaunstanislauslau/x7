package x7;

import org.springframework.context.annotation.Import;
import x7.txtrace.TxController;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(TxController.class)
public @interface EnableTxTracer {

}
