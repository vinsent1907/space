package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.Date;
import java.util.List;

public interface ShipService {

    /**
     * Сохраняет корабль
     * @param ship - корабль для сохранения
     * @return
     */
    Ship saveShip(Ship ship);

    /**
     * Возвращает корабль по его ID
     * @param id  ID корабля
     * @return - объект клиента с заданным ID
     */
    Ship getShip(Long id);

    /**
     * Обновляет корабль
     * @param newShip -  новый корабль
     * @param oldShip - старый корабль
     */
    Ship updateShip(Ship oldShip, Ship newShip) throws IllegalArgumentException;

    /**
     * Удаляет корабль
     * //@param id - id корабля, которого нужно удалить
     * //@return - true если корабль был удален, иначе false
     */
    void deleteShip(Ship ship);
    /**
     * Возвращает список кораблей с заданными необязательными параметрами:
     * @param   name  название корабля
     * @param   planet планета производства корабля
     * @param   shipType тип корабля
     * @param   after время производсва корабля
     * @param   before время производсвакорабля
     * @param   isUsed корабля
     * @param   minSpeed корабля
     * @param   maxSpeed корабля
     * @param   minCrewSize корабля
     * @param   maxCrewSize корабля
     * @param   minRating корабля
     * @param   maxRating корабля
     * @return - Возвращает List<Ship>
     */
    List<Ship> getShips(
            String name,
            String planet,
            ShipType shipType,
            Long after,
            Long before,
            Boolean isUsed,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating
    );


    List<Ship> sortShips(List<Ship> ships, ShipOrder order);

    List<Ship> getPage(List<Ship> ships, Integer pageNumber, Integer pageSize);


    boolean isValidShip(Ship ship);

    double calculateRating(double speed, boolean isUsed, Date prod);









}
