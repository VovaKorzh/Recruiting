package ua.kpi.nc.persistence.dao.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.kpi.nc.persistence.dao.StatusDao;
import ua.kpi.nc.persistence.model.Status;
import ua.kpi.nc.persistence.util.JdbcTemplate;
import ua.kpi.nc.persistence.util.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IO on 21.04.2016.
 */
public class StatusDaoImpl extends JdbcDaoSupport implements StatusDao {

    private static Logger log = LoggerFactory.getLogger(StatusDaoImpl.class.getName());

    public StatusDaoImpl(DataSource dataSource) {
        this.setJdbcTemplate(new JdbcTemplate(dataSource));
    }

    private ResultSetExtractor<Status> extractor = resultSet -> {
        Status status = new Status();
        status.setId(resultSet.getLong("id"));
        status.setTitle(resultSet.getString("title"));
        return status;
    };

    @Override
    public Status getById(Long id) {
        log.trace("Looking for user with id = ", id);
        return this.getJdbcTemplate().queryWithParameters("SELECT status.id, status.title FROM public.status WHERE status.id = ?;",
                extractor, id);
    }

    @Override
    public int insertStatus(Status status) {
        log.trace("Insert status");
        return this.getJdbcTemplate().update("INSERT INTO public.status(status.title) VALUES(?);", status.getTitle());
    }

    @Override
    public int updateStatus(Status status) {
        log.trace("Update status with id = ", status.getId());
        return this.getJdbcTemplate().update("UPDATE public.status SET status.title = ? WHERE status.id = ?;",
                status.getTitle(), status.getId());
    }

    @Override
    public int deleteStatus(Status status) {
        log.trace("Delete status with id = ", status.getId());
        return this.getJdbcTemplate().update("DELETE FROM public.status WHERE status.id = ?;", status.getId());
    }
}
