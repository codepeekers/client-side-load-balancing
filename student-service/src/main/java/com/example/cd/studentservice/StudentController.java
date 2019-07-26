package com.example.cd.studentservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
@Slf4j
public class StudentController {

  private List<Student> students = new ArrayList<>();

  @Autowired private DiscoveryClient discoveryClient;
  @Autowired private LoadBalancerClient loadBalancerClient;
  @Autowired private RestTemplate restTemplate;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @PostConstruct
  public void init() {
    students.add(new Student("John", "email@gmail.coom", 1));
    students.add(new Student("Miller", "miller@gmail.coom", 2));
    students.add(new Student("Tom", "tom@gmail.coom", 3));
    students.add(new Student("Hanks", "hanks@gmail.coom", 4));
  }

  @GetMapping("/list")
  public List<StudentDTO> getStudents() {
    log.info("request received to list students");
    List<ServiceInstance> serviceInstances = discoveryClient.getInstances("course-service");
    List<String> hostPorts =
        serviceInstances.stream()
            .map(serviceInstance -> serviceInstance.getHost() + ":" + serviceInstance.getPort())
            .collect(Collectors.toList());
    log.info("Instance list :-> {}", hostPorts);

    ServiceInstance serviceInstance = loadBalancerClient.choose("course-service");
    log.info("Loadbalancer chose {}:{}", serviceInstance.getHost(), serviceInstance.getPort());

    String url =
        "http://"
            + serviceInstance.getHost()
            + ":"
            + serviceInstance.getPort()
            + "/courses/details/";
    return students
        .parallelStream()
        .map(student -> getStudentDTO(url, student))
        .collect(Collectors.toList());
  }

  private StudentDTO getStudentDTO(String url, Student student) {
    url = url + student.getCourseId();
    Course course = restTemplate.getForObject(url, Course.class);
    return new StudentDTO(student.getName(), student.getEmailId(), course);
  }
}
