SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


-- -----------------------------------------------------
-- Table `users-backlog`.`idea_recommendation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`idea_recommendation` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `idea_id` INT(11) NOT NULL,
  `innovator_id` INT(11) NOT NULL,
  `date_time_created` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `date_time_modified` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `message` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idea_recommendation_fk1_idx` (`idea_id` ASC),
  INDEX `idea_recommendation_fk2_idx` (`innovator_id` ASC),
  CONSTRAINT `idea_recommendation_fk1`
    FOREIGN KEY (`idea_id`)
    REFERENCES `users-backlog`.`idea` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `idea_recommendation_fk2`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`idea_recommendation_reply`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`idea_recommendation_reply` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `innovator_id` INT(11) NOT NULL,
  `idea_recommendation_id` INT(11) NOT NULL,
  `date_time_modified` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `date_time_created` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `message` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idea_recommendation_reply_idx` (`idea_recommendation_id` ASC),
  INDEX `idea_recommendation_reply_fk2_idx` (`innovator_id` ASC),
  CONSTRAINT `idea_recommendation_reply_fk1`
    FOREIGN KEY (`idea_recommendation_id`)
    REFERENCES `users-backlog`.`iidea_recommendation` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `idea_recommendation_reply_fk2`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`idea_recommendation_vote`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`idea_recommendation_vote` (
  `idea_recommendation_id` INT(11) NOT NULL,
  `innovator_id` INT(11) NOT NULL,
  `vote` INT(11) NOT NULL,
  PRIMARY KEY (`idea_recommendation_id`, `innovator_id`),
  INDEX `idea_recommendation_vote_fk2_idx` (`innovator_id` ASC),
  CONSTRAINT `idea_recommendation_vote_fk1`
    FOREIGN KEY (`idea_recommendation_id`)
    REFERENCES `users-backlog`.`idea_recommendation` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `idea_recommendation_vote_fk2`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`idea_vote`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`idea_vote` (
  `idea_id` INT(11) NOT NULL,
  `innovator_id` INT(11) NOT NULL,
  `vote` INT(11) NOT NULL,
  PRIMARY KEY (`idea_id`, `innovator_id`),
  INDEX `idea_vote_fk2_idx` (`innovator_id` ASC),
  CONSTRAINT `idea_vote_fk1`
    FOREIGN KEY (`idea_id`)
    REFERENCES `users-backlog`.`idea` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `idea_vote_fk2`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
