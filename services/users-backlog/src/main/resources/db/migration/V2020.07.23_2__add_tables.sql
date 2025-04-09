-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema users-backlog
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table `users-backlog`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`innovator`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`innovator` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `email_address` VARCHAR(225) NOT NULL,
  `display_name` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_address_UNIQUE` (`email_address` ASC),
  UNIQUE INDEX `display_name_UNIQUE` (`display_name` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`idea`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`idea` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `summary` VARCHAR(255) NOT NULL,
  `description` VARCHAR(225) NULL DEFAULT NULL,
  `innovator_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `summary_UNIQUE` (`summary` ASC),
  INDEX `idea_innovator_fk_idx` (`innovator_id` ASC),
  CONSTRAINT `idea_fk1`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`idea_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`idea_category` (
  `idea_id` INT(11) NOT NULL,
  `category_id` INT(11) NOT NULL,
  PRIMARY KEY (`idea_id`, `category_id`),
  INDEX `idea_category_fk2_idx` (`category_id` ASC),
  CONSTRAINT `idea_category_fk1`
    FOREIGN KEY (`idea_id`)
    REFERENCES `users-backlog`.`idea` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `idea_category_fk2`
    FOREIGN KEY (`category_id`)
    REFERENCES `users-backlog`.`category` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`implementation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`implementation` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(225) NOT NULL,
  `description` VARCHAR(225) NULL DEFAULT NULL,
  `innovator_id` INT(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `implementation_fk1_idx` (`innovator_id` ASC),
  CONSTRAINT `implementation_fk1`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 11
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`idea_implementation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`idea_implementation` (
  `implementation_id` INT(11) NOT NULL,
  `idea_id` INT(11) NOT NULL,
  PRIMARY KEY (`implementation_id`, `idea_id`),
  INDEX `idea_implementation_fk1_idx` (`idea_id` ASC),
  CONSTRAINT `idea_implementation_fk1`
    FOREIGN KEY (`idea_id`)
    REFERENCES `users-backlog`.`idea` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `idea_implementation_fk2`
    FOREIGN KEY (`implementation_id`)
    REFERENCES `users-backlog`.`implementation` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`implementation_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`implementation_category` (
  `implementation_id` INT(11) NOT NULL,
  `category_id` INT(11) NOT NULL,
  PRIMARY KEY (`implementation_id`, `category_id`),
  INDEX `implementation_category_fk2_idx` (`category_id` ASC),
  CONSTRAINT `implementation_category_fk1`
    FOREIGN KEY (`implementation_id`)
    REFERENCES `users-backlog`.`implementation` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `implementation_category_fk2`
    FOREIGN KEY (`category_id`)
    REFERENCES `users-backlog`.`category` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`implementation_recommendation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`implementation_recommendation` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `implementation_id` INT(11) NOT NULL,
  `innovator_id` INT(11) NOT NULL,
  `date_time_created` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `date_time_modified` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `message` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `implementation_recommendation_fk1_idx` (`implementation_id` ASC),
  INDEX `implementation_recommendation_fk2_idx` (`innovator_id` ASC),
  CONSTRAINT `implementation_recommendation_fk1`
    FOREIGN KEY (`implementation_id`)
    REFERENCES `users-backlog`.`implementation` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `implementation_recommendation_fk2`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 6
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`implementation_recommendation_reply`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`implementation_recommendation_reply` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `innovator_id` INT(11) NOT NULL,
  `implementation_recommendation_id` INT(11) NOT NULL,
  `date_time_modified` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `date_time_created` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `message` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `implementation_recommendation_reply_idx` (`implementation_recommendation_id` ASC),
  INDEX `implementation_recommendation_reply_fk2_idx` (`innovator_id` ASC),
  CONSTRAINT `implementation_recommendation_reply_fk1`
    FOREIGN KEY (`implementation_recommendation_id`)
    REFERENCES `users-backlog`.`implementation_recommendation` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `implementation_recommendation_reply_fk2`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`implementation_recommendation_vote`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`implementation_recommendation_vote` (
  `implementation_recommendation_id` INT(11) NOT NULL,
  `innovator_id` INT(11) NOT NULL,
  `vote` INT(11) NOT NULL,
  PRIMARY KEY (`implementation_recommendation_id`, `innovator_id`),
  INDEX `implementation_recommendation_vote_fk2_idx` (`innovator_id` ASC),
  CONSTRAINT `implementation_recommendation_vote_fk1`
    FOREIGN KEY (`implementation_recommendation_id`)
    REFERENCES `users-backlog`.`implementation_recommendation` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `implementation_recommendation_vote_fk2`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`implementation_vote`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`implementation_vote` (
  `implementation_id` INT(11) NOT NULL,
  `innovator_id` INT(11) NOT NULL,
  `vote` INT(11) NOT NULL,
  PRIMARY KEY (`implementation_id`, `innovator_id`),
  INDEX `implementation_vote_fk2_idx` (`innovator_id` ASC),
  CONSTRAINT `implementation_vote_fk1`
    FOREIGN KEY (`implementation_id`)
    REFERENCES `users-backlog`.`implementation` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `implementation_vote_fk2`
    FOREIGN KEY (`innovator_id`)
    REFERENCES `users-backlog`.`innovator` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


-- -----------------------------------------------------
-- Table `users-backlog`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users-backlog`.`product` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `implementation_id` INT(11) NOT NULL,
  `description` VARCHAR(225) NULL DEFAULT NULL,
  `url` VARCHAR(225) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
