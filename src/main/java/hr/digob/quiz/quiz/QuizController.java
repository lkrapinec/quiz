package hr.digob.quiz.quiz;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quiz")
public class QuizController {
    @GetMapping
    public ResponseEntity<String> getQuiz() {
        return ResponseEntity.ok("Hello World");
    }
}
