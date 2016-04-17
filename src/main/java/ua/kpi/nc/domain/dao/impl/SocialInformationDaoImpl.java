package ua.kpi.nc.domain.dao.impl;

import org.apache.log4j.Logger;
import ua.kpi.nc.domain.dao.SocialInformationDao;
import ua.kpi.nc.domain.model.SocialInformation;
import ua.kpi.nc.domain.model.SocialNetwork;
import ua.kpi.nc.domain.model.impl.proxy.UserProxy;
import ua.kpi.nc.domain.model.impl.real.SocialInformationImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Chalienko on 15.04.2016.
 */
public class SocialInformationDaoImpl extends DaoSupport implements SocialInformationDao {

    private static Logger log = Logger.getLogger(UserDaoImpl.class.getName());

    public SocialInformationDaoImpl() {
    }

    @Override
    public SocialInformation getById(Long id) {
        String sql = "SElECT si.*,sn.title\n" +
                "FROM \"social_information\" si\n" +
                "   INNER JOIN social_network sn ON si.id_social_network = sn.id "
                +" WHERE si.id =" + id;
        log.trace("Looking for user with id = " + id);
        return getSocialInformationByQuery(sql);
    }

    private SocialInformation getSocialInformationByQuery(String sql) {
        SocialInformation socialInformation = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            log.trace("Open connection");
            log.trace("Create prepared statement");
            log.trace("Get result set");
            if (resultSet.next()) {
                log.trace("Create Social Information to return");
                socialInformation = new SocialInformationImpl(
                        resultSet.getLong("id"),
                        resultSet.getString("access_info"),
                        new UserProxy(resultSet.getLong("id_user")),
                        new SocialNetwork(resultSet.getLong("id_social_network"),resultSet.getString("title")));
                return socialInformation;
            }
        } catch (SQLException e) {
            log.error("Cannot read user", e);
            return null;
        }
        if (null == socialInformation) {
            log.debug("Social Information not found");
        } else {
            log.trace("Social Information " + socialInformation + " found");
        }
        log.trace("Returning social information");
        return socialInformation;
    }

}
