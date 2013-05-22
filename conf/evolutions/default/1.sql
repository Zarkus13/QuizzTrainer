# --- First database schema

# --- !Ups

CREATE TABLE questions (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  text longtext NOT NULL,
  correct_answers varchar(15) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE answers (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  letter VARCHAR(1) NOT NULL,
  text mediumtext NOT NULL,
  question_fk bigint(20) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (question_fk) REFERENCES questions(id)
);

# --- !Downs

DROP TABLE 'answers';
DROP TABLE 'questions';