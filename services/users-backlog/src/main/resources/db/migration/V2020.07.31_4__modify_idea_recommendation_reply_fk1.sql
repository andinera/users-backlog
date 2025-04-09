SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';


ALTER TABLE `users-backlog`.`idea_recommendation_reply` 
DROP FOREIGN KEY `idea_recommendation_reply_fk1`;
ALTER TABLE `users-backlog`.`idea_recommendation_reply` 
ADD INDEX `idea_recommendation_reply_fk1_idx` (`idea_recommendation_id` ASC) VISIBLE,
DROP INDEX `idea_recommendation_reply_idx` ;
;
ALTER TABLE `users-backlog`.`idea_recommendation_reply` 
ADD CONSTRAINT `idea_recommendation_reply_fk1`
  FOREIGN KEY (`idea_recommendation_id`)
  REFERENCES `users-backlog`.`idea_recommendation` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
