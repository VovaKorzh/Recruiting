package ua.kpi.nc.service;

import ua.kpi.nc.persistence.model.Status;

import java.util.List;

/**
 * Created by Chalienko on 21.04.2016.
 */
public interface StatusService {

    Status getStatusById(Long id);

    int insertStatus(Status status);

    int updateStatus(Status status);

    int deleteStatus(Status status);

    List<Status> getAllStatuses();

    Status getByName(String name);
}
