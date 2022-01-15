package com.game.service;


import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    private final long minDate = new GregorianCalendar(2000, Calendar.JANUARY,1).getTimeInMillis();
    private final long maxDate = new GregorianCalendar(3000, Calendar.JANUARY,1).getTimeInMillis();

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    private List<Player> getAll(){
        return playerRepository.findAll();
    }

    public List<Player> getFilteredPlayers(Map<String,String> params) {
        return (new CustomPlayerSearch(params)).filterPlayer(getAll());
    }
    private List<Player> getSortedPlayers(Map<String,String> params) {
        List<Player> players = getFilteredPlayers(params);
        if (params.containsKey("order")) {
            PlayerOrder order = PlayerOrder.valueOf(params.get("order"));
            sortPlayers(players, order);
        } else {
            sortPlayers(players, PlayerOrder.ID);
        }
        return players;
    }

    public List<Player> getPlayersPage(Map<String,String> params){
        List<Player> players = getSortedPlayers(params);
        return createPage(params, players);
    }

    private List<Player> createPage(Map<String,String> params, List<Player> players) {
        List<Player> playersPage = new ArrayList<>();
        int pageNumber = 0;
        int pageSize = 3;
        if (params.containsKey("pageNumber")) {
            pageNumber = Integer.parseInt(params.get("pageNumber"));
        }
        if (params.containsKey("pageSize")) {
            pageSize = Integer.parseInt(params.get("pageSize"));
        }
        int playersCount = players.size();
        int pages = playersCount/pageSize;
        int first = pageSize*pageNumber ;
        int last = pageSize*(pageNumber+1);
        if (pageNumber == pages) {
            last = playersCount;
        }
        for (int i = first; i < last; i++) {
            playersPage.add(players.get(i));
        }
        return playersPage;
    }


    private void sortPlayers (List<Player> players, PlayerOrder order ){
        if (order.equals(PlayerOrder.ID)) {
            players.sort(Comparator.comparing(Player::getId));
            return;
        }
        if (order.equals(PlayerOrder.NAME)) {
            players.sort(Comparator.comparing(Player::getName));
            return;
        }
        if (order.equals(PlayerOrder.EXPERIENCE)) {
            players.sort(Comparator.comparing(Player::getExperience));
            return;
        }
        if (order.equals(PlayerOrder.BIRTHDAY)) {
            players.sort(Comparator.comparing(Player::getBirthday));
            return;
        }
        if (order.equals(PlayerOrder.LEVEL)) {
            players.sort(Comparator.comparing(Player::getLevel));
        }
    }

    public Player getByID(String stringId) throws NoSuchElementException, IllegalArgumentException {
        Long id = idConverter(stringId);
        Optional<Player> optional = playerRepository.findById(id);
        return optional.get();
    }

    private Long idConverter(String stringId) throws NumberFormatException{
        Long id = Long.parseLong(stringId);
        if (id <= 0) {
            throw new NumberFormatException();
        }
        return id;
    }

    public Player createPlayer(Map<String,String> params) throws IllegalArgumentException{
        Player player = new Player();
        if (!(params.containsKey("name")&&params.containsKey("title")
                &&params.containsKey("race")&&params.containsKey("profession")
                &&params.containsKey("birthday")&&params.containsKey("experience"))) {
            throw new IllegalArgumentException();
        }
        String name = params.get("name");
        String title = params.get("title");
        String race = params.get("race").toUpperCase();
        String profession = params.get("profession").toUpperCase();
        Long birthday = Long.parseLong(params.get("birthday"));
        Integer experience = Integer.parseInt(params.get("experience"));
        Boolean banned = false;
        if (params.get("banned")!= null) {
            banned = (params.get("banned").equals("true"));
        }
        if (name.length()>12 || name.equals("") || title.length()>30 || experience > 10_000_000
                || experience < 0|| birthday < minDate || birthday > maxDate || birthday < 0) {
            throw new IllegalArgumentException();
        }
        player.setName(name);
        player.setTitle(title);
        player.setRace(Race.valueOf(race));
        player.setProfession(Profession.valueOf(profession));
        player.setBirthday(birthday);
        player.setBanned(banned);
        player.setExperience(experience);
        levelUpCheck(player,experience);
        playerRepository.save(player);
        playerRepository.flush();
        return player;
    }

    private void levelUpCheck (Player player, Integer experience) {
        int level = ((int)Math.sqrt((2500+200*experience)) - 50)/100;
        int untilNextLevel = 50*(level+1)*(level+2) - experience;
        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevel);
    }

    public Player updatePlayer(String stringId, Map<String,String> params) throws
            NoSuchElementException, IllegalArgumentException{
        Player player = getByID(stringId);
        if (params.containsKey("name")) {
            String name = params.get("name");
            if (name.length()>12 || name.equals("")) {
                throw new IllegalArgumentException();
            }
            player.setName(name);
        }
        if (params.containsKey("title")) {
            String title = params.get("title");
            if (title.length()>30) {
                throw new IllegalArgumentException();
            }
            player.setTitle(title);
        }
        if (params.containsKey("race")) {
            player.setRace(Race.valueOf(params.get("race")));
        }
        if (params.containsKey("profession")) {
            player.setProfession(Profession.valueOf(params.get("profession")));
        }
        if (params.containsKey("birthday")) {
            Long birthday = Long.parseLong(params.get("birthday"));
            if (birthday < minDate || birthday > maxDate || birthday < 0) {
                throw new IllegalArgumentException();
            }
            player.setBirthday(birthday);
        }
        if (params.containsKey("experience")) {
            Integer experience = Integer.parseInt(params.get("experience"));
            if(experience > 10_000_000 || experience < 0){
                throw new IllegalArgumentException();
            }
            player.setExperience(experience);
            levelUpCheck(player,experience);
        }
        if (params.containsKey("banned")) {
            player.setBanned(params.get("banned").equals("true"));
        }
        playerRepository.saveAndFlush(player);
        return player;
    }

    public void deletePlayer(String stringId) throws NoSuchElementException, IllegalArgumentException {
        Long id = idConverter(stringId);
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
        } else {
            throw new NoSuchElementException();
        }
    }

    private class CustomPlayerSearch {
        private String name = null;
        private String title = null;
        private Race race = null;
        private Profession profession = null;
        private Long after = null;
        private Long before = null;
        private Boolean banned = null;
        private Integer minExperience = null;
        private Integer maxExperience = null;
        private Integer minLevel = null;
        private Integer maxLevel = null;

        private CustomPlayerSearch(Map<String,String> params) {
            this.parseParams(params);
        }
        private List<Player> filterPlayer (List<Player> players) {
            List<Player> filteredPlayers = new ArrayList<>();
            for (Player player : players) {
                if (checkFilters(player)) {
                    filteredPlayers.add(player);
                }
            }
            return filteredPlayers;
        }

        private boolean checkFilters(Player player) {
            if (name != null) {
                if (!player.getName().matches(returnRegex(name))) {
                    return false;
                }
            }
            if (title != null) {
                if (!player.getTitle().matches(returnRegex(title))) {
                    return false;
                }
            }
            if (race != null) {
                if (!player.getRace().equals(race)) {
                    return false;
                }
            }
            if (profession != null) {
                if (!player.getProfession().equals(profession)) {
                    return false;
                }
            }
            if (after != null) {
                if (player.getBirthday() < after) {
                    return false;
                }
            }
            if (before != null) {
                if (player.getBirthday() > before) {
                    return false;
                }
            }
            if (banned != null) {
                if (player.getBanned() != banned) {
                    return false;
                }
            }
            if (minExperience !=null) {
                if (player.getExperience() < minExperience) {
                    return false;
                }
            }
            if (maxExperience!=null) {
                if (player.getExperience() > maxExperience) {
                    return false;
                }
            }
            if (minLevel !=null) {
                if (player.getLevel() < minLevel) {
                    return false;
                }
            }
            if (maxLevel!=null) {
                return player.getLevel() <= maxLevel;
            }
            return true;
        }

        private String returnRegex(String parameter) {
            return ".*" + parameter + ".*";
        }



        private void parseParams (Map<String,String> params) {
            if (params.containsKey("name")) {
                name = params.get("name");
            }
            if (params.containsKey("title")) {
                title = params.get("title");
            }
            if (params.containsKey("race")) {
                race = Race.valueOf(params.get("race"));
            }
            if (params.containsKey("profession")) {
                profession = Profession.valueOf(params.get("profession"));
            }
            if (params.containsKey("after")) {
                after = Long.parseLong(params.get("after"));
            }
            if (params.containsKey("before")) {
                before = Long.parseLong(params.get("before"));
            }
            if (params.containsKey("banned")) {
                banned = params.get("banned").equals("true");
            }
            if (params.containsKey("minExperience")) {
                minExperience = Integer.parseInt(params.get("minExperience"));
            }
            if (params.containsKey("maxExperience")) {
                maxExperience = Integer.parseInt(params.get("maxExperience"));
            }
            if (params.containsKey("minLevel")) {
                minLevel = Integer.parseInt(params.get("minLevel"));
            }
            if (params.containsKey("maxLevel")) {
                maxLevel = Integer.parseInt(params.get("maxLevel"));
            }
        }
    }
}

