package ua.kpi.nc.service;

import ua.kpi.nc.persistence.model.SocialInformation;

/**
 * Created by Chalienko on 15.04.2016.
 */
public interface SocialInformationService {
    SocialInformation getById(Long id);
}
