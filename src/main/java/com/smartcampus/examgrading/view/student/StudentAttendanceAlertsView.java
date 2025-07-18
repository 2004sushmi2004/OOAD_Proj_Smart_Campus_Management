package com.smartcampus.examgrading.view.student;

import com.smartcampus.examgrading.model.Course;
import com.smartcampus.examgrading.model.User;
import com.smartcampus.examgrading.security.SecurityService;
import com.smartcampus.examgrading.service.CourseService;
import com.smartcampus.examgrading.view.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Route(value = "attendance/alerts", layout = MainLayout.class)
@PageTitle("Attendance Alerts | Smart Campus")
public class StudentAttendanceAlertsView extends VerticalLayout {

    private final SecurityService securityService;
    private final CourseService courseService;
    
    // Sample data for demo
    private final List<AttendanceAlert> attendanceAlerts = new ArrayList<>();

    public StudentAttendanceAlertsView(SecurityService securityService, CourseService courseService) {
        this.securityService = securityService;
        this.courseService = courseService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        H2 viewTitle = new H2("Attendance Alerts");
        add(viewTitle);
        
        User currentUser = securityService.getCurrentUser();
        if (currentUser == null) {
            add(new Span("Please log in to access this page"));
            return;
        }
        
        // Get courses the student is enrolled in
        List<Course> enrolledCourses = courseService.getEnrolledCourses(currentUser);
        
        if (enrolledCourses.isEmpty()) {
            add(new H4("You are not enrolled in any courses"));
            return;
        }
        
        // Create attendance summary cards
        VerticalLayout alertsContainer = new VerticalLayout();
        alertsContainer.setPadding(false);
        alertsContainer.setSpacing(true);
        
        // Generate sample data
        generateSampleAlerts(enrolledCourses);
        
        if (attendanceAlerts.isEmpty()) {
            add(new H4("No attendance issues detected"));
            return;
        }
        
        // Create alerts grid
        Grid<AttendanceAlert> alertsGrid = new Grid<>();
        alertsGrid.addComponentColumn(alert -> {
            Icon icon;
            if (alert.getSeverity().equals("Critical")) {
                icon = VaadinIcon.EXCLAMATION_CIRCLE.create();
                icon.setColor("var(--lumo-error-color)");
            } else if (alert.getSeverity().equals("Warning")) {
                icon = VaadinIcon.WARNING.create();
                icon.setColor("var(--lumo-warning-color)");
            } else {
                icon = VaadinIcon.INFO_CIRCLE.create();
                icon.setColor("var(--lumo-primary-color)");
            }
            return icon;
        }).setHeader("").setWidth("60px").setFlexGrow(0);
        
        alertsGrid.addColumn(alert -> alert.getCourse().getCourseName())
                 .setHeader("Course")
                 .setAutoWidth(true);
                 
        alertsGrid.addColumn(AttendanceAlert::getMessage)
                 .setHeader("Alert")
                 .setAutoWidth(true);
        
        alertsGrid.addComponentColumn(alert -> {
            ProgressBar progress = new ProgressBar();
            progress.setMin(0);
            progress.setMax(100);
            progress.setValue(alert.getAttendancePercentage());
            progress.setWidth("100px");
            
            if (alert.getAttendancePercentage() < 60) {
                progress.getStyle().set("--lumo-primary-color", "var(--lumo-error-color)");
            } else if (alert.getAttendancePercentage() < 70) {
                progress.getStyle().set("--lumo-primary-color", "var(--lumo-warning-color)");
            }
            
            HorizontalLayout progressLayout = new HorizontalLayout(
                progress, 
                new Span(alert.getAttendancePercentage() + "%")
            );
            progressLayout.setAlignItems(Alignment.CENTER);
            
            return progressLayout;
        }).setHeader("Attendance").setWidth("200px");
        
        alertsGrid.addColumn(AttendanceAlert::getDate)
                 .setHeader("Date")
                 .setAutoWidth(true);
        
        alertsGrid.setItems(attendanceAlerts);
        
        // Add help text
        Span helpText = new Span("You will receive alerts when your attendance falls below the minimum requirement of 70%");
        helpText.getStyle().set("color", "var(--lumo-secondary-text-color)");
        helpText.getStyle().set("font-style", "italic");
        
        add(helpText, alertsGrid);
    }
    
    private void generateSampleAlerts(List<Course> courses) {
        Random random = new Random();
        
        for (Course course : courses) {
            // Generate a random attendance percentage
            int attendancePercentage = 50 + random.nextInt(30); // 50-79%
            
            // Only add alerts for courses with attendance below 70%
            if (attendancePercentage < 70) {
                String severity = attendancePercentage < 60 ? "Critical" : "Warning";
                
                String message;
                if (attendancePercentage < 60) {
                    message = "Your attendance is critically low. You may be barred from exams.";
                } else {
                    message = "Your attendance is below the required 70% minimum.";
                }
                
                attendanceAlerts.add(new AttendanceAlert(
                    course,
                    message,
                    severity,
                    attendancePercentage,
                    LocalDate.now().minusDays(random.nextInt(7)).toString()
                ));
            }
        }
    }
    
    // Sample class for demo purposes
    private static class AttendanceAlert {
        private final Course course;
        private final String message;
        private final String severity;
        private final int attendancePercentage;
        private final String date;
        
        public AttendanceAlert(Course course, String message, String severity, 
                              int attendancePercentage, String date) {
            this.course = course;
            this.message = message;
            this.severity = severity;
            this.attendancePercentage = attendancePercentage;
            this.date = date;
        }
        
        public Course getCourse() {
            return course;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public int getAttendancePercentage() {
            return attendancePercentage;
        }
        
        public String getDate() {
            return date;
        }
    }
} 