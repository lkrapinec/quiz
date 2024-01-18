package hr.digob.quiz.quiz;

import hr.digob.quiz.quiz.dto.QuestionDto;
import lombok.Data;

@Data
public class NewQuestionDto {
    private Integer topicId;
    private QuestionDto question;
}
