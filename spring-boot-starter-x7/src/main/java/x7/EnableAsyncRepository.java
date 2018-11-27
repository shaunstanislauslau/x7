package x7;

import org.springframework.context.annotation.Import;
import x7.repository.AsyncRepository;
import x7.repository.dao.AsyncDaoImpl;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({AsyncDaoImpl.class,AsyncRepository.class})
public @interface EnableAsyncRepository {

}
