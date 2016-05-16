package ua.kpi.nc.controller.admin;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ua.kpi.nc.DeadlineController;
import ua.kpi.nc.persistence.dto.RecruitmentSettingsDto;
import ua.kpi.nc.persistence.model.ApplicationForm;
import ua.kpi.nc.persistence.model.EmailTemplate;
import ua.kpi.nc.persistence.model.Recruitment;
import ua.kpi.nc.persistence.model.User;
import ua.kpi.nc.persistence.model.impl.real.RecruitmentImpl;
import ua.kpi.nc.service.ApplicationFormService;
import ua.kpi.nc.service.EmailTemplateService;
import ua.kpi.nc.service.RecruitmentService;
import ua.kpi.nc.service.ServiceFactory;
import ua.kpi.nc.service.UserService;
import ua.kpi.nc.service.util.SenderService;
import ua.kpi.nc.service.util.SenderServiceImpl;

import java.sql.Timestamp;
import java.util.List;

import javax.mail.MessagingException;

/**
 * Created by dima on 30.04.16.
 */
@RestController
@RequestMapping("/admin")
public class AdminRecruitmentSettingsController {

    private RecruitmentService recruitmentService = ServiceFactory.getRecruitmentService();
    private DeadlineController deadlineController = DeadlineController.getInstance();
    private ApplicationFormService applicationFormService = ServiceFactory.getApplicationFormService();
    private EmailTemplateService emailTemplateService = ServiceFactory.getEmailTemplateService();
    private UserService userService = ServiceFactory.getUserService();
    private SenderService senderService = SenderServiceImpl.getInstance();
    
    @RequestMapping(value = "/addRecruitment", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    private void addRecruitmentSettings(@RequestBody RecruitmentSettingsDto recruitmentDto) throws MessagingException {
        if (null == recruitmentService.getCurrentRecruitmnet()){

        System.out.println(recruitmentDto);
        Recruitment recruitment = new RecruitmentImpl(recruitmentDto.getName(), new Timestamp(System.currentTimeMillis()), recruitmentDto.getMaxAdvancedGroup(), recruitmentDto.getMaxGeneralGroup(),
                Timestamp.valueOf(recruitmentDto.getRegistrationDeadline()), Timestamp.valueOf(recruitmentDto.getScheduleChoicesDeadline()));
        recruitment.setEndDate(Timestamp.valueOf(recruitmentDto.getEndDate()));
        recruitmentService.addRecruitment(recruitment);
        EmailTemplate newRecruitmentTemplate = emailTemplateService.getById(7L);
        List<User> users = userService.getStudentsWithNotconnectedForms();
        for (User student : users) {
        	String subject = newRecruitmentTemplate.getTitle();
  			String text = emailTemplateService.showTemplateParams(newRecruitmentTemplate.getText(), student);
  			senderService.send(student.getEmail(), subject, text);
		}
        deadlineController.setRegisteredDeadline(recruitment.getRegistrationDeadline());
        deadlineController.setEndOfRecruitingDeadLine(recruitment.getEndDate());
        }

    }
    @RequestMapping(value = "/editRecruitment", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public void editRecruitment(@RequestBody RecruitmentSettingsDto recruitmentDto){
        Recruitment recruitment = recruitmentService.getRecruitmentByName(recruitmentDto.getName());
        System.out.println(recruitment);
        recruitment.setName(recruitmentDto.getName());
        recruitment.setRegistrationDeadline(Timestamp.valueOf(recruitmentDto.getRegistrationDeadline()));
        recruitment.setScheduleChoicesDeadline(Timestamp.valueOf(recruitmentDto.getScheduleChoicesDeadline()));
        recruitment.setEndDate(Timestamp.valueOf(recruitmentDto.getEndDate()));
        recruitment.setMaxGeneralGroup(recruitmentDto.getMaxGeneralGroup());
        recruitment.setMaxAdvancedGroup(recruitmentDto.getMaxAdvancedGroup());
        System.out.println(recruitment);
        recruitmentService.updateRecruitment(recruitment);
    }

//    @RequestMapping(value = "/endRecruitment", method = RequestMethod.GET)
//    private void endRecruitment() {
//        Recruitment recruitment = recruitmentService.getCurrentRecruitmnet();
//        recruitment.setEndDate(new Timestamp(System.currentTimeMillis()));
////        recruitmentService.updateRecruitment(recruitment);
//        List<ApplicationForm> applicationFormList = applicationFormService.getCurrentApplicationForms();
//        for (ApplicationForm applicationForm : applicationFormList) {
//            applicationForm.setActive(false);
////            applicationFormService.updateApplicationForm(applicationForm);
//            System.out.println(applicationForm.toString());
//        }
//    }

    @RequestMapping(value = "/getCurrentRecruitment", method = RequestMethod.GET)
    private Recruitment getCurrentRecruitment() {
        return recruitmentService.getCurrentRecruitmnet();
    }

}
