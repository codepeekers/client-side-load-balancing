package com.example.cd.courseservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/courses")
public class CourseController {

  private Map<Integer, Course> courseMap = new HashMap<>();

  @PostConstruct
  public void init() {
    courseMap.put(1, new Course("Data structures", 1000, 1));
    courseMap.put(2, new Course("Artificial Intelligence", 10000, 2));
    courseMap.put(3, new Course("Java", 30000, 3));
    courseMap.put(4, new Course("Spriongboot", 40000, 4));
  }

  @GetMapping("/details/{courseId}")
  public Course getCourseById(@PathVariable("courseId") int courseId) {
    return courseMap.get(courseId);
  }
}
