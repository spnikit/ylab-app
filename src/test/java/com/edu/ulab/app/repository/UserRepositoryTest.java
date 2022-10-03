package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить пользователя. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getAge()).isEqualTo(111);
        assertSelectCount(0);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


    @DisplayName("Обновить пользователя. Число update должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        // Given
        Person person = new Person();
        person.setId(1001L);
        person.setFullName("updated user");
        person.setTitle("updated title");
        person.setAge(666);


        // When
        Person updatedPerson = userRepository.save(person);

        // Then
        assertThat(updatedPerson.getFullName()).isEqualTo("updated user");
        assertThat(updatedPerson.getTitle()).isEqualTo("updated title");
        assertThat(updatedPerson.getAge()).isEqualTo(666);

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }


    // failed update
    @DisplayName("Обновить пользователя значением null. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePersonWithNull(){
        // when
        Throwable throwable = catchThrowable(() -> userRepository.save(null));

        // then
        assertThat(throwable)
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    // get
    @DisplayName("Получить пользователя. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_thenAssertDmlCount() {

        // When
        Person person = userRepository.findById(1001L).orElseThrow();

        // Then
        assertThat(person.getFullName()).isEqualTo("default user");
        assertThat(person.getTitle()).isEqualTo("reader");
        assertThat(person.getAge()).isEqualTo(55);

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    // failed get
    @DisplayName("Получить пользователя с id, который равен null. Должно выбросить ошибку")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPersonWithIdThanDoesntExist(){

        // when
        Throwable throwable = catchThrowable(() -> userRepository.findById(null));

        // then
        assertThat(throwable)
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasMessageContaining("id");
    }


    // delete
    @DisplayName("Удалить пользователя. Число delete должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {


        // When
        userRepository.deleteById(1001L);

        // Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


    // failed delete
    @DisplayName("Удалить пользователя с id, которого не существует. Должно выбросить ошибку")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePersonWithIdThatDoesntExist() {

        // When
        Throwable throwable = catchThrowable(() -> userRepository.deleteById(666L));
        // Then
        assertThat(throwable).isInstanceOf(EmptyResultDataAccessException.class);
    }


}
