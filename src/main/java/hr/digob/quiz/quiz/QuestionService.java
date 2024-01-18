package hr.digob.quiz.quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.digob.quiz.quiz.entity.NewQuestionDto;
import hr.digob.quiz.quiz.entity.Question;
import hr.digob.quiz.user.User;
import hr.digob.quiz.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final UserService userService;
    private final TopicService topicService;
    private final ObjectMapper mapper;

    public void save(Principal principal, NewQuestionDto question) throws IllegalAccessException {
        if (question.getTopicId() == null) {
            throw new IllegalArgumentException("Topic id must not be null");
        }
        if (question.getQuestion() == null) {
            throw new IllegalArgumentException("Question must not be null");
        }
        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName()).orElse(null);
        }

        var topic = topicService.findById(question.getTopicId().longValue());
        if (topic.isEmpty()) {
            throw new NoSuchElementException("Topic not found");
        }
        if (topic.get().getUser() != null && !topic.get().getUser().equals(user)) {
            throw new IllegalAccessException("User does not have permission to add questions to this topic");
        }
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        Question q = mapper.convertValue(question.getQuestion(), Question.class);
        q.setUser(user);
        topicService.addQuestion(topic.get(), q);
    }

    public List<Question> getSavedQuestions(Principal principal, Long topicId) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal must not be null");
        }
        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var topic = topicService.findById(topicId);
        if (topic.isEmpty()) {
            throw new NoSuchElementException("Wrong topic id");
        }
        if (topic.get().getUser() != null && !topic.get().getUser().equals(user)) {
            throw new IllegalArgumentException("User is not owner of this topic");
        }
        return topic.get().getQuiz().stream().filter(q -> q.getUser().equals(user)).toList();
    }
}
