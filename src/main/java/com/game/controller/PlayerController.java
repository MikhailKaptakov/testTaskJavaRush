package com.game.controller;

import com.game.controller.exceptions.BadRequestException;
import com.game.entity.Player;
import com.game.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping ( "/players")
    public List<Player> getPlayersList(@RequestParam Map<String,String> params) {
        return playerService.getPlayersPage(params);
    }

    @GetMapping("/players/count")
    public Integer getCountofPlayersList(@RequestParam Map<String,String> params) {
        return playerService.getFilteredPlayers(params).size();
    }

    @PostMapping("/players")
    public Player createNewPlayer(@RequestBody Map<String,String> params) {
        Player player;
        try {
            player = playerService.createPlayer(params);
        } catch (IllegalArgumentException notValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return player;
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayerFromID(@PathVariable("id") String id) {
        try {
            Player player = playerService.getByID(id);
            return ResponseEntity.status(HttpStatus.OK).body(player);
        } catch (IllegalArgumentException notValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException notFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@PathVariable("id") String id, @RequestBody Map<String,String> params) {
        Player player;
        try {
            player = playerService.updatePlayer(id,params);
        } catch (IllegalArgumentException notValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException notFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return player;
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable("id") String id) {
        try {
            playerService.deletePlayer(id);
        } catch (IllegalArgumentException notValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException notFound) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}


