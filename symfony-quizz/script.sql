CREATE TRIGGER `prj_play_delete` BEFORE DELETE ON `game`
 FOR EACH ROW UPDATE stats
SET game_tot = IF(OLD.state = 1, game_tot - 1, game_tot),
game_win = IF(OLD.state = 2, 
                  IF(OLD.winner_id IS NOT null, 
                     IF(OLD.winner_id = user_id,
                        game_win - 1, 
                        game_win),
                     game_win), 
                  game_win)
WHERE user_id = OLD.user_A_id OR user_id = OLD.user_B_id

CREATE TRIGGER `prj_user` AFTER INSERT ON `user`
 FOR EACH ROW INSERT INTO stats (user_id)
VALUES (NEW.id)

CREATE TRIGGER `prj_play_update` AFTER UPDATE ON `game`
 FOR EACH ROW UPDATE stats
SET game_tot = IF(OLD.state = 0, IF(NEW.state = 1, game_tot + 1, game_tot), game_tot),
game_win = IF(NEW.state = 2, 
                  IF(NEW.winner_id IS NOT null, 
                     IF(NEW.winner_id = user_id,
                        game_win + 1, 
                        game_win),
                     game_win), 
                  game_win)
WHERE user_id = NEW.user_A_id OR user_id = NEW.user_B_id