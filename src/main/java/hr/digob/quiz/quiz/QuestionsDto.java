package hr.digob.quiz.quiz;

import hr.digob.quiz.quiz.dto.QuestionDto;
import lombok.Data;

import java.util.List;

@Data
public class QuestionsDto {
    List<QuestionDto> quiz;

}
