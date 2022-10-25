package com.rg.demo.student;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Student>> getStudent(@PathVariable long id) {
        return studentService.findStudentById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Student> listStudents(@RequestParam(name = "name", required = false) String name) {
        return studentService.findStudentsByName(name);
    }

    @PostMapping
    public Mono<Student> addNewStudent(@RequestBody Student student) {
        return studentService.addNewStudent(student);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Student>> updateStudent(@PathVariable long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteStudent(@PathVariable long id) {
        return studentService.findStudentById(id)
                .flatMap(s ->
                        studentService.deleteStudent(s)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "ndjson", produces = MediaType.APPLICATION_NDJSON_VALUE)
    Flux<Data> ndjson() {
        return source();
    }

    @GetMapping(value = "json")
    Flux<Data> json() {
        return source();
    }

    private Flux<Data> source() {
        return Flux.interval(Duration.ofSeconds(1))
                .take(5)
                .map(i -> new Data(i, Instant.now()));
    }

    @lombok.Data
    @AllArgsConstructor
    static class Data {
        private Long i;
        private Instant time;
    }
}
