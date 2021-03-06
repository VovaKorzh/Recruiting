package ua.kpi.nc.service.impl;

import java.util.List;
import java.util.Set;

import ua.kpi.nc.persistence.dao.ReportDao;
import ua.kpi.nc.persistence.model.FormAnswerVariant;
import ua.kpi.nc.persistence.model.FormQuestion;
import ua.kpi.nc.persistence.model.Recruitment;
import ua.kpi.nc.persistence.model.ReportInfo;
import ua.kpi.nc.persistence.model.enums.ReportTypeEnum;
import ua.kpi.nc.report.Line;
import ua.kpi.nc.report.Report;
import ua.kpi.nc.service.FormAnswerVariantService;
import ua.kpi.nc.service.FormQuestionService;
import ua.kpi.nc.service.RecruitmentService;
import ua.kpi.nc.service.ReportService;
import ua.kpi.nc.service.ServiceFactory;

/**
 * Created by Nikita on 24.04.2016.
 */
public class ReportServiceImpl implements ReportService {
	private ReportDao reportDao;

	public ReportServiceImpl(ReportDao reportDao) {
		this.reportDao = reportDao;
	}

	@Override
	public ReportInfo getByID(Long id) {
		return reportDao.getByID(id);
	}

	@Override
	public ReportInfo getByTitle(String title) {
		return reportDao.getByTitle(title);
	}

	@Override
	public Set<ReportInfo> getAll() {
		return reportDao.getAll();
	}

	@Override
	public Report getReportOfApproved() {
		ReportInfo reportInfo = getByID(ReportTypeEnum.APPROVED.getId());
		Report report = new Report(reportInfo.getTitle());
		List<Line> lines = reportDao.extractWithMetaData(reportInfo);
		if (lines.size() > 0) {
			report.setHeader(lines.get(0));
			report.setLines(lines.subList(1, lines.size()));
		}
		return report;
	}

	@Override
	public Report getReportOfAnswers(Long idQuestion) {
		RecruitmentService recruitmentService = ServiceFactory.getRecruitmentService();
		ReportInfo reportInfo = getByID(ReportTypeEnum.ANSWERS.getId());
		Report report = new Report(reportInfo.getTitle());
		FormAnswerVariantService variantService = ServiceFactory.getFormAnswerVariantService();
		List<Recruitment> recruitments = recruitmentService.getAllSorted();
		report.getHeader().addCell("Recruitment");
		for (Recruitment recruitment : recruitments) {
			report.getHeader().addCell(recruitment.getName());
		}
		FormQuestionService formQuestionService = ServiceFactory.getFormQuestionService();
		FormQuestion formQuestion = formQuestionService.getById(idQuestion);
		if (formQuestion != null) {
			List<FormAnswerVariant> variants = variantService.getAnswerVariantsByQuestion(formQuestion);
			for (FormAnswerVariant variant : variants) {
				Line line = reportDao.getAnswerVariantLine(reportInfo, formQuestion, variant);
				line.addFirstCell(variant.getAnswer());
				report.addRow(line);
			}
		}
		return report;
	}

}
