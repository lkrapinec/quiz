package hr.digob.quiz.quiz;

import hr.digob.quiz.quiz.entity.Topic;
import hr.digob.quiz.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Query("SELECT t FROM Topic t WHERE t.user = ?1")
    List<Topic> findAllByUser(User user);

    @Query("SELECT t FROM Topic t WHERE t.user is null")
    List<Topic> findAllNonUser();
}
