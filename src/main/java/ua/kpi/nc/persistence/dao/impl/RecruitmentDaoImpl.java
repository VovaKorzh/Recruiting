package ua.kpi.nc.persistence.dao.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.kpi.nc.persistence.dao.RecruitmentDAO;
import ua.kpi.nc.persistence.model.Recruitment;
import ua.kpi.nc.persistence.model.SchedulingStatus;
import ua.kpi.nc.persistence.model.impl.real.RecruitmentImpl;
import ua.kpi.nc.persistence.util.JdbcTemplate;
import ua.kpi.nc.persistence.util.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Chalienko on 22.04.2016.
 */
public class RecruitmentDaoImpl implements RecruitmentDAO {
    private static Logger log = LoggerFactory.getLogger(RecruitmentDaoImpl.class.getName());

    private final JdbcDaoSupport jdbcDaoSupport;

    public RecruitmentDaoImpl(DataSource dataSource) {
        this.jdbcDaoSupport = new JdbcDaoSupport();
        jdbcDaoSupport.setJdbcTemplate(new JdbcTemplate(dataSource));
    }

    private ResultSetExtractor<Recruitment> extractor = resultSet -> {
        Recruitment recruitment = new RecruitmentImpl();
        recruitment.setId(resultSet.getLong("id"));
        recruitment.setEndDate(resultSet.getTimestamp("end_date"));
        recruitment.setName(resultSet.getString("name"));
        recruitment.setStartDate(resultSet.getTimestamp("start_date"));
        recruitment.setMaxGeneralGroup(resultSet.getInt("max_general_group"));
        recruitment.setMaxAdvancedGroup(resultSet.getInt("max_advanced_group"));
        recruitment.setRegistrationDeadline(resultSet.getTimestamp("registration_deadline"));
        recruitment.setScheduleChoicesDeadline(resultSet.getTimestamp("schedule_choices_deadline"));
        recruitment.setStudentsOnInterview(resultSet.getInt("students_on_interview"));
        recruitment.setTimeInterviewSoft(resultSet.getInt("time_interview_soft"));
        recruitment.setTimeInterviewTech(resultSet.getInt("time_interview_tech"));
        recruitment.setNumberSoftInterviewers(resultSet.getInt("number_soft_interviewers"));
        recruitment.setNumberTechInterviewers(resultSet.getInt("number_tech_interviewers"));
        recruitment.setNumberOfDays(resultSet.getInt("number_of_hours"));
        recruitment.setSchedulingStatus(new SchedulingStatus(resultSet.getString("title"), resultSet.getLong("scheduling_status")));
        return recruitment;
    };

    private static final String SQL_GET_RECRUITMENT_BY_ID = "SELECT r.id, r.name,r.start_date,r.end_date," +
            "r.max_general_group, r.max_advanced_group, r.registration_deadline, r.schedule_choices_deadline," +
            " r.schedule_choices_deadline, r.students_on_interview  ,r.time_interview_soft, r.time_interview_tech," +
            "r.number_tech_interviewers, r.number_soft_interviewers, r.number_of_hours, r.scheduling_status,  " +
            "ss.title FROM recruitment r JOIN scheduling_status ss on (ss.id= r.scheduling_status)" +
            "WHERE r.id = ?;";

    private static final String SQL_GET_RECRUITMENT_BY_NAME = "SELECT r.id, r.name,r.start_date,r.end_date," +
            "r.max_general_group, r.max_advanced_group, r.registration_deadline, r.schedule_choices_deadline," +
            " r.schedule_choices_deadline, r.students_on_interview ,r.time_interview_soft, r.time_interview_tech," +
            "r.number_tech_interviewers, r.number_soft_interviewers, r.number_of_hours, r.scheduling_status, \n" +
            "ss.title  FROM recruitment r JOIN scheduling_status ss on (ss.id= r.scheduling_status)" +
            "WHERE r.name = ?;";

    private static final String SQL_GET_ALL = "SELECT r.id, r.name,r.start_date,r.end_date," +
            "r.max_general_group, r.max_advanced_group, r.registration_deadline, r.schedule_choices_deadline," +
            " r.schedule_choices_deadline, r.students_on_interview ,r.time_interview_soft, r.time_interview_tech," +
            "r.number_tech_interviewers, r.number_soft_interviewers, r.number_of_hours, r.scheduling_status, \n" +
            "ss.title  " +
            "FROM \"recruitment\" r JOIN scheduling_status ss on (ss.id= r.scheduling_status)";

    private static final String SQL_GET_ALL_SORTED = SQL_GET_ALL + " ORDER BY r.start_date";

    private static final String SQL_UPDATE = "UPDATE \"recruitment\" " +
            "SET name = ? , start_date = ?,\n" +
            "end_date = ?, max_general_group = ?, max_advanced_group = ?, registration_deadline = ?," +
            "schedule_choices_deadline = ?, students_on_interview = ?, time_interview_tech = ?, " +
            "time_interview_soft = ?, number_tech_interviewers = ?, number_soft_interviewers = ?, number_of_hours = ? ," +
            "scheduling_status = ? \n" +
            "WHERE recruitment.id = ?;";

    private static final String SQL_INSERT = "INSERT INTO \"recruitment\"(name, start_date," +
            "end_date, max_general_group, max_advanced_group, registration_deadline, schedule_choices_deadline, " +
            "students_on_interview, time_interview_tech, time_interview_soft, number_tech_interviewers," +
            "number_soft_interviewers) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";

    private static final String SQL_DELETE = "DELETE FROM \"recruitment\" WHERE \"recruitment\".id = ?;";

    private static final String SQL_GET_CURRENT = "SELECT r.id, r.name,r.start_date,r.end_date," +
            "r.max_general_group, r.max_advanced_group, r.registration_deadline, r.schedule_choices_deadline," +
            " r.schedule_choices_deadline, r.students_on_interview  ,r.time_interview_soft, r.time_interview_tech," +
            "r.number_tech_interviewers, r.number_soft_interviewers, r.number_of_hours, r.scheduling_status, \n" +
            "ss.title FROM \"recruitment\" r JOIN scheduling_status ss on (ss.id= r.scheduling_status) " +
            "WHERE r.end_date > CURRENT_DATE;";

    private static final String SQL_GET_REGISTERED_COUNT = "SELECT COUNT(*) FROM \"application_form\" + apl\n" +
            "WHERE apl.id_recruitment=?";

    private static final String SQL_GET_LAST_N_RECRUITMENT = "SELECT r.id, r.name,r.start_date,r.end_date, r.max_general_group, " +
            " r.max_advanced_group, r.registration_deadline, r.schedule_choices_deadline, " +
            " r.schedule_choices_deadline, r.students_on_interview  ,r.time_interview_soft, r.time_interview_tech, " +
            " r.number_tech_interviewers, r.number_soft_interviewers, r.number_of_hours, r.scheduling_status, " +
            " ss.title FROM recruitment r JOIN scheduling_status ss on (ss.id= r.scheduling_status) ORDER BY r.id DESC LIMIT ?;";

    @Override
    public Recruitment getRecruitmentById(Long id) {
        log.info("Looking for recruitment with id = {}", id);
        return jdbcDaoSupport.getJdbcTemplate().queryWithParameters(SQL_GET_RECRUITMENT_BY_ID, extractor, id);
    }

    @Override
    public Recruitment getRecruitmentByName(String name) {
        log.info("Looking for recruitment with name = {}", name);
        return jdbcDaoSupport.getJdbcTemplate().queryWithParameters(SQL_GET_RECRUITMENT_BY_NAME, extractor, name);
    }

    @Override
    public int updateRecruitment(Recruitment recruitment) {
        log.info("Update recruitment with name = {}", recruitment.getName());
        return jdbcDaoSupport.getJdbcTemplate().update(SQL_UPDATE, recruitment.getName(), recruitment.getStartDate(),
                recruitment.getEndDate(), recruitment.getMaxGeneralGroup(), recruitment.getMaxAdvancedGroup(),
                recruitment.getRegistrationDeadline(), recruitment.getScheduleChoicesDeadline(),
                recruitment.getStudentsOnInterview(), recruitment.getTimeInterviewTech(), recruitment.getTimeInterviewSoft(),
                recruitment.getNumberTechInterviewers(), recruitment.getNumberSoftInterviewers(),
                recruitment.getNumberOfDays(), recruitment.getSchedulingStatus().getId(), recruitment.getId());
    }

    @Override
    public boolean addRecruitment(Recruitment recruitment) {
        log.info("Insert recruitment with name = {}", recruitment.getName());
        return jdbcDaoSupport.getJdbcTemplate().insert(SQL_INSERT, recruitment.getName(), recruitment.getStartDate(),
                recruitment.getEndDate(), recruitment.getMaxGeneralGroup(), recruitment.getMaxAdvancedGroup(),
                recruitment.getRegistrationDeadline(), recruitment.getScheduleChoicesDeadline(),
                recruitment.getStudentsOnInterview(), recruitment.getTimeInterviewTech(), recruitment.getTimeInterviewSoft(),
                recruitment.getNumberTechInterviewers(), recruitment.getNumberSoftInterviewers()) > 0;
    }

    @Override
    public int deleteRecruitment(Recruitment recruitment) {
        log.info("Delete Recruitment with id = {}", recruitment.getId());
        return jdbcDaoSupport.getJdbcTemplate().update(SQL_DELETE, recruitment.getId());
    }

    @Override
    public List<Recruitment> getAll() {
        log.info("Get all recruitment");
        return jdbcDaoSupport.getJdbcTemplate().queryForList(SQL_GET_ALL, extractor);
    }

    @Override
    public Recruitment getCurrentRecruitmnet() {
        log.trace("Getting current recruitment");
        return jdbcDaoSupport.getJdbcTemplate().queryWithParameters(SQL_GET_CURRENT, extractor);
    }

    private Long getRegisteredNumbers(Long recruitmentId) {
        log.info("Get Registered number");
        return jdbcDaoSupport.getJdbcTemplate().queryWithParameters(SQL_GET_REGISTERED_COUNT, resultSet -> resultSet.getLong(1), recruitmentId);
    }

    @Override
    public List<Recruitment> getAllSorted() {
        log.info("Get sorted recruitments");
        return jdbcDaoSupport.getJdbcTemplate().queryForList(SQL_GET_ALL_SORTED, extractor);
    }

    @Override
    public Recruitment getLastRecruitment() {
        log.info("Get last recruitment");
        return jdbcDaoSupport.getJdbcTemplate().queryWithParameters(SQL_GET_LAST_N_RECRUITMENT, extractor, 1);
    }
}
