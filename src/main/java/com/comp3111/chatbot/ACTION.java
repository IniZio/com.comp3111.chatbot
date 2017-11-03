package com.comp3111.chatbot;

public interface ACTION {
    String EXIT_MAIN = "exit_to_main";
    String PEOPLE_INPUT = "people_input"; // user going to input people to search
    String PEOPLE_SEARCH = "people_search"; // server going to search
    String ROOM_INPUT = "room_input";
    String ROOM_SEARCH = "room_search";
    String OPENINGHOUR_CHOOSE = "openinghour_choose";
    String OPENINGHOUR_SEARCH = "openinghour_search";
    String BUS_CHOOSE_BUS = "bus_choose_bus";
    String BUS_CHOOSE_DEST = "bus_choose_dest";
    String BUS_SEARCH = "bus_search";
}
