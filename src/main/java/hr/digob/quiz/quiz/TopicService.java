package hr.digob.quiz.quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.digob.quiz.quiz.dto.QuestionDto;
import hr.digob.quiz.quiz.entity.Question;
import hr.digob.quiz.quiz.entity.Topic;
import hr.digob.quiz.user.User;
import hr.digob.quiz.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TopicRepository topicRepository;
    private final UserService userService;

    public List<TopicDto> getTopics(Principal principal) {
        List<Topic> topics = new ArrayList<>();
        if (principal != null) {
            Optional<User> user = userService.findByUsername(principal.getName());
            if (user.isPresent()) {
                topics = topicRepository.findAllByUser(user.get());
            }
        }
        topics.addAll(topicRepository.findAllNonUser());
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        return topics.stream().map(topic -> mapper.convertValue(topic, TopicDto.class)).toList();
    }

    public Long save(String username, TitleAndContent titleAndContent) {
        String title = titleAndContent.getTitle();
        String description = titleAndContent.getContent();
        Topic topic = new Topic(title, description);
        userService.findByUsername(username).ifPresent(topic::setUser);

        topicRepository.save(topic);

        return topic.getId();
    }

    public List<QuestionDto> generateQuestions(Principal principal, String id, int count) {

        User user = null;
        if (principal != null) {
            Optional<User> optionalUser = userService.findByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            }
        }
        Optional<Topic> topic = topicRepository.findById(Long.parseLong(id));
        if (topic.isEmpty()) {
            return List.of();
        }
        if (topic.get().getUser() != null && !topic.get().getUser().equals(user)) {
            return List.of();
        }

        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, String> map = new HashMap<>();
        map.put("text", topic.get().getContent());
        map.put("question_count", String.valueOf(count));

        QuestionsDto questions = restTemplate.postForObject("http://127.0.0.1:5000/generate-questions", map, QuestionsDto.class);

        return questions != null ? questions.getQuiz() : null;

    }

    public Optional<Topic> findById(Long topicId) {
        return topicRepository.findById(topicId);
    }

    public void addQuestion(Topic topic, Question q) {
        topic.addQuestion(q);
        topicRepository.save(topic);
    }
}
