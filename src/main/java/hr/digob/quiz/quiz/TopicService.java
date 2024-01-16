package hr.digob.quiz.quiz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.digob.quiz.quiz.dto.QuestionDto;
import hr.digob.quiz.quiz.entity.Topic;
import hr.digob.quiz.user.User;
import hr.digob.quiz.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
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

    public Long save(String username, TitleAndDescription titleAndDescription) {
        String title = titleAndDescription.getTitle();
        String description = titleAndDescription.getDescription();
        Topic topic = new Topic(title, description);
        userService.findByUsername(username).ifPresent(topic::setUser);

        topicRepository.save(topic);

        return topic.getId();
    }

    public List<QuestionDto> generateQuestions(Principal principal, String id) {
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

        final String generatedQuestions = """
                      {
                         "quiz":[
                            {
                               "question":"Koje godine je prvi put objavljena Spring Framework verzija 1.0?",
                               "answers":[
                                  {
                                     "text":"2004",
                                     "correct":true
                                  },
                                  {
                                     "text":"2003",
                                     "correct":false
                                  },
                                  {
                                     "text":"2006",
                                     "correct":false
                                  }
                               ]
                            },
                            {
                               "question":"Tko je autor Spring Frameworka, objavljenog uz njegovu knjigu 'Expert One-on-One J2EE Design and Development'?",
                               "answers":[
                                  {
                                     "text":"Rod Johnson",
                                     "correct":true
                                  },
                                  {
                                     "text":"James Gosling",
                                     "correct":false
                                  },
                                  {
                                     "text":"Martin Fowler",
                                     "correct":false
                                  }
                               ]
                            },
                            {
                               "question":"Pod kojom licencom je prvi put objavljen Spring Framework?",
                               "answers":[
                                  {
                                     "text":"Apache 2.0",
                                     "correct":true
                                  },
                                  {
                                     "text":"MIT",
                                     "correct":false
                                  },
                                  {
                                     "text":"GPL",
                                     "correct":false
                                  }
                               ]
                            },
                            {
                               "question":"Koja verzija Spring Frameworka je uvela podršku za Java SE 8 i Groovy 2?",
                               "answers":[
                                  {
                                     "text":"4.0",
                                     "correct":true
                                  },
                                  {
                                     "text":"3.0",
                                     "correct":false
                                  },
                                  {
                                     "text":"5.0",
                                     "correct":false
                                  }
                               ]
                            },
                            {
                               "question":"Koja je glavna značajka Spring Frameworka 5.0?",
                               "answers":[
                                  {
                                     "text":"Podrška za Reactive Streams kompatibilni Reactor Core",
                                     "correct":true
                                  },
                                  {
                                     "text":"Podrška za WebSocket",
                                     "correct":false
                                  },
                                  {
                                     "text":"Integracija s Hibernate",
                                     "correct":false
                                  }
                               ]
                            },
                            {
                               "question":"Što predstavlja 'Spring Core Container' u Spring Frameworku?",
                               "answers":[
                                  {
                                     "text":"Osnovni modul Springa",
                                     "correct":true
                                  },
                                  {
                                     "text":"Modul za web aplikacije",
                                     "correct":false
                                  },
                                  {
                                     "text":"Modul za sigurnost",
                                     "correct":false
                                  }
                               ]
                            },
                            {
                               "question":"Koja Spring Framework verzija uključuje 'Aspect-oriented programming' mogućnosti?",
                               "answers":[
                                  {
                                     "text":"1.2",
                                     "correct":true
                                  },
                                  {
                                     "text":"2.0",
                                     "correct":false
                                  },
                                  {
                                     "text":"3.0",
                                     "correct":false
                                  }
                               ]
                            },
                            {
                               "question":"Koja verzija Spring Frameworka pruža podršku za Jakarta EE 9+?",
                               "answers":[
                                  {
                                     "text":"6.0",
                                     "correct":true
                                  },
                                  {
                                     "text":"5.0",
                                     "correct":false
                                  },
                                  {
                                     "text":"4.0",
                                     "correct":false
                                  }
                               ]
                            },
                            {
                               "question":"Koja verzija Spring Frameworka je prva uvela 'Spring Boot'?",
                               "answers":[
                                  {
                                     "text":"4.0",
                                     "correct":true
                                  },
                                  {
                                     "text":"3.0",
                                     "correct":false
                                  },
                                  {
                                     "text":"2.0",
                                     "correct":false
                                  }
                               ]
                            }
                         ]
                      }
                """;
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(generatedQuestions, QuestionsDto.class).getQuiz();
        } catch (JsonProcessingException e) {
            return List.of();
        }

    }
}
