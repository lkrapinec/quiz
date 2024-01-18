package hr.digob.quiz.quiz;

import hr.digob.quiz.quiz.entity.NewQuestionDto;
import hr.digob.quiz.quiz.entity.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<Void> saveQuestion(Principal principal, @RequestBody NewQuestionDto question) {
        try {
            questionService.save(principal, question);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{topicId}")
    public ResponseEntity<List<Question>> getSavedQuestion(Principal principal, @PathVariable Long topicId) {
        try {
            List<Question> questions = questionService.getSavedQuestions(principal, topicId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
