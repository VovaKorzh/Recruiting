package ua.kpi.nc.persistence.dao;

import ua.kpi.nc.persistence.model.*;

import java.sql.Connection;
import java.util.Set;

/**
 * @author Desparete Houseviwes
 */
public interface FormAnswerDao {

	FormAnswer getById(Long id);

	Set<FormAnswer> getByInterviewAndQuestion(Interview interview, FormQuestion question);

	Long insertFormAnswer(FormAnswer formAnswer, Interview interview, FormQuestion question,
			FormAnswerVariant answerVariant, ApplicationForm applicationForm, Connection connection);

	Long insertFormAnswerForApplicationForm(FormAnswer formAnswer, FormQuestion question,
			FormAnswerVariant answerVariant, ApplicationForm applicationForm, Connection connection);

	Long insertFormAnswerForInterview(FormAnswer formAnswer, FormQuestion question, FormAnswerVariant answerVariant,
			Interview interview, Connection connection);

	int updateFormAnswer(FormAnswer formAnswer);

	int deleteFormAnswer(FormAnswer formAnswer);

}
