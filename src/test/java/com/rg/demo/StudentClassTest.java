package com.rg.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public class StudentClassTest {
    @Data
    @AllArgsConstructor
    private static class StudentClass {
        private Long id;
    }

    @Data
    @AllArgsConstructor
    private static class Student {
        private Long classId;
    }

    private static class StudentClassService {
        Flux<StudentClass> getAll() {
            return Flux.just(new StudentClass(3L), new StudentClass(1L), new StudentClass(4L));
        }
    }
    private static class StudentService {
        Flux<Student> getByClassIds(Collection<Long> classIds) {
            var values = Stream.of(new Student(1L), new Student(2L), new Student(3L), new Student(1L))
                    .filter(s -> classIds.contains(s.classId))
                    .toList();
            return Flux.fromIterable(values);
        }
    }

    private record StudentClassWithStudents(StudentClass studentClass, List<Student> students) {}

    @Test
    public void student_class_print() {

        var studentClassService = new StudentClassService();
        var studentService = new StudentService();
        studentClassService.getAll()
                .buffer(2)
                .map(studentClasses -> studentClasses.stream()
                        .collect(Collectors.toMap(StudentClass::getId, Function.identity())))
                .flatMap(studentClassById -> {
                    var monoStudents = studentService.getByClassIds(studentClassById.keySet())
                            .collectList()
                            .map(students -> students.stream()
                                    .collect(Collectors.groupingBy(Student::getClassId))
                            );
                    return Flux.fromIterable(studentClassById.values())
                            .flatMap(studentClass -> Mono.just(studentClass).zipWith(monoStudents));
                }).map(tuple2 -> {
                    var studentClass = tuple2.getT1();
                    var students = tuple2.getT2().getOrDefault(studentClass.id, emptyList());
                    return new StudentClassWithStudents(studentClass, students);
                }).subscribe(s -> System.out.println("result:" + s));

    }
}
