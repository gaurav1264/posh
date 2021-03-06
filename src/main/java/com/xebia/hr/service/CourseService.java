package com.xebia.hr.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xebia.hr.Converter.CourseConverter;
import com.xebia.hr.constants.AppConstants;
import com.xebia.hr.dto.CourseDto;
import com.xebia.hr.dto.QuestionDto;
import com.xebia.hr.entity.Attempt;
import com.xebia.hr.entity.Course;
import com.xebia.hr.entity.Employee;
import com.xebia.hr.exceptions.NotFoundException;
import com.xebia.hr.repository.AttemptRepository;
import com.xebia.hr.repository.CourseRepository;
import com.xebia.hr.utils.CommonUtils;


/**
 * @author gauravagrawal
 * @since 15 July, 16
 */
@Service
public class CourseService {
	
	private final static Logger logger = LoggerFactory.getLogger(CourseService.class);
	
	@Autowired
	private EmployeeService employeeService;
	
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private AttemptRepository attemptRepository;
    
    @Autowired
    private AttemptService attemptService;
    
    @Autowired
	private QuestionService questionService;
    
    @Value("${induction.course.score.pass.percent}")
    private Integer coursePassPercent;
    
    @Value("${induction.course.maxAttempt}")
	private Integer maxAttempt;

    public List<Course> findAll(){
    	List<Course> courses = courseRepository.findAll();
    	return courses;
    }
    
    public Course save(Course course){
    	return courseRepository.save(course);
    }
    
    public List<CourseDto> findCourses(String empId) throws NotFoundException{
    	logger.info("Getting courses for employee id:"+empId);
    	Employee employee = employeeService.findByEmpId(empId);
    	List<Course> courses = courseRepository.findByEmployees(employee);
    	
    	List<CourseDto> dtos = new ArrayList<>(courses.size());
    	for(Course course : courses){
    		List<Attempt> attempts = attemptRepository.findByCourseAndEmployee(course, employee);
    		CourseDto courseDto = CourseConverter.CONVERT_COURSE_TO_COURSE_DTO.apply(attempts, course);
    		courseDto.setMaxAttempt(maxAttempt);
        	dtos.add(courseDto);
    	}
    	logger.info("Returned courses: "+dtos);
    	return dtos;
    }
    
    public Course findOne(Long courseId) throws NotFoundException{
    	Course course = courseRepository.findOne(courseId);
		if(Objects.isNull(course)){
			throw new NotFoundException("Invalid course id: "+ courseId);
		}
    	return course;
    }
    
    public void submitCourse(List<QuestionDto> questions, long attemptId) throws Exception{
    	
    	try {
			Attempt attempt = attemptService.findOne(attemptId);
			int actualScore = 0;
			List<QuestionDto> actualQues = questionService.findAllQuestions(attempt.getCourse().getId());

			HashMap<Long, QuestionDto> questionmap = new HashMap<>();

			for(QuestionDto question : actualQues){
				questionmap.put(Long.valueOf(question.getId()), question);
			}

			for (QuestionDto question : questions) {
				QuestionDto questiondb = questionmap.get(question.getId());
				String selectedChoiceId = question.getSelectedChoiceId();
				if(Objects.nonNull(selectedChoiceId)){
					if (questiondb.getCorrectChoiceId().equals(selectedChoiceId)) {
						actualScore++;
					}
				}
			}
			int percentage = CommonUtils.calculatepercentage(actualScore, attempt.getMaxScore()); 

			if (percentage >= coursePassPercent) {
				attempt.setResult(AppConstants.PASSED);
			} else {
				attempt.setResult(AppConstants.FAILED);
			}
			attempt.setFinishTime(new Timestamp(System.currentTimeMillis())); 
			attempt.setScore(actualScore);
			attempt.setScoreInPercent(Double.valueOf(percentage)); 
			attemptService.save(attempt);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    }

	public CourseDto getScoreCard(long attemptId) throws Exception {
		Attempt attempt = attemptService.findOne(attemptId);
		return CourseConverter.CONVERT_ATTEMPT_TO_COURSE_DTO.apply(attempt);
	}
    
}
