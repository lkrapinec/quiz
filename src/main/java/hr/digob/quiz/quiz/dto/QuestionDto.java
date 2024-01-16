package hr.digob.quiz.quiz.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private String question;
    private List<AnswerDto> answers;
}
