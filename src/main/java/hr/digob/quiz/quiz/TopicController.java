package hr.digob.quiz.quiz;

import hr.digob.quiz.quiz.dto.QuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/topic")
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<List<TopicDto>> getTopics(Principal principal) {
        List<TopicDto> quiz = topicService.getTopics(principal);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping
    public ResponseEntity<Long> saveTopic(Principal principal, @RequestBody TitleAndContent titleAndContent) {
        Long id = topicService.save(principal.getName(), titleAndContent);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}/questions/{count}")
    public ResponseEntity<List<QuestionDto>> getQuestions(Principal principal, @PathVariable String id, @PathVariable int count) {
        List<QuestionDto> questions = topicService.generateQuestions(principal, id, count);
        return ResponseEntity.ok(questions);
    }


}
