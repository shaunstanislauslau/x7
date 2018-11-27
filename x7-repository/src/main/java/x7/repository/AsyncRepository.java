package x7.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import x7.repository.dao.AsyncDao;

import java.util.List;

/**
 * Created by Sim on 2018/11/27.
 */
@Repository
public class AsyncRepository {

    @Autowired
    private AsyncDao asyncDao;

    public void create(Object obj) {
        this.asyncDao.create(obj);
    }

    public void refresh(Object obj) {
        this.asyncDao.refresh(obj);
    }

    public void remove(Object obj) {
        this.asyncDao.remove(obj);
    }

}
